package me.jack.devicerecord.extension

import org.apache.commons.lang3.time.FastDateFormat

/**
 * Created by Jack on 2018/1/2.
 */
fun currentTimeWithFormat() = FastDateFormat.getInstance("yyyy/MM/dd").format(System.currentTimeMillis())!!

fun ensureRightTime(time: String) = if (time.isNotBlankAndNA()) time else currentTimeWithFormat()