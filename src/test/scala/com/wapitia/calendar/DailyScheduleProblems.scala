package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import org.junit.{Test, Ignore}
import org.junit.Assert._

class DailyScheduleProblems {

  @Test  // @TODO
  def testWeeklySundayScheduleStream() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
    val expecteds = List[LocalDate]("2018-03-02","2018-03-09","2018-03-16","2018-03-23").toStream
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Ignore @Test // TODO
  def testBiWeeklySchedule() {

    val biwSched: DailySchedule = DailySchedule
      .mutipleWeekly(2, DayOfWeek.SUNDAY, 0)
      .withWeekDayOffsetsInCycle((0,DayOfWeek.FRIDAY), (1,DayOfWeek.THURSDAY))
      .build()
      ???
  }

}
