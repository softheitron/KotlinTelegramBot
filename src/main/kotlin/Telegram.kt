package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0L
    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(1000)
        val responseString = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = telegramBotService.json.decodeFromString(responseString)
        val update = response.result
        val firstUpdate = update.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1


        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val receivedText = firstUpdate.message?.text
        val data = firstUpdate.callbackQuery?.data

        if (chatId != null) {
            if (receivedText.equals(START_MENU, true)) telegramBotService.sendMenu(chatId)
            when {
                data.equals(LEARN_WORDS_CLICK) -> checkNextQuestionAndSend(trainer, telegramBotService, chatId)
                data.equals(STATISTICS_CLICK) -> showStatistics(trainer, telegramBotService, chatId)
                data.equals(BACK_TO_MENU_CLICK) -> telegramBotService.sendMenu(chatId)
                data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX, true) == true -> {
                    val answerId = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull() ?: continue
                    val lastQuestion = trainer.getLastQuestion()
                    checkAnswerAndSendMessage(
                        answerId,
                        lastQuestion,
                        trainer,
                        telegramBotService,
                        chatId
                    )
                }
            }
        }
    }

}

fun checkAnswerAndSendMessage(
    answerId: Int,
    lastQuestion: Question?,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    if (trainer.checkAnswer(answerId)) {
        telegramBotService.sendMessage(chatId, RIGHT_ANSWER_MESSAGE)
        checkNextQuestionAndSend(trainer, telegramBotService, chatId)
    } else {
        val wrongAnswerMessage = "Неправильно! " +
                "${lastQuestion?.correctAnswer?.originalWord}" +
                " - это ${lastQuestion?.correctAnswer?.translatedWord}"
        telegramBotService.sendMessage(chatId, wrongAnswerMessage)
        checkNextQuestionAndSend(trainer, telegramBotService, chatId)
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val nextQuestion = trainer.getNextQuestion()
    if (nextQuestion == null) {
        telegramBotService.sendMessage(chatId, ALL_WORDS_LEARNED_MESSAGE)
    } else {
        telegramBotService.sendQuestion(chatId, nextQuestion)
    }
}

fun showStatistics(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val statistics = trainer.getStatistics()
    val statisticsMessageText = "Выучено ${statistics.learnedWords} из ${statistics.wordsAmount} слов | ${statistics.learnedPercent}%\n"
    telegramBotService.sendMessage(chatId, statisticsMessageText)
}


