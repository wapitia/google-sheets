package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

/** 
 *  @tparam A finished row object or builder/marshaller 
 *            of such row objects
 */
trait LabelledSheetMarshaller[A] {
  
  def startNewRow(): LabelledRowMarshaller[A]
}

object LabelledSheetMarshaller {
  
}
