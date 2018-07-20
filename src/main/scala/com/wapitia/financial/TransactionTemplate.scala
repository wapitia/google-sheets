package com.wapitia
package financial

import java.math.BigDecimal
import java.time.LocalDate
import com.wapitia.calendar.Cycle

/**
 * A transaction template defines a transfer of cash from a source account
 * to a target account.
 * The template does not define a particular start date, but does
 * have slots to describe its cycle offset and an optional final payment date.
 * A specific transaction combines an actual date with the information from
 * this template.
 */
case class TransactionTemplate(
    item: String, 
    nextTrans: LocalDate, 
    amount: BigDecimal,
    cycle: Cycle,
    cycleRefDate: LocalDate,
    maxOpt: Option[BigDecimal],
    lastPmtDateOpt: Option[LocalDate],
    variable: Boolean,
    source: Account,
    target: Account,
    pmtMethod: String, 
    catNDays: Int,
    catNMonths: Int) {
  
  override def toString(): String = {
    "%34s : %10s : %8.2f : %9s | %10s : %10s : %-10s : %-5s : %-8s : %-8s : %-8s : %2d : %2d".format(
        item, nextTrans, amount, 
        cycle, cycleRefDate, 
        maxOpt.map(bd => "%8.2f".format(bd.doubleValue())).getOrElse("-"),
        lastPmtDateOpt.map(ld => "%s".format(ld.toString())).getOrElse("-"), 
        variable, source, target,
        pmtMethod, catNDays, catNMonths)
  }
}

object TransactionTemplate {
  
  def builder() = new Builder(itemOpt = None,  
      nextTransOpt = None, 
      amountOpt = None, 
      cycleOpt = None, 
      cycleRefDateOpt = None, 
      maxOpt = None, 
      lastPmtDateOpt = None, 
      variableOpt = None, 
      sourceOpt = None, 
      targetOpt = None, 
      pmtMethodOpt = None, 
      catNDaysOpt = None, 
      catNMonthsOpt = None)
  
  class Builder(itemOpt: Option[String], 
      nextTransOpt: Option[LocalDate], 
      amountOpt: Option[BigDecimal], 
      cycleOpt: Option[Cycle], 
      cycleRefDateOpt: Option[LocalDate], 
      maxOpt: Option[BigDecimal], 
      lastPmtDateOpt: Option[LocalDate], 
      variableOpt: Option[Boolean], 
      sourceOpt: Option[Account], 
      targetOpt: Option[Account], 
      pmtMethodOpt: Option[String], 
      catNDaysOpt: Option[Int], 
      catNMonthsOpt: Option[Int])
  {
    
    def item(itm: String) = new Builder(Some(itm), nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def nextTrans(nextTrans: LocalDate) = new Builder(itemOpt, Some(nextTrans), amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def amount(amount: BigDecimal) = new Builder(itemOpt, nextTransOpt, Some(amount), cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def cycle(cycle: Cycle) = new Builder(itemOpt, nextTransOpt, amountOpt, Some(cycle),
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def cycleRefDate(cycleRefDate: LocalDate) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        Some(cycleRefDate), maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def max(max: BigDecimal) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, Some(max), lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def lastPmtDate(lastPmtDate: LocalDate) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, Some(lastPmtDate), variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def variable(variable: Boolean) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, Some(variable), sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def source(source: Account) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, Some(source), targetOpt,
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def target(target: Account) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, Some(target),
        pmtMethodOpt, catNDaysOpt, catNMonthsOpt)
    
    def pmtMethod(pmtMethod: String) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        Some(pmtMethod), catNDaysOpt, catNMonthsOpt)
    
    def catNDays(catNDays: Int) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, Some(catNDays), catNMonthsOpt)
    
    def catNMonths(catNMonths: Int) = new Builder(itemOpt, nextTransOpt, amountOpt, cycleOpt,
        cycleRefDateOpt, maxOpt, lastPmtDateOpt, variableOpt, sourceOpt, targetOpt,
        pmtMethodOpt, catNDaysOpt, Some(catNMonths))
      
    def build() = TransactionTemplate(
      item = itemOpt.getOrElse(throw new RuntimeException("Missing Item Name")), 
      nextTrans = nextTransOpt.getOrElse(throw new RuntimeException("Missing Next Transaction Date")), 
      amount = amountOpt.getOrElse(throw new RuntimeException("Missing Transaction Amount")),
      cycle = cycleOpt.getOrElse(throw new RuntimeException("Missing Cycle")),
      cycleRefDate = cycleRefDateOpt.getOrElse(throw new RuntimeException("Missing Cycle Ref Date")),
      maxOpt = maxOpt,
      lastPmtDateOpt = lastPmtDateOpt,
      variable = variableOpt.getOrElse(false),
      source = sourceOpt.getOrElse(Account.UnknownAccount),
      target = targetOpt.getOrElse(Account.UnknownAccount),
      pmtMethod = pmtMethodOpt.getOrElse(""),
      catNDays = catNDaysOpt.getOrElse(1),
      catNMonths = catNMonthsOpt.getOrElse(1) ) 
    
  }
}
