package chucknorris

import kotlin.math.pow
import kotlin.system.exitProcess

private const val ENCODE = "encode"
private const val DECODE = "decode"
private const val EXIT = "exit"
private const val ENCODING_LENGTH = 7
private const val SPACE = ' '
private const val ZERO = "0"
private const val ONE = "1"
fun main() {
    showMainMenu()
}

fun showMainMenu() {
    println("Please input operation (encode/decode/exit):")
    val input = readln()
    if (input == EXIT) {
        println("Bye")
        exitProcess(0)
    } else {
        performOption(input)
    }
}

fun performOption(option: String) {
    when (option) {
        ENCODE -> {
            encode()
            println()
        }
        DECODE -> {
            decode()
            println()
        }
        else -> {
            println("There is no '$option' operation")
        }
    }
    showMainMenu()
}

fun encode() {
    println("Input string:")
    val input = readln()
    println("Encoded string:")
    val binaryCode = stringToBinaryCode(input)
    val encoded = binaryCodeToChuckNorrisUnaryCode(binaryCode)
    println(encoded)
}

fun decode() {
    println("Input encoded string:")
    val input = readln()
    val isEncodedInputValid = validateEncodedInput(input)
    if (!isEncodedInputValid) {
        println("Encoded string is not valid.")
    } else {
        val pairs = createBlocks(input)
        val decoded = decodePairs(pairs)
        val bytes = createBytes(decoded)
        val text = convertToChar(bytes)
        println("Decoded string:")
        println(text)
    }
}

private fun binaryCodeToChuckNorrisUnaryCode(binaryCode: String): String {
    var currentIndex = 0
    var result = ""
    while (currentIndex < binaryCode.length) {
        val firstIndex = currentIndex
        var nextIndex = currentIndex + 1
        while (currentIndex < nextIndex) {
            val currentChar = binaryCode[currentIndex]
            if (nextIndex == binaryCode.length) {
                if (currentChar == binaryCode[currentIndex - 1]) {
                    result += ZERO
                    currentIndex = nextIndex
                } else {
                    val zeros = printZeros(currentChar, 1)
                    result += zeros
                    currentIndex = nextIndex
                }
                break
            } else {
                val nextChar = binaryCode[nextIndex]
                when {
                    nextIndex == binaryCode.length - 1 -> {
                        nextIndex = binaryCode.length - 1
                        val zeros = printZeros(currentChar, nextIndex - firstIndex)
                        result += zeros
                        currentIndex = nextIndex
                        break
                    }
                    currentChar.code == nextChar.code -> {
                        currentIndex = nextIndex
                        nextIndex = currentIndex + 1
                    }
                    else -> {
                        val zeros = printZeros(currentChar, nextIndex - firstIndex)
                        result += zeros
                        currentIndex = nextIndex
                        break
                    }
                }
            }
        }
    }
    return result.trim()
}

private fun printZeros(
    charToPrint: Char,
    numberOfZerosToPrint: Int
): String {
    var zeros = ""
    repeat(numberOfZerosToPrint) {
        zeros += "$ZERO"
    }
    return if (charToPrint.toString() == ZERO) {
        "$SPACE$ZERO$ZERO$SPACE"
    } else {
        "$SPACE$ZERO$SPACE"
    }.plus(zeros)
}

private fun stringToBinaryCode(input: String): String {
    var result = ""

    return if (input.length > 1) {
        for (char in input) {
            val binaryCode = to7DigitBinary(char.code)
            result += binaryCode
        }
        result
    } else {
        to7DigitBinary(input[0].code)
    }
}

private fun to7DigitBinary(number: Int): String {
    val binaryCode = Integer.toBinaryString(number)
    val remainingZeros = ENCODING_LENGTH - binaryCode.length
    var code = ""
    if (remainingZeros == 0) {
        code = binaryCode
    } else {
        repeat(remainingZeros) {
            code += "$ZERO"
        }
        code += binaryCode
    }
    return code
}

private fun createBlocks(input: String): List<List<String>>  {
    val inputList = input.split(" ")
    val pairs = mutableListOf<List<String>>()
    for (i in 0..inputList.size - 2 step 2) {
        pairs.add(listOf(inputList[i], inputList[i + 1]))
    }
    return pairs
}

private fun decodePairs(pairs: List<List<String>>): String {
    var decoded = ""
    for (i in pairs.indices) {
        val serie = pairs[i][0]
        val repetitions = pairs[i][1].length
        if (serie == ZERO) {
            repeat(repetitions) {
                decoded += ONE
            }
        } else {
            repeat(repetitions) {
                decoded += ZERO
            }
        }
    }
    return decoded
}

private fun createBytes(decodedString: String): List<CharSequence> {
    val bytes = mutableListOf<CharSequence>()
    for (i in decodedString.indices step 7) {
        val byte = decodedString.subSequence(i, i + 7)
        bytes.add(byte)
    }
    return bytes
}

private fun convertToChar(bytes: List<CharSequence>): String {
    var transformed = ""
    for(byte in bytes) {
        transformed += convertToAscii(byte)
    }
    return transformed
}

private fun convertToAscii(byte: CharSequence): Char {
    var code = 0
    val reversedByte = byte.reversed()
    for (i in byte.length - 1 downTo 0) {
        val bit = reversedByte[i].digitToInt() * (2.0.pow(i))
        code += bit.toInt()
    }
    return code.toChar()
}

private fun validateEncodedInput(input: String): Boolean {
    val hasOtherThanZeros = input.replace(" ","").filterNot { it == '0' }.isNotEmpty()
    if (hasOtherThanZeros)
        return false

    val blocks = input.split(" ")
    var bitsNumber = 0
    for (i in blocks.indices step 2) {
        bitsNumber += blocks[i+1].length
        if (blocks[i] != "00" && blocks[i] != "0") {
            return false
        }
    }

    if (blocks.size % 2 != 0)
        return false

    if (bitsNumber % 7 != 0)
        return false

    return true
}