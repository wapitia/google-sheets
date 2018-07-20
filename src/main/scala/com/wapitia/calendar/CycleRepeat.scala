package com.wapitia
package calendar

import scala.reflect.ClassTag

trait CycleRepeat {
  
  def cycleIndices: Stream[Int]
}

object CycleRepeat {
  
  def every(fm: Int) = new CycleRepeat {
    def cycleIndices: Stream[Int] = each(fm, 1).cycleIndices
  }
  
  def each(fm: Int, step: Int) = new CycleRepeat {
    def cycleIndices: Stream[Int] = Stream.from(fm, step)
  }
  
}
