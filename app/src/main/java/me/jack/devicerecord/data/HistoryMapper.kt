package me.jack.devicerecord.data

import me.jack.devicerecord.extension.isBlankOrNA
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Jack on 2017/12/24.
 */
class HistoryMapper {

    fun convertToHistoryFromString(str: String): SortedMap<String, String> {
        if (str.isBlankOrNA()) return emptyMap<String, String>().toSortedMap()
        val map = HashMap<String, String>()
        str.split("\n").forEach {
            val set = it.split(":")
            map.put(set[0].trim(), set[1].trim())
        }
//        Log.d(javaClass.simpleName, ">> Convert to history from string: \n${map.toSortedMap()}")
        return map.toSortedMap()
    }

    fun convertToStringFromHistory(map: SortedMap<String, String>): String {
        val builder = StringBuilder()
        map.forEach {
            builder.append("${it.key} : ${it.value}\n")
        }
        return if (map.isEmpty()) "NA" else builder.deleteCharAt(builder.lastIndex).toString()
    }
}