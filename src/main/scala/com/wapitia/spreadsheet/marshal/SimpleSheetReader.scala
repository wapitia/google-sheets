package com.wapitia
package spreadsheet.marshal

/** SimpleSheetReader assumes that some given input spreadsheet
 *  will not be tricky, but rather will be of the form where
 *  the first non-empty row is the single header row followed 
 *  by the data rows.
 *  The sheet may be a subsection of some entire google sheet, as delivered
 *  by the remote sheet service.
 *  The header row is considered to be the first row in a provided sheet
 *  for which the given `nonHeaderFilter` function argument returns `true`.
 *  
 *  @tparam A the type of the object to be created for each row.
 *  @tparam B the builder of `A` objects
 *
 *   TODO Fix the backwards filter logic  
 *  @param nonHeaderFilter function applied to each row until the header
 *                         row is found.
 *                         The first row for which this returns `false` 
 *                         is considered the one header row.
 *  @param blankRowFilter  function applied to each data (body) row.
 *                         Returns `true` if the row is _not_ to be included
 *                         in the mix.
 *  @param killSwitch      Starting with the first data row's index as 0,
 *                         Returns `true` if this row and all subsequent rows
 *                         are to be ignored. That is, tells whether processing
 *                         of rows is to stop prematurely.
 *                         Otherwise processes the spreadsheet until we run off the end. 
 *  @param sheetMarshal    parsing and marshalling configuration for the rows,
 *                         supplier of row marshals.                                                                               
 */
// TODO Switch the logic of the filters
// TODO Make filters indexed
class SimpleSheetReader[A,B](
    nonHeaderFilter: List[Any] => Boolean,
    blankRowFilter: List[Any] => Boolean,
    killSwitch: (Int, List[Any]) => Boolean,
    sheetMarshal: LabelledSheetMarshal[A,B])
extends SheetReader[A]
{
  /**
   * Read the given spreadsheet from a list of lists of cells into a list
   * of the target elements of type `A`.
   * First any rows coming before the one header row are skipped according
   * to the logic given in the `nonHeaderFilter`.
   * Second the header row (List of Strings) is retained and used to build
   * all subsequent rows until the list is exhausted.
   */
  override def read(rows: List[List[Any]]): List[A] =
    readIndexed(Stream.continually(0).zip(rows).toList)
  
  protected def readIndexed(indexedRows: List[(Int, List[Any])]): List[A] = indexedRows match {
    // skip all initial empty rows
    case (rowNo, row) :: rest if nonHeaderFilter(row) => 
      readIndexed(rest)
    // top hrow is the one header row, rest are sequential rows of data
    case (rowNo, hrow) :: rest => 
      parseBody(common.stringsOf(hrow), rest map { case (_, row) => row } )
    // we got nothin'
    case _ => 
      Nil.asInstanceOf[List[A]]
  }

  /** Read, parse, marshal and bind each row of the body given its corresponding header names */
  // TODO Make blankRowFilterIndexed, put after the index zip
  protected def parseBody(header: Seq[String], body: List[List[Any]]): List[A] = {

    body.filter( ! blankRowFilter(_))
      .zip(Stream.continually(0))
      .takeWhile { case (row,index) => !killSwitch(index,row) } 
      .map {
        case (row,index) => {
          val rowMarshaller = sheetMarshal.makeRow()
          header.zip(row).foreach { case (title, cell) => rowMarshaller.set(title, cell) }
          rowMarshaller.make()
        }
      }
  }
}

object SimpleSheetReader {
  
  def noKill(rowNo: Int, row: List[Any]) = false

  def apply[A,B](
    nonHeaderFilter: List[Any] => Boolean,
    blankRowFilter: List[Any] => Boolean,
    killSwitch: (Int, List[Any]) => Boolean,
    rowBuilder: LabelledSheetMarshal[A,B]): SimpleSheetReader[A,B] =
      new SimpleSheetReader(nonHeaderFilter, blankRowFilter, killSwitch, rowBuilder)

  /** Create a new SimpleSheetReader with the given rowBuilder instance,
   *  using `isBlankRow` instances for nonHeaderFilter and blankRowFilter.
   */
  def apply[A,B](rowBuilder: LabelledSheetMarshal[A,B]): SimpleSheetReader[A,B] =
      new SimpleSheetReader(isBlankRow, isBlankRow, noKill, rowBuilder)
}
