package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val idTextRegex = "\"update_id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(1000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        val idMatches = idTextRegex.findAll(updates)
        val idGroups = idMatches.lastOrNull()?.groups
        updateId = idGroups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue


        val matchResult = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value ?: "No messages yet"
        println(text)

    }

}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val getUpdatesResponse = client.send(getUpdatesRequest, HttpResponse.BodyHandlers.ofString())


    return getUpdatesResponse.body()
}
