package dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path
import javax.swing.JOptionPane

/**
 * Enum показывающий возможные значения для диалогового окна
 */
enum class DialogResult {
    Yes, No, Cancel
}

/**
 * Компонент для отображения диалогового окна выбора файла.
 *
 * @param title Заголовок диалогового окна.
 * @param isLoad Если true, диалог используется для загрузки файлов, иначе для сохранения.
 * @param onResult Коллбэк для обработки выбранного пути к файлу.
 */
@Composable
fun FrameWindowScope.FileDialog(
    title: String,
    isLoad: Boolean,
    onResult: (result: Path?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(window, "Выберите файл", if (isLoad) LOAD else SAVE) {
            override fun setVisible(value: Boolean) {
                // Показываем диалоговое окно
                super.setVisible(value)

                // Если юзер выбрал файл, то передаем его в коллбэк
                if (value) {
                    if (file != null) {
                        if (!file.endsWith(".txt")) file += ".txt"

                        onResult(File(directory).resolve(file).toPath())
                    } else {
                        onResult(null)
                    }
                }
            }
        }.apply {
            // Устанавливаем заголовок
            this.title = title
        }
    },
    dispose = FileDialog::dispose // Не забываем уничтожить компонент, чтобы освободить память
)

/**
 * Компонент для отображения диалогового окна с вариантами "Да", "Нет" и "Отмена".
 *
 * @param title Заголовок диалогового окна.
 * @param message Сообщение, отображаемое в диалоговом окне.
 * @param onResult Коллбэк для обработки результата диалогового окна.
 */
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WindowScope.YesNoCancelDialog(
    title: String,
    message: String,
    onResult: (result: DialogResult) -> Unit
) {
    DisposableEffect(Unit) {
        // Запускаем корутину для показа окна
        val job = GlobalScope.launch(Dispatchers.Swing) {
            // Здесь мы показываем непосредственно диалоговое окно
            val resultInt = JOptionPane.showConfirmDialog(
                window, message, title, JOptionPane.YES_NO_CANCEL_OPTION
            )
            // Преобразовываем результат в enum
            val result = when (resultInt) {
                JOptionPane.YES_OPTION -> DialogResult.Yes
                JOptionPane.NO_OPTION -> DialogResult.No
                else -> DialogResult.Cancel
            }
            // Отдаем в коллбэк на обработку
            onResult(result)
        }

        // Не забываем отменять корутину при уничтожении компонента
        onDispose {
            job.cancel()
        }
    }
}

@Composable
fun SearchDialog(
    onSearch: (query: String) -> Unit,
    onCancel: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onCancel() }) {
        Column {
            Text("Введите текст для поиска:")
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.testTag("searchTextField")
            )
            Row {
                Button(onClick = { onSearch(searchQuery) }) {
                    Text("Найти")
                }
                Button(onClick = { onCancel() }) {
                    Text("Отмена")
                }
            }
        }
    }
}

@Composable
fun ReplaceDialog(
    foundCount: Int,
    onReplace: (replaceWith: String, replaceAll: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var replaceQuery by remember { mutableStateOf("") }
    var replaceAll by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = { onCancel() }) {
        Column {
            Text("Найдено совпадений: $foundCount")
            Text("Введите текст для замены:")
            TextField(
                value = replaceQuery,
                onValueChange = { replaceQuery = it },
                modifier = Modifier.testTag("replaceTextField") // Добавляем testTag для поля ввода
            )
            Row {
                Button(onClick = { onReplace(replaceQuery, replaceAll) }, modifier = Modifier.testTag("replaceButton")) {
                    Text("Заменить")
                }
                Button(onClick = { onCancel() }, modifier = Modifier.testTag("cancelButton")) {
                    Text("Отмена")
                }
            }
            Row {
                Checkbox(
                    checked = replaceAll,
                    onCheckedChange = { replaceAll = it },
                    modifier = Modifier.testTag("replaceAllCheckbox")
                )
                Text("Заменить все")
            }
        }
    }
}

@Composable
fun HighlightSearchDialog(
    onSearch: (String, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var ignoreCase by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = { onCancel() }) {
        Column {
            Text("Введите текст для поиска:")
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.testTag("highlightSearchTextField") // Добавляем testTag для поля ввода
            )
            Row {
                Button(onClick = { onSearch(searchQuery, ignoreCase) }, modifier = Modifier.testTag("searchButton")) {
                    Text("Поиск")
                }
                Button(onClick = { onCancel() }, modifier = Modifier.testTag("cancelButton")) {
                    Text("Отмена")
                }
            }
            Row {
                Checkbox(
                    checked = ignoreCase,
                    onCheckedChange = { ignoreCase = it },
                    modifier = Modifier.testTag("ignoreCaseCheckbox") // Добавляем testTag для чекбокса
                )
                Text("Игнорировать регистр")
            }
        }
    }
}