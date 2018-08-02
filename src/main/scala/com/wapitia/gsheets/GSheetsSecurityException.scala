package com.wapitia
package gsheets

import java.security.GeneralSecurityException

@SerialVersionUID(1L)
class GSheetsSecurityException(message: String, cause: GeneralSecurityException)
extends GSheetsException[GeneralSecurityException](message, cause)
{
  def this(cause: GeneralSecurityException) { this(cause.getMessage, cause) }
}
