import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.application
import providers.LocalResources
import providers.rememberAppResources

fun main() {
    // Запускаем приложение с помощью функции application
     application {
         // Предоставляем локальные ресурсы для всего приложения
         CompositionLocalProvider(LocalResources provides rememberAppResources()) {
             // Запускаем главный компонент приложения TextEditorApp с состоянием приложения
             TextEditorApp(rememberApplicationState())
         }
     }
}
