package com.vinayismd.contactify

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat

class MainActivityVM : ViewModel() {

    var uiState by mutableStateOf(UiState(isLoading = false))
        private set

    private val contactListT = mutableStateListOf<Contact>()


    fun handleAction(action: UIAction) {
        when (action) {
            is UIAction.SelectFile -> {
                uiState = uiState.copy(selectFile = true)
            }
            is UIAction.SaveList -> {
                saveList(action.type, action.file)

            }
            is UIAction.ShowInfo -> {
                uiState = uiState.copy(showList = true)
            }
            is UIAction.ShowList -> {

            }
        }
    }

    fun removeElement(contacts: Contact) {
        contactListT.remove(contacts)
        uiState = uiState.copy(contactList = contactListT)
        Log.d("VINAY", contactListT.size.toString())
    }


    fun hideShowList() {
        uiState = uiState.copy(showList = false)
    }

    private fun saveList(type: String, file: File) {
        contactListT.clear()
        when (type) {
            FILETYPE_XLS -> {
                val myInput = FileInputStream(file)
                val myFileSystem = POIFSFileSystem(myInput)
                val workbook = HSSFWorkbook(myFileSystem)
                val formulaEvaluator: FormulaEvaluator =
                    workbook.creationHelper.createFormulaEvaluator()
                workbook.let { wb ->
                    val sheet: Sheet = wb.getSheetAt(0)
                    sheet.forEach { row ->
                        val firstName = getCellAsString(row, 0, formulaEvaluator)
                        val lastName = getCellAsString(row, 1, formulaEvaluator)
                        val contactNumber = getCellAsString(row, 2, formulaEvaluator)
                        contactListT.add(Contact(firstName, lastName, contactNumber))
                    }
                    uiState = uiState.copy(showList = true, contactList = contactListT)
                }
            }

            FILETYPE_XLSX -> {
                val myInput = FileInputStream(file)
                val workbook = XSSFWorkbook(myInput)
                val sheet: XSSFSheet = workbook.getSheetAt(0)
                val rowsCount = sheet.physicalNumberOfRows
                val formulaEvaluator: FormulaEvaluator =
                    workbook.creationHelper.createFormulaEvaluator()
                for (r in 0 until rowsCount) {
                    val row: Row = sheet.getRow(r)
                    val firstName = getCellAsString(row, 0, formulaEvaluator)
                    val lastName = getCellAsString(row, 1, formulaEvaluator)
                    val contactNumber = getCellAsString(row, 2, formulaEvaluator)
                    contactListT.add(Contact(firstName, lastName, contactNumber))
                }
                uiState = uiState.copy(showList = true, contactList = contactListT)
            }
        }
    }

    private fun getCellAsString(row: Row, c: Int, formulaEvaluator: FormulaEvaluator): String {
        var value = ""
        try {
            val cell = row.getCell(c)
            val cellValue = formulaEvaluator.evaluate(cell)
            when (cellValue.cellType) {
                Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.booleanValue
                Cell.CELL_TYPE_NUMERIC -> {
                    val numericValue = (formulaEvaluator.evaluateInCell(cell) as XSSFCell).rawValue
                    value = if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val date = cellValue.numberValue
                        val formatter = SimpleDateFormat("dd/MM/yy")
                        formatter.format(HSSFDateUtil.getJavaDate(date))
                    } else {
                        "" + numericValue
                    }
                }
                Cell.CELL_TYPE_STRING -> value = "" + cellValue.stringValue
                else -> {
                }
            }
        } catch (e: NullPointerException) {

        }
        return value
    }

    data class UiState(
        val isLoading: Boolean = false,
        val selectFile: Boolean = false,
        val showList: Boolean = false,
        val contactList: List<Contact> = listOf()
    )


    sealed class UIAction {
        object SelectFile : UIAction()
        data class SaveList(val type: String, val file: File) : UIAction()
        object ShowList : UIAction()
        object ShowInfo : UIAction()
    }


    companion object {
        const val FILETYPE_XLS = "application/vnd.ms-excel"
        const val FILETYPE_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        const val FILETYPE_CSV = "text/comma-separated-values"
    }

}