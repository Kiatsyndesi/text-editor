package windowTest

import org.junit.Test
import org.mockito.Mockito.mock
import window.TextEditorWindowNotification
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextEditorWindowNotificationTest {

    @Test
    fun testSaveSuccessNotificationInitialization() {
        // Мокаем путь для теста
        val mockPath = mock(Path::class.java)

        // Создаем экземпляр нотификации SaveSuccess
        val notification = TextEditorWindowNotification.SaveSuccess(mockPath)

        // Проверяем, что экземпляр является SaveSuccess
        assertTrue(notification is TextEditorWindowNotification.SaveSuccess)

        // Проверяем правильность пути
        assertEquals(mockPath, notification.path)
    }

    @Test
    fun testSaveErrorNotificationInitialization() {
        // Мокаем путь для теста
        val mockPath = mock(Path::class.java)

        // Создаем экземпляр нотификации SaveError
        val notification = TextEditorWindowNotification.SaveError(mockPath)

        // Проверяем, что экземпляр является SaveError
        assertTrue(notification is TextEditorWindowNotification.SaveError)

        // Проверяем правильность пути
        assertEquals(mockPath, notification.path)
    }
}
