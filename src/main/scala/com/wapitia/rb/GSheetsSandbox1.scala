package com.wapitia
package rb

import com.wapitia.financial.TransactionTemplate
import com.wapitia.financial.marshal.TransactionTemplateMarshaller
import com.wapitia.gsheets.GoogleSheetsAccess
import com.wapitia.spreadsheet.SheetReader
import com.wapitia.spreadsheet.SimpleSheetReader
import com.wapitia.spreadsheet.marshal.isBlankRow


object GSheetsSandbox1 extends App {

	val sheetSID = "1qU3idNFQkVUyfpoF7WTOgbeghM_WNlHV-EoW7KY9hPU"
	val sheetRange = "Corey's Budget 2018!A:P"
	
	val acc = new GoogleSheetsAccess("Google Sheets API Java Quickstart", GoogleSheetsAccess.ReadOnlyScopes)
	val values = acc.loadSheetUnformattedValues(sheetSID, sheetRange)
	val rdr = new SimpleSheetReader[TransactionTemplate.Builder](isBlankRow, new TransactionTemplateMarshaller)
	val rows: List[TransactionTemplate] = rdr.parse(values).map(_.build())
	rows.foreach(println)
	
}
