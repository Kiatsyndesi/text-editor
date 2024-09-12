import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import kotlinx.coroutines.launch
import providers.LocalResources
import window.TextEditorWindow

// Компонент с самым главным в приложении - окнами
@Composable
fun ApplicationScope.TextEditorApp(state: TextEditorAppState) {
    // Если включен трей и есть открытые окна, отображаем трей
    if (state.traySettings.isTrayEnabled && state.windows.isNotEmpty()) {
        ApplicationTray(state)
    }

    // Для каждого окна в состоянии приложения создаем компонент TextEditorWindow
    for (window in state.windows) {
        key(window) {
            TextEditorWindow(window)
        }
    }
}

// Компонент трея приложения
@Composable
private fun ApplicationScope.ApplicationTray(state: TextEditorAppState) {
    // Создаем трей с иконкой, состоянием и меню
    Tray(
        LocalResources.current.icon,
        state = state.trayState,
        tooltip = "Текстовый редактор",
        menu = { ApplicationMenu(state) }
    )
}

// Компонент с меню приложения для управления им
@Composable
private fun MenuScope.ApplicationMenu(state: TextEditorAppState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("Новый", onClick = state::newWindow)
    Separator()
    Item("Выход", onClick = { exit() })
}