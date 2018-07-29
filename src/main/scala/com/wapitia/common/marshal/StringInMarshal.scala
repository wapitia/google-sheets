package com.wapitia
package common
package marshal

/** Marshals any incoming object to a string. */
class StringInMarshal extends InMarshal[Any,String] {

  /** Convert the value to string by calling `toString`.
   *
   * @param v The value to be converted. Can be null.
   */
  override def unmarshal(v: Any): String = v.toString
  
}