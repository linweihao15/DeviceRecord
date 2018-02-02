package me.jack.devicerecord.util

import android.os.Environment
import android.util.Log
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.data.DeviceRecord
import me.jack.devicerecord.data.HistoryMapper
import me.jack.devicerecord.extension.ensureNotBlankAndTrim
import me.jack.devicerecord.extension.ensureRightTime
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 * Created by Jack on 2017/12/11.
 */
class POIUtils {

    companion object {
        val instance by lazy { POIUtils() }
        private val rootPath =
                if (Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED)
                    Environment.getExternalStorageDirectory().toString()
                else ""
        private val ColumnNames = arrayOf(
                "ID",
                "Inventory Number",
                "Device Model",
                "Brand",
                "Serial Number",
                "IMEI",
                "Description",
                "Password",
                "Owner",
                "Owner Team",
                "Location",
                "Buyer",
                "Admin Record",
                "Remark",
                "Time",
                "History"
        )
        val DEFAULT_PATH = rootPath + File.separator + "DeviceRecord"
        val FILE_NAME = "DeviceRecord.xlsx"
        val EMPTY_RECORD = DeviceRecord("empty", mutableListOf())
    }

    private var mRecord = EMPTY_RECORD

    var modified = false

    fun record(): DeviceRecord {
        mRecord = if (mRecord == EMPTY_RECORD) importExcel() else mRecord
        return mRecord
    }

    fun importExcel(): DeviceRecord {
        return importExcel(File(DEFAULT_PATH + File.separator + FILE_NAME))
    }

    fun importExcel(file: File): DeviceRecord {
        val list = ArrayList<Device>()
        if (file.exists()) {
            val workbook = WorkbookFactory.create(file)
            val sheet = workbook.getSheetAt(0)
            sheet.drop(1).forEach {
                if (!isEmptyRow(it)) {
                    val map = mutableMapOf<String, Any?>(
                            "index" to it.rowNum,
                            "recordId" to it.getCell(0).stringCellValue.ensureNotBlankAndTrim(),
                            "inventoryNumber" to it.getCell(1).stringCellValue.ensureNotBlankAndTrim(),
                            "model" to it.getCell(2).stringCellValue.ensureNotBlankAndTrim(),
                            "brand" to it.getCell(3).stringCellValue.ensureNotBlankAndTrim(),
                            "serialNumber" to it.getCell(4).stringCellValue.ensureNotBlankAndTrim(),
                            "imei" to it.getCell(5).stringCellValue.ensureNotBlankAndTrim(),
                            "description" to it.getCell(6).stringCellValue.ensureNotBlankAndTrim(),
                            "password" to it.getCell(7).stringCellValue.ensureNotBlankAndTrim(),
                            "owner" to it.getCell(8).stringCellValue.ensureNotBlankAndTrim(),
                            "team" to it.getCell(9).stringCellValue.ensureNotBlankAndTrim(),
                            "location" to it.getCell(10).stringCellValue.ensureNotBlankAndTrim(),
                            "buyer" to it.getCell(11).stringCellValue.ensureNotBlankAndTrim(),
                            "adminRecord" to it.getCell(12).stringCellValue.ensureNotBlankAndTrim(),
                            "remark" to it.getCell(13).stringCellValue.ensureNotBlankAndTrim(),
                            "time" to ensureRightTime(it.getCell(14).stringCellValue),
                            "history" to HistoryMapper().convertToHistoryFromString(it.getCell(15).stringCellValue))
                    list.add(Device(map))
                }
            }
            return DeviceRecord(sheet.sheetName, list)
        }
        return EMPTY_RECORD
    }

    fun exportExcel(record: DeviceRecord): Boolean {
        return exportExcel(record, File(DEFAULT_PATH + File.separator + FILE_NAME))
    }

    fun exportExcel(record: DeviceRecord, file: File): Boolean {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet(record.title)
            //title
            val titleRow = sheet.createRow(0)
            ColumnNames.indices.forEach {
                titleRow.createCell(it, CellType.STRING).apply {
                    setCellValue(ColumnNames[it])
                    cellStyle = titleStyle(workbook)
                }
            }
            //content
            record.list.indices.forEach {
                val row = sheet.createRow(it + 1)
                with(record.list[it]) {
                    row.createCell(0, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(recordId) }
                    row.createCell(1, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(inventoryNumber) }
                    row.createCell(2, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(model) }
                    row.createCell(3, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(brand) }
                    row.createCell(4, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(serialNumber) }
                    row.createCell(5, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(imei) }
                    row.createCell(6, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(description) }
                    row.createCell(7, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(password) }
                    row.createCell(8, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(owner) }
                    row.createCell(9, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(team) }
                    row.createCell(10, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(location) }
                    row.createCell(11, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(buyer) }
                    row.createCell(12, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(adminRecord) }
                    row.createCell(13, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(remark) }
                    row.createCell(14, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(time) }
                    row.createCell(15, CellType.STRING).apply { cellStyle = contentStyle(workbook); setCellValue(HistoryMapper().convertToStringFromHistory(history)) }
                }
            }

            //Fit column width
            ColumnNames.indices.forEach {
                var columnWidth = sheet.getColumnWidth(it) / 256
                sheet.forEach { row ->
                    val cell = row.getCell(it)
                    if (cell.cellTypeEnum == CellType.STRING) {
                        val length = cell.stringCellValue.toByteArray().size
                        if (columnWidth < length) {
                            columnWidth = length
                        }
                    }
                }
                //Max width: 25
                val width = if (columnWidth > 25) 25 else columnWidth
                sheet.setColumnWidth(it, (width + 4) * 256)
            }

            //write
            var out: OutputStream? = null
            return try {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                    Log.d(javaClass.simpleName, ">> File path not exist: ${file.parentFile.name}")
                }
                if (file.exists()) {
                    file.delete()
                    Log.d(javaClass.simpleName, ">> Remove exist file")
                }
                out = FileOutputStream(file)
                workbook.write(out)
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                out?.close()
                workbook.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun titleStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.fontHeightInPoints = 9
        font.bold = true
        font.fontName = "Courier New"
        font.color = IndexedColors.WHITE.index
        style.setFont(font)
        style.wrapText = false
        //alignment
        style.setAlignment(HorizontalAlignment.CENTER)
        style.setVerticalAlignment(VerticalAlignment.CENTER)
        //background
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        style.fillForegroundColor = IndexedColors.RED.index
        //border
        style.setBorderTop(BorderStyle.THIN)
        style.setBorderBottom(BorderStyle.THIN)
        style.setBorderLeft(BorderStyle.THIN)
        style.setBorderRight(BorderStyle.THIN)
        style.topBorderColor = IndexedColors.WHITE.index
        style.bottomBorderColor = IndexedColors.WHITE.index
        style.leftBorderColor = IndexedColors.WHITE.index
        style.rightBorderColor = IndexedColors.WHITE.index
        return style
    }

    private fun contentStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.fontHeightInPoints = 8
        font.fontName = "Courier New"
        style.setFont(font)
        style.wrapText = true
        style.setAlignment(HorizontalAlignment.LEFT)
        style.setVerticalAlignment(VerticalAlignment.CENTER)
        return style
    }

    private fun isEmptyRow(row: Row): Boolean {
        row.forEach {
            if (it != null && it.cellTypeEnum != CellType.BLANK) {
                return false
            }
        }
        return true
    }
}