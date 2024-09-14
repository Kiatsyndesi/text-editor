package window

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

class HighlightTransformation(
    private val text: String,
    private val highlightedIndices: List<Int>,
    private val queryLength: Int
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Построение строки с подсветкой
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0
            highlightedIndices.forEach { index ->
                append(text.substring(lastIndex, index)) // Добавляем обычный текст до совпадения
                withStyle(style = SpanStyle(color = Color.Red, textDecoration = TextDecoration.Underline)) {
                    append(text.substring(index, index + queryLength)) // Подсвечиваем совпадение
                }
                lastIndex = index + queryLength
            }
            append(text.substring(lastIndex)) // Добавляем оставшийся текст
        }

        return TransformedText(annotatedString, offsetMapping = VisualTransformation.None.filter(annotatedString).offsetMapping)
    }
}