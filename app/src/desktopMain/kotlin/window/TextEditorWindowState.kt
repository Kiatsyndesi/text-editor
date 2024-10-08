package window

import TextEditorAppState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.WindowState
import dialogs.DialogResult
import dialogs.DialogState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.nio.file.Path

/**
 * Класс для управления состояние окна текстового редактора
 *
 * @param application Состояние непосредственно приложения
 * @param path Путь к файлу, который нужно открыть в окне редактора
 * @param exit Коллбэк для закрытия окна
 */
class TextEditorWindowState(
    private val application: TextEditorAppState,
    path: Path?,
    private val exit: (TextEditorWindowState) -> Unit
) {
    // Одно из важнейших свойств - состояние окна
    val window = WindowState()

    // Здесь используем технику Jetpack Compose - mutableStateOf.
    // Нужен для свойств, которые будут меняться, например путь к файлу, был изменен файл или нет
    var path by mutableStateOf(path)

    var isChanged by mutableStateOf(false)
        private set

    // Состояния окон-диалогов для управления взаимодействия с юзером при работе с файлом
    val openDialog = DialogState<Path?>()
    val saveDialog = DialogState<Path?>()
    val exitDialog = DialogState<DialogResult>()
    val searchDialog = DialogState<Unit>()
    val replaceDialog = DialogState<Unit>()
    val searchHighlightDialog = DialogState<Unit>()

    // Используем канал для работы с нотификациями, Flow нужен для реактивного реагирования
    private var _notifications = Channel<TextEditorWindowNotification>(0)
    val notifications: Flow<TextEditorWindowNotification> get() = _notifications.receiveAsFlow()

    // Храним состояние текста файла, которое может меняться. Добавляем геттер, сеттер
    private var _text by mutableStateOf("")

    var text: String
        get() = _text
        set(value) {
            check(isInit)
            _text = value
            isChanged = true
        }

    // Флаг, чтобы понять инициализировано редактирование файла или нет
    var isInit by mutableStateOf(false)
        private set

    // Открывает документ по указанному пути, если пути нет - создает новый
    suspend fun run() {
        if (path != null) {
            open(path!!)
        } else {
            initNew()
        }
    }

    // Открывает окно-диалог для выбора файла и загружает его содержимое
    suspend fun open() {
        if (askToSave()) {
            val path = openDialog.awaitResult()
            if (path != null) {
                open(path)
            }
        }
    }

    // Асинхронно открывает документ по указанному пути
    private suspend fun open(path: Path) {
        isInit = false
        isChanged = false
        this.path = path
        try {
            _text = path.readTextAsync()
            isInit = true
        } catch (e: Exception) {
            e.printStackTrace()
            text = "Неверно указанный путь к файлу: $path"
        }
    }

    // Ф-я для создания нового документа. Текст при этом пустой
    private fun initNew() {
        _text = ""
        isInit = true
        isChanged = false
    }

    // Открывает новое окно приложения
    fun newWindow() {
        application.newWindow()
    }


    // Сохраняет текущий документ. Если путь не указан, открывает диалог для выбора пути
    suspend fun save(): Boolean {
        check(isInit)

        if (path == null) {
            val path = saveDialog.awaitResult()
            if (path != null) {
                save(path)
                return true
            }
        } else {
            save(path!!)
            return true
        }
        return false
    }

    // Джоба для асинхронного сохранения документа
    private var saveJob: Job? = null

    // Асинхронно сохраняет документ по указанному пути с помощью джобы
    private suspend fun save(path: Path) {
        isChanged = false
        this.path = path

        saveJob?.cancel()
        saveJob = path.launchSaving(text)

        try {
            saveJob?.join()
            _notifications.trySend(TextEditorWindowNotification.SaveSuccess(path))
        } catch (e: Exception) {
            isChanged = true
            e.printStackTrace()
            _notifications.trySend(TextEditorWindowNotification.SaveError(path))
        }
    }

    // Закрывает окно, но перед закрытием спрашивает нужно ли сохранить файл
    suspend fun exit(): Boolean {
        return if (askToSave()) {
            exit(this)
            true
        } else {
            false
        }
    }

    // Спрашивает у пользователя, нужно ли сохранить изменения перед выполнением действия
    private suspend fun askToSave(): Boolean {
        if (isChanged) {
            when (exitDialog.awaitResult()) {
                DialogResult.Yes -> {
                    if (save()) {
                        return true
                    }
                }
                DialogResult.No -> {
                    return true
                }
                DialogResult.Cancel -> return false
            }
        } else {
            return true
        }

        return false
    }

    var searchQuery by mutableStateOf("")
    var highlightedIndices = listOf<Int>()
    var searchResults by mutableStateOf(listOf<Int>())

    // Состояние для показа информационного окна с количеством совпадений
    var isMatchCountDialogVisible by mutableStateOf(false)

    suspend fun searchAndReplace() {
        searchDialog.awaitResult()
    }

    suspend fun replace() {
        replaceDialog.awaitResult()
    }

    suspend fun searchAndHighlight() {
        searchHighlightDialog.awaitResult()
    }

    // Метод поиска
    fun search(query: String) {
        searchQuery = query
        searchResults = text.findAll(query)
    }

    // Метод замены
    fun replace(replaceWith: String, replaceAll: Boolean) {
        if (replaceAll) {
            text = text.replace(searchQuery, replaceWith)
        } else {
            searchResults.firstOrNull()?.let { index ->
                text = text.replaceRange(index, index + searchQuery.length, replaceWith)
            }
        }

        searchResults = text.findAll(searchQuery) // Обновляем результаты поиска
    }

    // Метод поиска и подсветки совпадений
    fun searchAndHighlight(query: String, ignoreCase: Boolean) {
        searchQuery = query
        val searchText = if (ignoreCase) text.lowercase() else text
        val searchQueryToUse = if (ignoreCase) query.lowercase() else query

        // Поиск всех совпадений
        highlightedIndices = searchText.findAll(searchQueryToUse)
    }

    // Очищаем подсветку
    fun clearHighlight() {
        highlightedIndices = emptyList()
    }

    // Показать информационное окно с количеством совпадений
    fun showMatchCountDialog() {
        isMatchCountDialogVisible = true
    }

    // Скрыть информационное окно
    fun dismissMatchCountDialog() {
        isMatchCountDialogVisible = false
    }

    // Вспомогательная функция для поиска всех совпадений
    private fun String.findAll(query: String): List<Int> {
        val indices = mutableListOf<Int>()
        var index = indexOf(query)

        while (index >= 0) {
            indices.add(index)
            index = indexOf(query, index + 1)
        }

        return indices
    }

    // Отправляет уведомление через состояние приложения
    fun sendNotification(notification: Notification) {
        application.sendNotification(notification)
    }
}

// Запускает асинхронное сохранение текста в файл с использованием глобальной корутины
@OptIn(DelicateCoroutinesApi::class)
private fun Path.launchSaving(text: String) = GlobalScope.launch {
    writeTextAsync(text)
}

// Асинхронно записывает текст в файл, используя контекст ввода-вывода
private suspend fun Path.writeTextAsync(text: String) = withContext(Dispatchers.IO) {
    toFile().writeText(text, Charsets.UTF_8)
}

// Асинхронно читает текст из файла, используя контекст ввода-вывода
private suspend fun Path.readTextAsync() = withContext(Dispatchers.IO) {
    toFile().readText(Charsets.UTF_8)
}