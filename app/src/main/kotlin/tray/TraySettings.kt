package tray

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TraySettings {
    var isTrayEnabled by mutableStateOf(true)
        private set
}