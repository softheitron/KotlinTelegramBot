package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    val json = Json { ignoreUnknownKeys = true }
    private val httpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = httpClient.send(getUpdatesRequest, HttpResponse.BodyHandlers.ofString())
        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: Long, message: String) {
        val sendMessageUrl = "$TELEGRAM_API_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId,
            message
        )
        val requestBodyString = json.encodeToString(requestBody)
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Long) {
        val sendMessageUrl = "$TELEGRAM_API_URL$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId,
            MAIN_MENU_TEXT,
            ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = LEARN_WORDS_TEXT, callbackData = LEARN_WORDS_CLICK)),
                    listOf(
                        InlineKeyboard(text = STATISTICS_TEXT, callbackData = STATISTICS_CLICK))
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

    fun sendQuestion(chatId: Long, question: Question) {
        val sendMessageUrl = "$TELEGRAM_API_URL$botToken/sendMessage"
        val backToMenuButton = listOf(
            listOf(
                InlineKeyboard(
                    text = BACK_TO_MENU_TEXT,
                    callbackData = BACK_TO_MENU_CLICK)
            )
        )

        val requestBody = SendMessageRequest(
            chatId,
            question.correctAnswer.originalWord,
            ReplyMarkup(
                question.variants.mapIndexed { index, word ->
                    listOf(
                        InlineKeyboard(
                            text = word.translatedWord,
                            callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                        )
                    )
                } + backToMenuButton)
            )

        val requestBodyString = json.encodeToString(requestBody)

        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

}