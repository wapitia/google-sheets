package com.wapitia.spreadsheet.marshal

/** Accumulator and Builder for each row of data in the spreadsheet */
abstract class RowMarshal[A](marshalColumnName: String => String) {

  /** Make the instance of the resultant object for this row */
  def make(): A

  /** Get the value marshaller for the given column name */
  def getCellMarshal[C](colName: String): CellMarshal[C]

  def setMarshalled[C](marshalledName: String, marshalledValue: C): Unit

  /** Set the value for a named slot in the accumulating result object */
  def setRaw[C](colName: String, rawvalue: Any): Unit = {

    val cm = getCellMarshal[C](colName)

    if (!cm.isNull(rawvalue)) {
      val mname = marshalColumnName(colName)
      val mval: C = cm.unmarshal(rawvalue)
      setMarshalled[C](mname, mval)
    }
  }

}
