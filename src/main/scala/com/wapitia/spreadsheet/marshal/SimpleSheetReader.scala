package com.wapitia
package spreadsheet.marshal

/** SimpleSheetReader assumes that some given sheet to parse
 *  contains a single header row followed immediately by the data rows.
 *  The sheet may be a subsection of some entire google sheet, as delivered
 *  by the remote sheet service.
 *  The header row is considered to be the first row in a provided sheet
 *  for which the given nonHeaderFilter function argument returns true
 */
class SimpleSheetReader[A](
    nonHeaderFilter: List[Any] => Boolean,
    blankRowFilter: List[Any] => Boolean,
    rowBuilder: LabelledSheetMarshaller[A])
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
  override def read(rows: List[List[Any]]): List[A] = rows match {
    // skip all initial empty rows
    case row :: rest if nonHeaderFilter(row) => read(rest)
    // top hrow is the one header row, rest are sequential rows of data
    case hrow :: rest                        => parseBody(common.stringsOf(hrow), rest)
    // we got nothin'
    case _                                   => Nil.asInstanceOf[List[A]]
  }

  def parseBody(header: Seq[String], body: List[List[Any]]): List[A] = {

    // TODO CDM
//    body.filter(!blankRowFilter(_)) map { row =>
    body map { row =>
      val rowMarshaller: LabelledRowMarshaller[A] = rowBuilder.startNewRow()
      for ( (k, v) <- header.zip(row) )
        rowMarshaller.set(k, v)
      rowMarshaller.build()
    }
  }
}

object SimpleSheetReader {

  def apply[A](
    nonHeaderFilter: List[Any] => Boolean,
    blankRowFilter: List[Any] => Boolean,
    rowBuilder: LabelledSheetMarshaller[A]): SimpleSheetReader[A] =
      new SimpleSheetReader(nonHeaderFilter, blankRowFilter, rowBuilder)

  /** Create a new SimpleSheetReader with the given rowBuilder instance,
   *  using `isBlankRow` instances for nonHeaderFilter and blankRowFilter.
   */
  def apply[A](rowBuilder: LabelledSheetMarshaller[A]): SimpleSheetReader[A] =
      new SimpleSheetReader(isBlankRow, isBlankRow, rowBuilder)
}
