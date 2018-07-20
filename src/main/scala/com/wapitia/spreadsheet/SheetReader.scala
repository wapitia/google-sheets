package com.wapitia
package spreadsheet

/**
 * A sheet reader converts a spreadsheet of "raw", unformatted cell values 
 * into a list of rows of customized objects.
 * 
 * The incoming spreadsheet may have a header followed by an ordered
 * list of rows.
 * The header and data rows are Lists of AnyRef.
 * An unformatted Cell value will contain either:
 * 
 *  o An empty String representing an empty or Null value
 *  o A BigDecimal representing any number including Dates and DateTimes
 *  o A String representing text and boolean types
 *  o Something else?
 * 
 * Implementations of SheetReader are smart enough to find and parse
 * the header, and then bind each row's cell values to the proper header type
 * 
 * @type A row type
 */
@FunctionalInterface
trait SheetReader[A] {

  /** Parse a list of rows of lists of cells into a list of `A` objects,
   *  where `A` represents the marshalled combined record of one row.
   */
  def parse(rawsheet: List[List[AnyRef]]): List[A]
}
