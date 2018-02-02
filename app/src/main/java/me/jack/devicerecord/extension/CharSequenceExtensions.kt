package me.jack.devicerecord.extension

import me.jack.kotlin.library.extension.ensureNotBlank

/**
 * Created by Jack on 2017/12/31.
 */
fun CharSequence.ensureNotBlank() = ensureNotBlank("NA")

fun String.ensureNotBlank() = ensureNotBlank("NA")

fun String.ensureNotBlankAndTrim() = ensureNotBlank("NA").trim()

fun String.isNA() = this == "NA"

fun String.isNotNA() = this != "NA"

fun String.isNotBlankAndNA() = isNotBlank() && isNotNA()

fun String.isBlankOrNA() = isBlank() || isNA()

fun String.firstLetterUpperCase(): String {
    val ca = this.toCharArray()
    if (ca[0] in 'a'..'z') {
        ca[0] = (ca[0].toInt() - 32).toChar()
    }
    return String(ca)
}

fun CharSequence.toIf(s: CharSequence, block: (s: CharSequence) -> Boolean) = if (block(s)) s else this