package com.wapitia.financial

import java.time.LocalDate

/** Some definite transaction originating from a [[TransactionTemplate]].
 *
 *  @param item The description of the transaction.
 *  @param date the date of the transaction.
 *  @param amount the amount of the transaction.
 */
class Transaction(
    item: String,
    date: LocalDate,
    amount: BigDecimal,
    source: Account,
    target: Account)

object Transaction {

  /** Create a transaction. The amount may be negative, which would
   *  indicate a positive flow from target back to source.
   */
  def apply(item: String, date: LocalDate, amount: BigDecimal, source: Account, target: Account): Transaction =
    new Transaction(item, date, amount, target, source)

  /** Create a positive-flow transaction, ensuring that the resultant amount
   *  is positive (or non-negative), by flipping the amount and switching
   *  source and target if need-be.
   */
  def positive(item: String, date: LocalDate, amount: BigDecimal, source: Account, target: Account): Transaction =
    if (amount < BigDecimal(0))
      new Transaction(item, date, -amount, target, source)
    else
      new Transaction(item, date, amount, source, target)
}
