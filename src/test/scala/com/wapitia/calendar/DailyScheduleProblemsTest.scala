package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import org.junit.{Test, Ignore}
import org.junit.Assert._

class DailyScheduleProblemsTest {

  @Test  // @TODO
  def testWeeklyMondayScheduleStream() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
      .withWeekDaysInCycle(DayOfWeek.SATURDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-03","2018-03-10","2018-03-17","2018-03-24")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Ignore @Test // TODO
  def testBiWeeklySchedule() {

    val biwSched: DailySchedule = DailySchedule
      .multipleWeekly(2, DayOfWeek.SUNDAY, 0)
      .withWeekDayOffsetsInCycle((0,DayOfWeek.FRIDAY), (1,DayOfWeek.THURSDAY))
      .build()
      ???
  }

}
