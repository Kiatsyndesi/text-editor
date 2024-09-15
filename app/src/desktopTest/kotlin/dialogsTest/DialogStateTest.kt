package dialogsTest

import dialogs.DialogState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert.*
import org.junit.Test

class DialogStateTest {

    @Test
    fun testIsAwaitingInitialState() {
        val dialogState = DialogState<String>()
        // Проверяем, что изначально диалог не ожидает результата
        assertFalse(dialogState.isAwaiting)
    }

    @Test
    fun testAwaitResultSetsAwaitingState() = runTest {
        val dialogState = DialogState<String>()

        // Запускаем корутину для ожидания результата
        val job = launch {
            dialogState.awaitResult()
        }

        // Даем время корутине перейти в состояние ожидания
        yield()  // Передаем управление другим корутинам для выполнения

        // Проверяем, что состояние диалога теперь "ожидающее"
        assertTrue(dialogState.isAwaiting)

        // Завершаем выполнение, установив результат
        dialogState.onResult("Test Result")
        job.join()  // Ждем завершения корутины
    }

    @Test
    fun testOnResultSetsAndCompletesResult() = runTest {
        val dialogState = DialogState<String>()
        val expectedResult = "Test Result"

        // Запускаем корутину для ожидания результата
        val deferred = async {
            dialogState.awaitResult()
        }

        // Даем время корутине перейти в состояние ожидания
        yield()  // Передаем управление другим корутинам для выполнения

        // Устанавливаем результат
        dialogState.onResult(expectedResult)

        // Проверяем, что результат корректно установлен и ожидание завершилось
        assertEquals(expectedResult, deferred.await())
        assertFalse(dialogState.isAwaiting)  // После получения результата состояние не должно быть "ожидающим"
    }

    @Test(expected = IllegalStateException::class)
    fun testOnResultThrowsExceptionIfResultAlreadySet() = runTest {
        val dialogState = DialogState<String>()
        val result = "Test Result"

        // Запускаем корутину для ожидания результата
        val deferred = async {
            dialogState.awaitResult()
        }

        yield()  // Даем корутине перейти в состояние ожидания

        // Устанавливаем первый результат
        dialogState.onResult(result)

        yield()  // Даем корутине обработать результат

        // Попытка установить результат второй раз (до завершения диалога) должна выбросить исключение
        dialogState.onResult(result)  // Это должно вызвать исключение
    }

    @Test(expected = IllegalStateException::class)
    fun testOnResultThrowsExceptionIfResultNotAwaiting() {
        val dialogState = DialogState<String>()
        // Попытка установить результат без вызова awaitResult() должна выбросить исключение
        dialogState.onResult("Test Result")
    }
}

