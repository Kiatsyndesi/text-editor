package dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.AwtWindow
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