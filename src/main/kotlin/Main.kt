package org.example
import java.io.File

const val PERCENTAGE_HUNDRED = 100
const val MAX_CORRECT_ANSWERS = 3

data class Word(
    val originalWord: String,
    val translatedWord: String,
    val correctAnswersCount: Int = 0
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
        when(choseInput) {
            "1" -> {
                val notLearnedWords = dictionary.filter { it.correctAnswersCount < MAX_CORRECT_ANSWERS }
                if (notLearnedWords.isNotEmpty()){
                    while(true) {
                        val questionWords = notLearnedWords.shuffled().take(4)
                        val correctAnswer = questionWords.random()
                        println("\n${correctAnswer.originalWord}:")
                        questionWords.forEachIndexed { index, word ->
                            println("${index + 1} - ${word.translatedWord}")
                        }
                        print("Ввод: ")
                        val guessInput = readln().toIntOrNull()
                    }
                } else {
                    println("\nВсе слова выучены\n")
                    continue
                }

            }
            "2" -> {
                val wordsAmount = dictionary.size
                val learnedWords = dictionary.filter { it.correctAnswersCount >= MAX_CORRECT_ANSWERS }.size
                val learnedPercent = (learnedWords.toDouble() / wordsAmount * PERCENTAGE_HUNDRED).toInt()
                println(
                    "Общее количество слов в словаре | $wordsAmount\n" +
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