package window

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import dialogs.FileDialog
import dialogs.YesNoCancelDialog
import kotlinx.coroutines.launch
import providers.LocalResources

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

        // Этот composable используется для показа базового поля редактора текста
        BasicTextField(
            state.text,
            state::text::set,
            enabled = state.isInit,
            modifier = Modifier.fillMaxSize()
        )


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

    Menu("Файл") {
        Item("Новое окно", onClick = state::newWindow)
        Item("Открыть...", onClick = { open() })
        Item("Сохранить", onClick = { save() }, enabled = state.isChanged || state.path == null)
        Separator()
        Item("Выход", onClick = { exit() })
    }
}