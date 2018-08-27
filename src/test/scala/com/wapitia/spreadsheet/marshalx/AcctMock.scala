package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate
import com.wapitia.calendar.CycleKind

case class AcctMock(acctName: String, cycle: CycleKind, date: LocalDate, age: Int, incomeOpt: Option[BigDecimal])

object AcctMock {

  def builder() = new Builder(None, None, None, None, None)

  class Builder(acctNameOpt: Option[String], cycleOpt: Option[CycleKind],
      dateOpt: Option[LocalDate], ageOpt: Option[Int], incomeOpt: Option[BigDecimal]) {

    def acctName(acctName: String) =
      new Builder(Some(acctName), cycleOpt, dateOpt, ageOpt, incomeOpt)

    def cycle(cycle: CycleKind) =
      new Builder(acctNameOpt, Some(cycle), dateOpt, ageOpt, incomeOpt)

    def date(date: LocalDate) =
      new Builder(acctNameOpt, cycleOpt, Some(date), ageOpt, incomeOpt)

    def age(age: Int) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, Some(age), incomeOpt)

    def income(income: BigDecimal) =
      new Builder(acctNameOpt, cycleOpt, dateOpt, ageOpt, Some(income))

    def build() = new AcctMock(
        acctNameOpt.getOrElse(throw new RuntimeException("Missing Account Name!")),
        cycleOpt.getOrElse(throw new RuntimeException("Missing Cycle!")),
        dateOpt.getOrElse(LocalDate.MIN),
        ageOpt.getOrElse(0),
        incomeOpt)
  }

}
