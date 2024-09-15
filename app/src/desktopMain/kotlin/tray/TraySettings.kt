package tray

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Класс, представляющий настройки системного трея.
 *
 * @property isTrayEnabled Флаг, указывающий, включен ли системный трей.
 *                         Значение по умолчанию - true.
 */
class TraySettings {
    var isTrayEnabled by mutableStateOf(true)
        private set
}