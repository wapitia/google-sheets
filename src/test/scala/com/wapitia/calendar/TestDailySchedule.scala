package com.wapitia.calendar

import java.time.DayOfWeek
import java.time.LocalDate

object TestDailySchedule extends App {

  // run each test
//  testDailySchedule1()
  testDailySchedule2()

  def testDailySchedule1() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
      .build()

    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 3, 1), LocalDate.of(2018, 2, 25))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 28), LocalDate.of(2018, 2, 25))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 27), LocalDate.of(2018, 2, 25))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 26), LocalDate.of(2018, 2, 25))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 25), LocalDate.of(2018, 2, 25))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 24), LocalDate.of(2018, 2, 18))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 23), LocalDate.of(2018, 2, 18))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 22), LocalDate.of(2018, 2, 18))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 21), LocalDate.of(2018, 2, 18))
    helpAssertAnchorCycleDate(dsched, LocalDate.of(2018, 2, 20), LocalDate.of(2018, 2, 18))

    val biwSched: DailySchedule = DailySchedule.mutipleWeekly(2, DayOfWeek.SUNDAY, 0)
      .withWeekDayOffsetsInCycle((0,DayOfWeek.FRIDAY), (1,DayOfWeek.THURSDAY))
      .build()
    helpAssertAnchorCycleDate(biwSched, LocalDate.of(2018, 2, 25), LocalDate.of(2018, 2, 25))

  }

  def testDailySchedule2() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter(LocalDate.of(2018, 3, 1))
    val actBuf = dateStrm.take(3).toSeq mkString ","
    helpAssertDateSeq(dsched, actBuf, "2018-03-02,2018-03-09,2018-03-16")
  }

  protected def helpAssertDateSeq(dsched: DailySchedule, act: String, exp: String) {
    assert(act == exp, s"For dsched $dsched, expected $exp but got $act")
    println(act.toString)
  }

  protected def helpAssertAnchorCycleDate(dsched: DailySchedule, candidate: LocalDate, exp: LocalDate) = {
    val d1: LocalDate = dsched.cycleAnchorDate(candidate)
    assert(d1 == exp, s"For candidate $candidate, expected $exp but got $d1")
    println(s"Anchor date of $candidate is $d1")
  }

}
