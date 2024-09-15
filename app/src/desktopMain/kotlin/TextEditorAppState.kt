import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import tray.TraySettings
import window.TextEditorWindowState

// Компонент для запоминания состояния приложения
@Composable
fun rememberApplicationState() = remember {
    TextEditorAppState().apply {
        newWindow()
    }
}

/**
 * Состояние приложения текстового редактора
 */
class TextEditorAppState {
    val traySettings = TraySettings()
    val trayState = TrayState()

    // Раздел по работе с окнами приложения
    private val _windows = mutableStateListOf<TextEditorWindowState>()
    val windows: List<TextEditorWindowState> get() = _windows

    // Создает новое окно приложения
    fun newWindow() {
        _windows.add(
            TextEditorWindowState(
                application = this,
                path = null,
                exit = _windows::remove
            )
        )
    }

    // Отправляет уведомление в трей
    fun sendNotification(notification: Notification) {
        trayState.sendNotification(notification)
    }

    // Закрывает все окна приложения
    suspend fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {
            if (!window.exit()) {
                break
            }
        }
    }
}