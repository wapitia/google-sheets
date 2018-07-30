package com.wapitia
package spreadsheet.marshal

/** SimpleSheetReader assumes that some given input spreadsheet
 *  will not be tricky, but rather will be of the form where
 *  the first non-empty row is the single header row followed
 *  by the data rows where each row is its own record.
 *
 *  The sheet may be a subsection of some larger spreadsheet, as delivered
 *  by the remote sheet service.
 *
 *  The header row is considered to be the first row in a provided sheet
 *  for which the given `headerFilter` function is `true`.
 *
 *  The `keepGoing` filter gives the opportunity to short-circuit the
 *  processing after a certain row. While `keepGoing` returns `true` for
 *  some data row, that row will be processed and will move on to the next
 *  row if there is a next row.
 *  If `keepGoing` returns `false` on some data row, that row and all
 *  subsequent rows (if any) shall be ignored.
 *  If `keepGoing` is always `true` all rows of the incoming spreadsheet
 *  are processed.
 *
 *  The `dataRowFilter` determines which data rows should be processed and
 *  which should be ignored. `dataRowFilter` returns `true` on rows that
 *  are to be processed, and returns `false` on rows that should be
 *  ignored, such as blank rows.
 *  The `keepGoing` filter is applied before the `dataRowFilter`,
 *  meaning that `keepGoing` will consider rows that may be subsequently
 *  filtered out, such as blank rows.
 *
 *  All of these filters take two arguments, the ''row number'' (an `Int`)
 *  and a ``List`` of values which is the ordered sequence of cells along
 *  that given row.
 *  The ''row number'' provided to `headerFilter` is relative to the start
 *  of the spreadsheet, so that the first row of the spreadsheet is
 *  row number 0.
 *  The ''row number'' provided to the `dataRowFilter` and `keepGoing` filters,
 *  however,  is relative to the first data row, so that the row immediately
 *  after the found header row is row number 0.
 *
 *  @tparam A the type of the object to be created for each row.
 *  @tparam B the builder of `A` objects
 *
 *  @param headerFilter  function applied to each row until the header
 *                       row is found.
 *                       The first row for which this returns `true`
 *                       is considered the one header row.
 *  @param dataRowFilter function applied to each data (body) row.
 *                       Starting with the first data row's index as 0,
 *                       returns `true` if the row is to be included
 *                       in the mix, parsed and bound into an object of
 *                       type `A`.
 *                       Returns `false` otherwise, in which case the
 *                       row is skipped, and the process proceeds to the next
 *                       row.
 *  @param keepGoing     Starting with the first data row's index as 0,
 *                       Returns `true` if this row should be processed.
 *                       Returns `false` if this and all subsequent rows
 *                       are to be ignored. That is, tells whether processing
 *                       of rows is to stop prematurely.
 *                       Otherwise processes the spreadsheet until we run off the end.
 *  @param sheetMarshal  parsing and marshalling configuration for the rows,
 *                       supplier of row marshals.
 */
class SimpleSheetReader[A,B](
    headerFilter: (Int, List[Any]) => Boolean,
    dataRowFilter: (Int, List[Any]) => Boolean,
    keepGoing: (Int, List[Any]) => Boolean,
    sheetMarshal: LabelledSheetMarshal[A,B])
  extends SheetReader[A] {

  /**
   * Read the given spreadsheet from a list of lists of cells into a list
   * of the target elements of type `A`.
   * First any rows coming before the one header row are skipped according
   * to `headerFilter`.
   * The header row (List of Strings) is then retained and used to build
   * all subsequent rows until the list is exhausted.
   */
  override def read(rows: Seq[List[Any]]): Seq[A] =
    readIndexed(Stream.continually(0).zip(rows).toList)

  protected def readIndexed(indexedRows: Seq[(Int, List[Any])]): Seq[A] = indexedRows match {
    // skip all initial empty or rather non-header rows
    case Seq((rowNo, row), rest@_*) if ! headerFilter(rowNo, row) =>
      readIndexed(rest)
    // top hrow is the one header row, rest are sequential rows of data
    case Seq((_, hrow), rest@_*)  =>
      // strip away the header row indexing, won't need it anymore since
      // the data row indexes reset to row 0.
      readDataRows(common.stringsOf(hrow), rest map { case (_, row) => row } )
    // we got nothin'
    case _ => Nil.asInstanceOf[List[A]]
  }

  /** Read, parse, marshal and bind each row of the body given its corresponding header names */
  protected def readDataRows(header: List[String], body: Seq[List[Any]]): Seq[A] = {
    // note that this incremental counter restarts at the first data row
    // and had nothing to do with the counter started for the headerFilter
    // in the read function
    Stream.continually(0).zip(body)
      .takeWhile { case (index,row) => keepGoing(index,row) }
      .filter { case (index,row) => dataRowFilter(index,row) }
      .map { case (index,row) =>
        // for each valid indexed data row make a new dedicated row marshaller
        val rowMarshaller = sheetMarshal.makeRow()
        // pair up each header name with the corresponding data cell
        // and marshal that named value into the rowMarshaller, which
        // will be building the resultant output object.
        header.zip(row).foreach { case (title, cell) => rowMarshaller.set(title, cell) }
        // then make the object, the list of each will be the result
        rowMarshaller.make()
      }
  }

}

object SimpleSheetReader {

  /** returns true only if the `row` is not blank. `rowNo` is ignored. */
  def isNonEmptyRow(rowNo: Int, row: List[Any]): Boolean = !isBlankRow(row)

  /** true if any and all cells in the row's list are blank. */
  def isBlankRow(row: List[Any]): Boolean = row match {
    case Nil => true
    case h :: t => isBlankCell(h) && isBlankRow(t)
  }

  /** return true if the given cell is blank.
   *  The cell is considered blank if it is null or an empty string.
   *
   *  In Google sheets, a cell can be empty if and only if it is an
   *  empty java String. Never seen a null.
   */
  def isBlankCell(cell: Any): Boolean = cell match {
    case null => true
    case s: String => s.isEmpty
    case _ => false
  }

  def neverStop(rowNo: Int, row: List[Any]) = true

  def apply[A,B](
      headerFilter: (Int, List[Any]) => Boolean,
      dataRowFilter: (Int, List[Any]) => Boolean,
      keepGoing: (Int, List[Any]) => Boolean,
      rowBuilder: LabelledSheetMarshal[A,B]): SimpleSheetReader[A,B] =
    new SimpleSheetReader(headerFilter, dataRowFilter, keepGoing, rowBuilder)

  /** Create a new `SimpleSheetReader` with the given `rowBuilder` instance,
   *  using `isNonEmptyRow` instances for `headerFilter` and `dataRowFilter`.
   */
  def apply[A,B](rowBuilder: LabelledSheetMarshal[A,B]): SimpleSheetReader[A,B] =
    new SimpleSheetReader(isNonEmptyRow, isNonEmptyRow, neverStop, rowBuilder)
}
