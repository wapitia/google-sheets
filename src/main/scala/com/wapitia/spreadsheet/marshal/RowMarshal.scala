package com.wapitia.spreadsheet.marshal

import com.wapitia.common.marshal.InMarshal

/** Accumulator and Builder for each row of data in the spreadsheet */
abstract class RowMarshal[A] {

  import RowMarshal._

  /** Make the instance of the resultant object for this row */
  def make(): A

  def cellMarshal[C](name: String): CellMarshal[C]

  def setMarshalled[C](name: String, value: C): Unit

  /** Set the value for a named slot in the accumulating result object */
  def setRaw[C](name: String, rawvalue: Any): Unit = {

    val cm = cellMarshal[C](name)

    if (!cm.isNull(rawvalue)) {
      val mval: C = cm.unmarshal(rawvalue)
      setMarshalled[C](name, mval)
    }
  }

}

object RowMarshal {

  /** Marshals a cell value from its original value into the internal type before
   *  binding via the binder function.
   */
  type CellMarshal[C] = InMarshal[Any,C]
}
