package com.wapitia
package spreadsheet
package marshal

/** A sheet reader converts a spreadsheet of "raw", unformatted cell values
 *  into a list of rows of customized objects.
 *
 *  The incoming spreadsheet may have a header followed by an ordered
 *  list of rows.
 *  The header and data rows are Lists of Any.
 *  In Google Sheets an unformatted cell contains either:
 *
 *  o An empty String representing an empty or Null value
 *  o A BigDecimal representing any number including Dates and DateTimes
 *  o A String representing text and boolean types
 *  o Something else? I haven't seen it
 *
 *  Implementations of SheetReader are smart enough to find and parse
 *  the header, and then bind each row's cell values to the proper header type
 *
 *  @tparam A row type
 */
@FunctionalInterface
trait SheetReader[A] {

  /** Read a list of rows of lists of cells into a sequence of `A` objects,
   *  accumulating them into a dedicated RowAccumulator,
   *  where `A` represents the marshalled combined record of one row.
   */
  def read(rawsheet: Seq[SheetRow], acc: RowAccumulator[A])
}
