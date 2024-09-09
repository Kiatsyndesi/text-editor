import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.application
import providers.LocalResources
import providers.rememberAppResources

fun main() {
     application {
         CompositionLocalProvider(LocalResources provides rememberAppResources()) {
             TextEditorApp(rememberApplicationState())
         }
     }
}
