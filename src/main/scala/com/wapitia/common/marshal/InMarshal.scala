package com.wapitia
package common
package marshal

/** One-directional marshalling of a single type.
 *  A InMarshal instance is typically interjected into
 *  an input stream a translation of the input data
 *  (usually a string of characters) into a particular
 *  expected type.
 *
 *  ValueType is the type of the incoming value. When coming
 *  off of some spreadsheet, this will typically be a String.
 *  Google sheets will return BigDecimal, String, or Null,
 *  which is why this is often instantiated with 'AnyRef in
 *  order to capture all of these types.
 *
 *  BoundType is the type to marshal into, which is the underlying
 *  type of your data model, typically String, LocalDate, BigDecimal,
 *  or some Enum type.
 *
 *  @tparam ValueType incoming contravariant value, usually some String.
 *  @tparam BoundType more elaborately-typed output variable.
 */
trait InMarshal[ValueType,+BoundType] {

  /** Return true if the value is null rather than the bound type.
   *  Many cell values don't have the concept of null, and so this
   *  method returns false by default.
   *  When isNull() returns true, the return value from unmarshal() is undefined.
   *  When isNull() returns false, unmarshal() must return the valid bound type value.
   *
   * @param v
   *      The value to be checked. Can be null.
   */
  def isNull(v: ValueType): Boolean = false

  /** Convert a value type to a bound type.
   *
   * @param v
   *      The value to be converted. Can be null.
   */
  def unmarshal(v: ValueType): BoundType
}
