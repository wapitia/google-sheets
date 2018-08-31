package com.wapitia
package calendar

import org.junit.Test
import org.junit.Ignore
import java.time.LocalDate
import org.junit.Assert._

class GenericScheduleBuilderTest {

  @Test
  def testDailySched() {
    val sched: Schedule = ScheduleFactory.builder(Cycle.Daily).build();
    val dateStrm: Stream[LocalDate] = sched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-01","2018-03-02","2018-03-03","2018-03-04",
        "2018-03-05","2018-03-06","2018-03-07")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test // @Ignore
  def testBidailySched1() {
    val builder = new DailyScheduleBuilder[Schedule](DailyCycle(2, 0), None)
    val sched: Schedule = builder.build()
    val dateStrm: Stream[LocalDate] = sched.onOrAfter("2018-03-01")
//    val expecteds = Stream[LocalDate]("2018-03-01","2018-03-03","2018-03-05","2018-03-07",
//        "2018-03-09","2018-03-11","2018-03-13")
    val expecteds = Stream[LocalDate]("2018-03-02","2018-03-04","2018-03-06","2018-03-08",
        "2018-03-10","2018-03-12","2018-03-14")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test // @Ignore
  def testBidailySched2() {
    val builder = new DailyScheduleBuilder[Schedule](DailyCycle(2, 1), None)
    val sched: Schedule = builder.build()
    val dateStrm: Stream[LocalDate] = sched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-01","2018-03-03","2018-03-05","2018-03-07",
        "2018-03-09","2018-03-11","2018-03-13")
//    val expecteds = Stream[LocalDate]("2018-03-02","2018-03-04","2018-03-06","2018-03-08",
//        "2018-03-10","2018-03-12","2018-03-14")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

}
