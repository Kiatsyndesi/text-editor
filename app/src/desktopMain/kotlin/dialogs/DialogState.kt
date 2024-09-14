package dialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred

/**
 * Класс, представляющий состояние диалога, который ожидает результат.
 *
 * @param <T> Тип результата, который ожидается от диалога.
 */
class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    /**
     * Возвращает true, если диалог ожидает результат.
     */
    val isAwaiting get() = onResult != null

    /**
     * Приостанавливает выполнение до тех пор, пока не будет получен результат.
     *
     * @return Результат типа T.
     * @throws IllegalStateException если результат уже был установлен.
     */
    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    /**
     * Устанавливает результат и завершает ожидание.
     *
     * @param result Результат типа T.
     * @throws IllegalStateException если результат уже был установлен.
     */
    fun onResult(result: T) = onResult!!.complete(result)
}