package providers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

/**
 * Класс, инкапсулирующий ресурсы приложения.
 * Для курсового проекта будет достаточно иконки.
 */
class AppResources(val icon: VectorPainter)

/**
 * Локальный провайдер ресурсов приложения.
 * Выдает ошибку, если ресурсы не были предоставлены.
 */
val LocalResources = staticCompositionLocalOf<AppResources> {
    error("Не прокинуты ресурсы в приложение")
}

/**
 * Запоминает и возвращает ресурсы приложения.
 *
 * @return Экземпляр ресурсов приложения с иконкой.
 */
@Composable
fun rememberAppResources(): AppResources {
    val icon = rememberVectorPainter(Icons.Default.Description, tintColor = Color(0xffff0000))

    return remember { AppResources(icon) }
}

/**
 * Запоминает и возвращает VectorPainter (инструмент для рендера векторной графики) для заданного ImageVector и цвета.
 *
 * @param image Векторное изображение.
 * @param tintColor Цвет для окрашивания.
 * @return Экземпляр VectorPainter.
 */
@Composable
fun rememberVectorPainter(image: ImageVector, tintColor: Color) = rememberVectorPainter(
        defaultWidth = image.defaultWidth,
        defaultHeight = image.defaultHeight,
        viewportWidth = image.viewportWidth,
        viewportHeight = image.viewportHeight,
        name = image.name,
        tintColor = tintColor,
        tintBlendMode = image.tintBlendMode,
        autoMirror = false,
        content = { _, _ -> RenderVectorGroup(group = image.root) }
    )
