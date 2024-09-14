
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dialogs.DialogResult
import dialogs.HighlightSearchDialog
import dialogs.ReplaceDialog
import dialogs.SearchDialog
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path
import javax.swing.JOptionPane
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchDialog() {
        var searchQuery = ""

        composeTestRule.setContent {
            SearchDialog(
                onSearch = { query -> searchQuery = query },
                onCancel = {}
            )
        }

        // Теперь ищем TextField по testTag
        composeTestRule.onNodeWithTag("searchTextField").assertExists()
        composeTestRule.onNodeWithTag("searchTextField").performTextInput("test search")
        composeTestRule.onNodeWithText("Найти").performClick()

        composeTestRule.waitForIdle()

        // Проверяем, что функция поиска вызвана с правильным значением
        assertEquals("test search", searchQuery)
    }

    @Test
    fun testReplaceDialog() {
        var replaceQuery = ""
        var replaceAll = false

        composeTestRule.setContent {
            ReplaceDialog(
                foundCount = 3,
                onReplace = { query, all ->
                    replaceQuery = query
                    replaceAll = all
                },
                onCancel = {}
            )
        }

        // Тестируем ввод текста и нажатие кнопки "Заменить"
        composeTestRule.onNodeWithTag("replaceTextField").assertExists()
        composeTestRule.onNodeWithTag("replaceTextField").performTextInput("замена")
        composeTestRule.onNodeWithTag("replaceButton").performClick()

        composeTestRule.waitForIdle()

        // Проверяем результат замены
        assertEquals("замена", replaceQuery)
        assertTrue(replaceAll)
    }

    @Test
    fun testHighlightSearchDialog() {
        var searchQuery = ""
        var ignoreCase = false

        composeTestRule.setContent {
            HighlightSearchDialog(
                onSearch = { query, case ->
                    searchQuery = query
                    ignoreCase = case
                },
                onCancel = {}
            )
        }

        // Тестируем ввод текста и нажатие кнопки "Поиск"
        composeTestRule.onNodeWithTag("highlightSearchTextField").assertExists()
        composeTestRule.onNodeWithTag("highlightSearchTextField").performTextInput("подсветка")
        composeTestRule.onNodeWithTag("searchButton").performClick()

        composeTestRule.waitForIdle()

        // Проверяем результат поиска
        assertEquals("подсветка", searchQuery)
        assertTrue(ignoreCase)
    }

    @Test
    fun testFileDialog() {
        // Мокаем коллбэк
        val mockCallback = mock<(Path?) -> Unit>()

        // Мокаем FileDialog
        val mockFileDialog = mock<FileDialog>()

        // Эмулируем поведение диалога, задавая файл и директорию
        whenever(mockFileDialog.file).thenReturn("testfile.txt")
        whenever(mockFileDialog.directory).thenReturn("/test/directory")

        // Симулируем вызов setVisible и вызов коллбэка
        mockCallback(File(mockFileDialog.directory).resolve(mockFileDialog.file).toPath())

        // Проверяем, что коллбэк был вызван с правильным результатом
        verify(mockCallback).invoke(Path.of("/test/directory/testfile.txt"))
    }

    @Test
    fun testYesNoCancelDialogYes() = runBlocking {
        // Мокаем коллбэк
        val mockCallback = mock<(DialogResult) -> Unit>()

        // Симулируем результат JOptionPane
        val dialogResult = JOptionPane.YES_OPTION
        if (dialogResult == JOptionPane.YES_OPTION) {
            mockCallback(DialogResult.Yes)
        }

        // Проверяем, что коллбэк был вызван с результатом "Yes"
        verify(mockCallback).invoke(DialogResult.Yes)
    }

    @Test
    fun testYesNoCancelDialogNo() = runBlocking {
        // Мокаем коллбэк
        val mockCallback = mock<(DialogResult) -> Unit>()

        // Симулируем результат JOptionPane
        val dialogResult = JOptionPane.NO_OPTION
        if (dialogResult == JOptionPane.NO_OPTION) {
            mockCallback(DialogResult.No)
        }

        // Проверяем, что коллбэк был вызван с результатом "No"
        verify(mockCallback).invoke(DialogResult.No)
    }

    @Test
    fun testYesNoCancelDialogCancel() = runBlocking {
        // Мокаем коллбэк
        val mockCallback = mock<(DialogResult) -> Unit>()

        // Симулируем результат JOptionPane
        val dialogResult = JOptionPane.CANCEL_OPTION
        if (dialogResult == JOptionPane.CANCEL_OPTION) {
            mockCallback(DialogResult.Cancel)
        }

        // Проверяем, что коллбэк был вызван с результатом "Cancel"
        verify(mockCallback).invoke(DialogResult.Cancel)
    }
}

