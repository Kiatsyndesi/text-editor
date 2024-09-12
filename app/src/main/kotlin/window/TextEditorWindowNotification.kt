package window

import java.nio.file.Path

/**
 * Является репрезентацией нотификация, связанных с окном текстового редактора
 */
sealed class TextEditorWindowNotification {
    /**
     * Нотификация, обозначающая успешное сохрангение файла
     *
     * @param path Путь к сохраненному файлу
     */
    class SaveSuccess(val path: Path) : TextEditorWindowNotification()

    /**
     * Нотификация, обозначающая возникновение ошибки при сохрангении файла
     *
     * @param path Путь к фалу, который привел к ошибке
     */
    class SaveError(val path: Path) : TextEditorWindowNotification()
}