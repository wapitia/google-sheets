package com.wapitia
package spreadsheet
package marshalx

import com.wapitia.common.marshal.MarshalIn

/** 
 *  @tparam A finished row object or builder/marshaller 
 *            of such row objects
 */
trait LabelledSheetMarshallerX[A] {
  
  def startNewRow(): LabelledRowMarshallerX[A]
}

object LabelledSheetMarshallerX {
  
}
