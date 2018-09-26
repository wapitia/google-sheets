package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.ImmutableBuilder
import com.wapitia.calendar.Cycle
import com.wapitia.spreadsheet.marshal.GSColumn

case class AcctMock(acctName: String, cycle: Cycle, date: LocalDate, age: Int, incomeOpt: Option[BigDecimal])

object AcctMock {

  def builder() = new Builder(None, None, None, None, None)

  class Builder(acctNameOpt: Option[String], cycleOpt: Option[Cycle],
      dateOpt: Option[LocalDate], ageOpt: Option[Int], incomeOpt: Option[BigDecimal])
  extends ImmutableBuilder[AcctMock] {

    @GSColumn("Acct")
    def acctName(acctName: String) =
      new Builder(Some(acctName), cycleOpt, dateOpt, ageOpt, incomeOpt)

    @GSColumn("Cycle")
    def cycle(cycle: Cycle) =
      new Builder(acctNameOpt, Some(cycle), dateOpt, ageOpt, incomeOpt)

    @GSColumn("Date")
    def date(date: LocalDate) =
      new Builder(acctNameOpt, cycleOpt, Some(date), ageOpt, incomeOpt)

    @GSColumn("Age")
    def age(age: Int) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, Some(age), incomeOpt)

    @GSColumn("Income")
    def income(income: BigDecimal) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, ageOpt, Some(income))

    override def build(): AcctMock = new AcctMock(
        acctNameOpt.getOrElse(throw new RuntimeException("Missing Account Name!")),
        cycleOpt.getOrElse(throw new RuntimeException("Missing Cycle!")),
        dateOpt.getOrElse(LocalDate.MIN),
        ageOpt.getOrElse(0),
        incomeOpt)
  }

}
