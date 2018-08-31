package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.DayOfWeek._
import org.junit.{Test, Ignore}
import org.junit.Assert._

class DailyScheduleProblemsTest {

//  @Test
//  def testWeeklyMondayScheduleStream() {
//    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
//      .withWeekDaysInCycle(SATURDAY)
//      .build()
//    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
//    val expecteds = Stream[LocalDate]("2018-03-03","2018-03-10","2018-03-17","2018-03-24")
//    (expecteds zip dateStrm).foreach {
//      case (exp, act) => assertEquals(exp, act)
//    }
//  }

  @Test
  def testBiWeeklySchedule1() {

    val biwSched: DailySchedule = DailySchedule
      .multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInCycle((0,FRIDAY), (1,THURSDAY))
      .build()
    val dateStrm: Stream[LocalDate] = biwSched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-01", "2018-03-09", "2018-03-15", "2018-03-23")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

}
