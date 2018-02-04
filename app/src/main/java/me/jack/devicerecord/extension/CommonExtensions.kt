package me.jack.devicerecord.extension

import org.apache.commons.lang3.time.FastDateFormat

/**
 * Created by Jack on 2018/1/2.
 */
fun currentTimeWithFormat() = FastDateFormat.getInstance("yyyy/MM/dd").format(System.currentTimeMillis())!!

fun ensureRightTime(time: String) = if (time.isNotBlankAndNA()) time else currentTimeWithFormat()

fun IntArray.shuffle(): IntArray {
    var i = lastIndex
    while (i > 0) {
        val j = Math.floor(Math.random() * i).toInt()
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
        i--
    }
    return this
}