package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.BLDR
import com.wapitia.calendar.Cycle

case class AcctMock(acctName: String, cycle: Cycle, date: LocalDate, age: Int, incomes: List[BigDecimal])

object AcctMock {

  def builder() = new Builder(None, None, None, None, Nil)

  class Builder(acctNameOpt: Option[String], cycleOpt: Option[Cycle],
      dateOpt: Option[LocalDate], ageOpt: Option[Int], incomes: List[BigDecimal])
  extends BLDR[AcctMock] {

    def acctName(acctName: String) =
      new Builder(Some(acctName), cycleOpt, dateOpt, ageOpt, incomes)

    def cycle(cycle: Cycle) =
      new Builder(acctNameOpt, Some(cycle), dateOpt, ageOpt, incomes)

    def date(date: LocalDate) =
      new Builder(acctNameOpt, cycleOpt, Some(date), ageOpt, incomes)

    def age(age: Int) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, Some(age), incomes)

    def addIncome(income: BigDecimal) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, ageOpt, incomes :+ income)

    override def build(): AcctMock = new AcctMock(
        acctNameOpt.getOrElse(throw new RuntimeException("Missing Account Name!")),
        cycleOpt.getOrElse(throw new RuntimeException("Missing Cycle!")),
        dateOpt.getOrElse(LocalDate.MIN),
        ageOpt.getOrElse(0),
        incomes)
  }

}
