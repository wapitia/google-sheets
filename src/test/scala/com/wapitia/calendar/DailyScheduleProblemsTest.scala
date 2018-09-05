package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.DayOfWeek._
import org.junit.{Test, Ignore}
import org.junit.Assert._

class DailyScheduleProblemsTest {

  @Test
  def testBiWeeklySchedule1() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((0,FRIDAY), (1,THURSDAY))
      .build()
    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-01", "2018-03-09", "2018-03-15", "2018-03-23")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

}
