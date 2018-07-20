package com.wapitia.financial

/**
 * Financial account holds the name of the account
 * and an optional account number as `String`s.
 */
case class Account(name: String, accountNumber: Option[String])

object Account {
  
  val MissingAccountName = "<Account Name>"
  
  val UnknownAccount = apply(MissingAccountName)
  
  def apply(name: String): Account = Account(name, None)
  
}