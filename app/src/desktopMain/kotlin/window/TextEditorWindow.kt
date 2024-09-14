package window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import dialogs.*
import kotlinx.coroutines.launch
import providers.LocalResources
import javax.swing.JOptionPane

/**
 * Главное окно текстового редактора
 *
 * @param state Состояние окна текстового редактора
 */
@Composable
fun TextEditorWindow(state: TextEditorWindowState) {
    // Устанавливаем скоуп для обработки асинхронщины
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }

    // Сетапим главное окно приложения
    Window(
        state = state.window,
        title = titleOf(state),
        icon = LocalResources.current.icon,
        onCloseRequest = { exit() }
    ) {
        // Запускаем стейт при запуске приложения
        LaunchedEffect(Unit) { state.run() }

        // Вызываем обработчики уведомлений и меню
        WindowNotifications(state)
        WindowMenuBar(state)
        Column {
            BasicTextField(
                value = state.text,
                onValueChange = { newValue ->
                    state.text = newValue // Обновляем содержимое текста
                    state.clearHighlight() // Очищаем подсветку при изменении текста
                },
                enabled = state.isInit,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = HighlightTransformation(state.text, state.highlightedIndices, state.searchQuery.length)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val scope = rememberCoroutineScope()

            when {
                state.openDialog.isAwaiting -> {
                    FileDialog(
                        title = "Текстовый редактор",
                        isLoad = true,
                        onResult = { state.openDialog.onResult(it) }
                    )
                }

                state.saveDialog.isAwaiting -> {
                    FileDialog(
                        title = "Текстовый редактор",
                        isLoad = false,
                        onResult = { state.saveDialog.onResult(it) }
                    )
                }

                state.exitDialog.isAwaiting -> {
                    YesNoCancelDialog(
                        title = "Текстовый редактор",
                        message = "Сохранить изменения?",
                        onResult = { state.exitDialog.onResult(it) }
                    )
                }

                state.searchDialog.isAwaiting -> {
                    SearchDialog(
                        onSearch = { query ->
                            scope.launch {
                                state.search(query)
                                state.searchDialog.onResult(Unit) // Закрываем диалог поиска
                                if (state.searchResults.isNotEmpty()) {
                                    state.clearHighlight()

                                    state.replaceDialog.awaitResult() // Открываем диалог замены, если найдены совпадения
                                } else {
                                    JOptionPane.showMessageDialog(null, "Совпадения не найдены")
                                }
                            }
                        },
                        onCancel = { state.searchDialog.onResult(Unit) }
                    )
                }

                state.replaceDialog.isAwaiting -> {
                    ReplaceDialog(
                        foundCount = state.searchResults.size,
                        onReplace = { replaceWith, replaceAll ->
                            scope.launch {
                                state.replace(replaceWith, replaceAll)
                                state.replaceDialog.onResult(Unit) // Закрываем диалог замены
                            }
                        },
                        onCancel = { state.replaceDialog.onResult(Unit) }
                    )
                }

                state.searchHighlightDialog.isAwaiting -> {
                    HighlightSearchDialog(
                        onSearch = { query, ignoreCase ->
                            scope.launch {
                                state.searchAndHighlight(query, ignoreCase)
                                // Показываем информационное окно с количеством совпадений
                                state.showMatchCountDialog()
                                state.searchHighlightDialog.onResult(Unit) // Закрываем диалог
                            }
                        },
                        onCancel = { state.searchHighlightDialog.onResult(Unit) }
                    )
                }

                state.isMatchCountDialogVisible -> {
                    AlertDialog(
                        onDismissRequest = { state.dismissMatchCountDialog() },
                        title = { Text("Результаты поиска") },
                        text = { Text("Найдено совпадений: ${state.highlightedIndices.size}") },
                        confirmButton = {
                            Button(onClick = { state.dismissMatchCountDialog() }) {
                                Text("ОК")
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Генератор названия файла. Если он в процессе изменения - помечаем звездочкой
 *
 * @param state Состояние окна текстового редактора.
 * @return Название окна.
 */
private fun titleOf(state: TextEditorWindowState): String {
    val changeMark = if (state.isChanged) "*" else ""
    val filePath = state.path ?: "Неизвестный файл"
    return "$changeMark$filePath - Текстовый редактор"
}

/**
 * Здесь слушаем уведомления от нашего state и показываем их через sendNotification
 *
 * @param state Состояние окна текстового редактора.
 */
@Composable
private fun WindowNotifications(state: TextEditorWindowState) {
    // Не забываем отформатировать уведомления в зависимости от их типа
    fun TextEditorWindowNotification.format() = when (this) {
        is TextEditorWindowNotification.SaveSuccess -> Notification(
            "Файл сохранен", path.toString(), Notification.Type.Info
        )
        is TextEditorWindowNotification.SaveError -> Notification(
            "Файл не сохранен", path.toString(), Notification.Type.Error
        )
    }

    // Собираем уведомления вместе, отправляем
    LaunchedEffect(Unit) {
        state.notifications.collect {
            state.sendNotification(it.format())
        }
    }
}

/**
 * Добавляем в наше окно меню работы с файлом
 *
 * @param state Состояние окна текстового редактора.
 */
@Composable
private fun FrameWindowScope.WindowMenuBar(state: TextEditorWindowState) = MenuBar {
    val scope = rememberCoroutineScope()

    fun save() = scope.launch { state.save() }
    fun open() = scope.launch { state.open() }
    fun exit() = scope.launch { state.exit() }
    fun searchAndReplace() = scope.launch { state.searchAndReplace() }
    fun searchAndHighlight() = scope.launch { state.searchAndHighlight() }

    Menu("Файл") {
        Item("Новое окно", onClick = state::newWindow)
        Item("Открыть...", onClick = { open() })
        Item("Сохранить", onClick = { save() }, enabled = state.isChanged || state.path == null)
        Separator()
        Item("Выход", onClick = { exit() })
    }

    Menu("Редактирование файла") {
        Item("Найти", onClick = { searchAndHighlight() })
        Item("Найти и заменить", onClick = { searchAndReplace() })
    }
}