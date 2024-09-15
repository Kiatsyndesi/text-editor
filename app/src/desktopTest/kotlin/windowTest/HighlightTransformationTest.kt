package windowTest

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import window.HighlightTransformation

class HighlightTransformationTest {

    @Test
    fun `test text with one highlight`() {
        val text = "Hello World"
        val highlightedIndices = listOf(6)  // "World" начинается с индекса 6
        val queryLength = 5  // Длина слова "World"

        val transformation = HighlightTransformation(text, highlightedIndices, queryLength)
        val result: TransformedText = transformation.filter(AnnotatedString(text))

        // Проверяем, что обычный текст "Hello" остался неизменным
        assertEquals("Hello ", result.text.text.substring(0, 6))

        // Проверяем, что "World" подсвечен красным и подчеркнут
        val spanStyle = result.text.spanStyles[0].item
        assertEquals(Color.Red, spanStyle.color)
        assertEquals(TextDecoration.Underline, spanStyle.textDecoration)

        // Проверяем, что правильный текст был подсвечен
        assertEquals("World", result.text.text.substring(6, 11))
    }

    @Test
    fun `test text with multiple highlights`() {
        val text = "Hello Hello"
        val highlightedIndices = listOf(0, 6)  // "Hello" дважды встречается
        val queryLength = 5  // Длина слова "Hello"

        val transformation = HighlightTransformation(text, highlightedIndices, queryLength)
        val result: TransformedText = transformation.filter(AnnotatedString(text))

        // Проверяем, что первое "Hello" подсвечено красным и подчеркнуто
        val firstSpanStyle = result.text.spanStyles[0].item
        assertEquals(Color.Red, firstSpanStyle.color)
        assertEquals(TextDecoration.Underline, firstSpanStyle.textDecoration)
        assertEquals("Hello", result.text.text.substring(0, 5))

        // Проверяем, что второе "Hello" также подсвечено
        val secondSpanStyle = result.text.spanStyles[1].item
        assertEquals(Color.Red, secondSpanStyle.color)
        assertEquals(TextDecoration.Underline, secondSpanStyle.textDecoration)
        assertEquals("Hello", result.text.text.substring(6, 11))
    }

    @Test
    fun `test text with no highlights`() {
        val text = "Hello World"
        val highlightedIndices = emptyList<Int>()  // Нет совпадений
        val queryLength = 5  // Длина запроса не важна, так как нет совпадений

        val transformation = HighlightTransformation(text, highlightedIndices, queryLength)
        val result: TransformedText = transformation.filter(AnnotatedString(text))

        // Проверяем, что текст остался неизменным
        assertEquals(text, result.text.text)
        assertTrue(result.text.spanStyles.isEmpty())  // Нет стилей, так как нет подсветки
    }

    @Test
    fun `test partial highlight`() {
        val text = "Hello World"
        val highlightedIndices = listOf(0)  // Подсветим только часть текста "Hello"
        val queryLength = 3  // Подсвечивается только "Hel"

        val transformation = HighlightTransformation(text, highlightedIndices, queryLength)
        val result: TransformedText = transformation.filter(AnnotatedString(text))

        // Проверяем, что "Hel" подсвечено красным и подчеркнуто
        val spanStyle = result.text.spanStyles[0].item
        assertEquals(Color.Red, spanStyle.color)
        assertEquals(TextDecoration.Underline, spanStyle.textDecoration)
        assertEquals("Hel", result.text.text.substring(0, 3))

        // Остальная часть текста должна быть обычной
        assertEquals("lo World", result.text.text.substring(3))
    }
}
