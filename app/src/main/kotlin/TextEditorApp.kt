import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import kotlinx.coroutines.launch
import providers.LocalResources
import window.TextEditorWindow

@Composable
fun ApplicationScope.TextEditorApp(state: TextEditorAppState) {
    if (state.settings.isTrayEnabled && state.windows.isNotEmpty()) {
        ApplicationTray(state)
    }

    for (window in state.windows) {
        key(window) {
            TextEditorWindow(window)
        }
    }
}

@Composable
private fun ApplicationScope.ApplicationTray(state: TextEditorAppState) {
    Tray(
        LocalResources.current.icon,
        state = state.tray,
        tooltip = "Notepad",
        menu = { ApplicationMenu(state) }
    )
}

@Composable
private fun MenuScope.ApplicationMenu(state: TextEditorAppState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("Новый", onClick = state::newWindow)
    Separator()
    Item("Выход", onClick = { exit() })
}