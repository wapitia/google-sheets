package com.wapitia
package common
package marshal

/** RuntimeException thrown from the marshalling system. */
@SerialVersionUID(1L)
class MarshalInException(message: String, cause: Exception) extends RuntimeException(message, cause)
{
  def this(cause: Exception) { this(cause.getMessage, cause) }
  def this(message: String) { this(message, null.asInstanceOf[Exception]) }
}
