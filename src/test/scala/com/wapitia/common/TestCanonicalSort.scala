package com.wapitia
package common

import com.wapitia.calendar.Cycle

object TestCanonicalSort extends App {
  
   println(canonicalList(4,3,2)) 
   println(canonicalList(1,1,1,3)) 
   println(canonicalList()) 
   println(canonicalList(9,8,3,7,8,3,4,1,4,8))
   println(canonicalList("B","C","B","A"))
   
   def whichCycle(c: Cycle) = c match {
     case Cycle.Monthly => println(s"Each Month, ".format(c.asDays, c.name))
     case Cycle.Daily => println("Every Day")
     case Cycle.BiDaily => println("Every other day")
     case Cycle.Weekly => println("Each Weeek")
     case Cycle.BiWeekly => println("Every other week")
     case Cycle.BiMonthly => println("Every other month")
     case Cycle.Quarterly => println("Each quarter")
     case Cycle.SemiAnnually => println("Every 6 months")
     case Cycle.Annually => println("Each year")
     
   }
   
}