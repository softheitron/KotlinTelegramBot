package org.example

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> "${index + 1} - ${word.translatedWord}\n" }
        .joinToString("")
    return "\n" + this.correctAnswer.originalWord + "\n" + variants + "0 - В меню"
}


fun main() {

    val trainer = try {
        LearnWordsTrainer(learnedAnswerCount = MAX_CORRECT_ANSWERS, countOfQuestionWords = QUESTION_WORDS_AMOUNT)
    } catch (e: Exception) {
        println("Невозможно загрузить словарь\nReason - $e")
        return
    }

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
                    val question = trainer.getNextQuestion()
                    if (question != null) {
                        println(question.asConsoleString())
                        print("Ввод: ")
                        val guessInput = readln().toIntOrNull()
                        if (guessInput == 0) break
                        if (guessInput != null && guessInput <= QUESTION_WORDS_AMOUNT) {
                            if (trainer.checkAnswer(guessInput.minus(1))) {
                                println("\nПравильно!")
                            } else {
                                println("\nНеправильно! ${question.correctAnswer.originalWord} - это ${question.correctAnswer.translatedWord}")
                            }
                        } else println("\nНекорректный ввод!")
                    } else {
                        println("\nВсе слова выучены!")
                        break
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println(
                    "\nОбщее количество слов в словаре | ${statistics.wordsAmount}\n" +
                            "Выучено ${statistics.learnedWords} из ${statistics.wordsAmount} слов | ${statistics.learnedPercent}%\n"
                )
            }

            "0" -> return
            else -> println("Неверный выбор. Введите число 1, 2 или 0")
        }

    }

}