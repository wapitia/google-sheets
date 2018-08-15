package com.wapitia.financial

/**
 * Financial account holds the name of the account
 * and an optional account number as `String`s.
 */
class Account(name: String, accountNumber: Option[String])

object Account {

  val MissingAccountName = "<Account Name>"

  val UnknownAccount = apply(MissingAccountName)

  def apply(name: String): Account = new Account(name, None)

  def apply(name: String, accountNumber: String): Account = new Account(name, Some(accountNumber))
}
