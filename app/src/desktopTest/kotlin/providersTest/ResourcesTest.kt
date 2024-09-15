package providersTest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import providers.LocalResources
import providers.rememberAppResources
import providers.rememberVectorPainter

/**
 * Тесты для класса AppResources и связанных функций.
 */
class AppResourcesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Проверяет, что функция rememberAppResources возвращает не null.
     */
    @Test
    fun rememberAppResourcesReturnsNotNull() {
        composeTestRule.setContent {
            val appResources = rememberAppResources()
            assertNotNull(appResources)
            assertNotNull(appResources.icon)
        }
    }

    /**
     * Проверяет, что LocalResources выдает ошибку, если ресурсы не были предоставлены.
     */
    @Test(expected = IllegalStateException::class)
    fun localResourcesWithoutProvidedResourcesThrowsError() {
        composeTestRule.setContent {
            LocalResources.current
        }
    }

    /**
     * Проверяет, что LocalResources возвращает корректные ресурсы, когда они предоставлены.
     */
    @Test
    fun localResourceWithProvidedResourcesReturnsCorrectResources() {
        composeTestRule.setContent {
            val appResources = rememberAppResources()
            CompositionLocalProvider(LocalResources provides appResources) {
                val currentResources = LocalResources.current
                assertEquals(appResources, currentResources)
            }
        }
    }

    /**
     * Проверяет, что rememberVectorPainter возвращает корректный VectorPainter.
     */
    @Test
    fun rememberVectorPainterReturnsCorrectVectorPainter() {
        composeTestRule.setContent {
            val imageVector = Icons.Default.Description
            val tintColor = Color(0xffff0000)
            val vectorPainter = rememberVectorPainter(imageVector, tintColor)

            // Проверяем, что vectorPainter не null
            assertNotNull(vectorPainter)

            // VectorPainter не предоставляет прямой доступ к внутренним свойствам,
            // Ограничимся проверкой не null и соответствия типов
            assertTrue(vectorPainter is VectorPainter)
        }
    }
}
