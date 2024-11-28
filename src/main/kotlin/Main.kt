package org.example

import java.io.File

const val PERCENTAGE_HUNDRED = 100
const val MAX_CORRECT_ANSWERS = 3

data class Word(
    val originalWord: String,
    val translatedWord: String,
    var correctAnswersCount: Int = 0
)

fun main() {

    val dictionary = loadDictionary()

    while (true) {

        print(
            "1 - Учить слова\n" +
                    "2 - Статистика\n" +
                    "0 - Выход\n" +
                    "Ввод: "
        )

        val choseInput = readln()
        when (choseInput) {
            "1" -> {
                while (true) {
                    val notLearnedWords = dictionary.filter { it.correctAnswersCount < MAX_CORRECT_ANSWERS }
                    if (notLearnedWords.isNotEmpty()) {
                        val questionWords = notLearnedWords.take(4).shuffled()
                        val correctAnswer = questionWords.random()
                        println("\n${correctAnswer.originalWord}:")
                        questionWords.forEachIndexed { index, word ->
                            println("${index + 1} - ${word.translatedWord}")
                        }
                        println("----------\n0 - В меню")
                        print("Ввод: ")
                        val guessInput = readln().toIntOrNull()
                        if (guessInput != null && guessInput <= 4) {
                            val correctAnswerIndex = questionWords.indexOf(correctAnswer)
                            when (guessInput) {
                                (correctAnswerIndex + 1) -> {
                                    println("\nПравильно!")
                                    val correctWordIndex = dictionary.indexOf(correctAnswer)
                                    dictionary[correctWordIndex].correctAnswersCount++
                                    saveDictionary(dictionary)
                                }
                                0 -> { println(); break }
                                else -> println("Неправильно! ${correctAnswer.originalWord} - это ${correctAnswer.translatedWord}")
                            }
                        } else println("Некорректный ввод!")
                    } else {
                        println("\nВсе слова выучены!\n")
                        break
                    }
                }
            }

            "2" -> {
                val wordsAmount = dictionary.size
                val learnedWords = dictionary.filter { it.correctAnswersCount >= MAX_CORRECT_ANSWERS }.size
                val learnedPercent = (learnedWords.toDouble() / wordsAmount * PERCENTAGE_HUNDRED).toInt()
                println(
                    "\nОбщее количество слов в словаре | $wordsAmount\n" +
                            "Выучено $learnedWords из $wordsAmount слов | $learnedPercent%\n"
                )
            }

            "0" -> return
            else -> println("Неверный выбор. Введите число 1, 2 или 0")
        }

    }

}

fun loadDictionary(): List<Word> {
    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()
    val fileLines = wordsFile.readLines()

    fileLines.forEach { line ->
        val splitLine = line.split("|")
        dictionary.add(
            Word(
                originalWord = splitLine[0],
                translatedWord = splitLine[1],
                correctAnswersCount = splitLine.getOrNull(2)?.toIntOrNull() ?: 0
            )
        )
    }
    return dictionary
}

fun saveDictionary(dictionary: List<Word>) {
    val wordsFile = File("words.txt")
    wordsFile.writeText("")
    dictionary.forEach {
        wordsFile.appendText("${it.originalWord}|${it.translatedWord}|${it.correctAnswersCount}\n")
    }
}