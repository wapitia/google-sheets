package com.wapitia
package spreadsheet

import com.wapitia.spreadsheet.marshal.LabelledSheetMarshaller
import com.wapitia.spreadsheet.marshal.LabelledRowMarshaller

/** SimpleSheetReader assumes that some given sheet to parse 
 *  contains a single header row followed immediately by the data rows.
 *  The sheet may be a subsection of some entire google sheet, as delivered
 *  by the remote sheet service.
 *  The header row is considered to be the first row in a provided sheet 
 *  for which the given nonHeaderFilter function argument returns true
 */
class SimpleSheetReader[ABldr](nonHeaderFilter: List[AnyRef] => Boolean,
    curbldr: LabelledSheetMarshaller[ABldr]) 
extends SheetReader[ABldr]
{
  override def parse(rows: List[List[AnyRef]]): List[ABldr] = rows match {
    // skip all initial empty rows
    case row :: rest if nonHeaderFilter(row) => parse(rest)
    // top hrow is the one header row, rest are sequential rows of data
    case hrow :: rest                        => parseBody(common.stringsOf(hrow), rest)
    // we got nothin'
    case _                                   => Nil.asInstanceOf[List[ABldr]]
  }
  
  def parseBody(header: Seq[String], body: List[List[AnyRef]]): List[ABldr] = {
    
    body map { row =>
      val rowMarshaller: LabelledRowMarshaller[ABldr] = curbldr.startNewRow()
      for ( (k, v) <- header.zip(row) )
        rowMarshaller.set(k, v)
      rowMarshaller.build()
    }
    
  }

}