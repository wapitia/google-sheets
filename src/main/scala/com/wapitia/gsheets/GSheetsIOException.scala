package com.wapitia
package gsheets

import java.io.IOException

@SerialVersionUID(1L)
class GSheetsIOException(message: String, cause: IOException)
extends GSheetsException[IOException](message, cause)
{
  def this(cause: IOException) { this(cause.getMessage, cause) }
}
