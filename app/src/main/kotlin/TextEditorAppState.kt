import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import tray.TraySettings
import window.TextEditorWindowState

@Composable
fun rememberApplicationState() = remember {
    TextEditorAppState().apply {
        newWindow()
    }
}

class TextEditorAppState {
    val settings = TraySettings()
    val tray = TrayState()

    private val _windows = mutableStateListOf<TextEditorWindowState>()
    val windows: List<TextEditorWindowState> get() = _windows

    fun newWindow() {
        _windows.add(
            TextEditorWindowState(
                application = this,
                path = null,
                exit = _windows::remove
            )
        )
    }

    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    suspend fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {
            if (!window.exit()) {
                break
            }
        }
    }
}