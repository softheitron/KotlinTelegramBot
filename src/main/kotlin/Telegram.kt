package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val INDEX_DESTINATION = 11

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(1000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updateIdString = updates.substring(startUpdateId + INDEX_DESTINATION, endUpdateId)

        updateId = updateIdString.toInt() + 1
    }

}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val getUpdatesResponse = client.send(getUpdatesRequest, HttpResponse.BodyHandlers.ofString())

    return getUpdatesResponse.body()
}
