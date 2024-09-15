package windowTest

import TextEditorAppState
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import window.TextEditorWindowState
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class TextEditorWindowStateTest {

    private lateinit var mockAppState: TextEditorAppState
    private lateinit var mockExitCallback: (TextEditorWindowState) -> Unit
    private lateinit var textEditorWindowState: TextEditorWindowState

    @Before
    fun setup() {
        mockAppState = mock(TextEditorAppState::class.java)
        mockExitCallback = mock()
        textEditorWindowState = TextEditorWindowState(mockAppState, null, mockExitCallback)
    }

    @Test
    fun testInitialStateIsCorrect() {
        assertNull(textEditorWindowState.path)
        assertFalse(textEditorWindowState.isChanged)
        assertFalse(textEditorWindowState.isMatchCountDialogVisible)
    }

    @Test
    fun testChangingPath() {
        val newPath = mock(Path::class.java)
        textEditorWindowState.path = newPath
        assertEquals(newPath, textEditorWindowState.path)
    }
}


