package wordsvirtuoso

import java.io.File
import kotlin.system.exitProcess

class Words(private val wordsFile: File, private val candidatesFile: File) {
    private var counter = 0

    init {
        if (!wordsFile.exists()) println("Error: The words file ${wordsFile.name} doesn't exist.")
            .also { exitProcess(0) }
        if (!candidatesFile.exists()) println("Error: The candidate words file ${candidatesFile.name} doesn't exist.")
            .also { exitProcess(0) }
    }

    private fun checkWord(word: String) {
        if (word.length != 5) counter++.also { return }
        if (!word.matches("""[a-zA-Z]+""".toRegex())) counter++.also { return }
        if (word.toCharArray().distinct().size != word.toCharArray().size) counter++
    }

    private fun checkWordsInFile(file: File): List<String> {
        val listOfWords = file.readLines().map { it.lowercase() }
        listOfWords.forEach {
            checkWord(it)
        }
        if (counter == 0) {
            return listOfWords
        }
        else {
            println("Error: $counter invalid words were found in the ${file.name} file.").also { exitProcess(0) }
        }
    }

    fun checkIfCandidatesAreInWordsFile() {
        val wordsList = checkWordsInFile(wordsFile)
        val candidateList = checkWordsInFile(candidatesFile)

        if (wordsList.containsAll(candidateList)) {
            println("Words Virtuoso")
        } else {
            counter = candidateList.count { !wordsList.contains(it) }
            println("Error: $counter candidate words are not included in the ${wordsFile.name} file.")
        }
    }
}

fun main(args: Array<String>) {
    if (args.size != 2) println("Error: Wrong number of arguments.").also { return }
    Words(File(args[0]), File(args[1])).apply { checkIfCandidatesAreInWordsFile() }
}
