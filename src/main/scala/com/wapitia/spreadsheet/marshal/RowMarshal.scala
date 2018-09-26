package com.wapitia.spreadsheet.marshal

/** Accumulator and Builder for each row of data in the spreadsheet */
abstract class RowMarshal[A] {

  /** Make the instance of the resultant object for this row */
  def make(): A

  def getCellMarshal[C](name: String): CellMarshal[C]

  def setMarshalled[C](name: String, value: C): Unit

  /** Set the value for a named slot in the accumulating result object */
  def setRaw[C](name: String, rawvalue: Any): Unit = {

    val cm = getCellMarshal[C](name)

    if (!cm.isNull(rawvalue)) {
      val mval: C = cm.unmarshal(rawvalue)
      setMarshalled[C](name, mval)
    }
  }

}
