package com.wapitia
package financial

class PaymentStream(
  target: String
)

object PaymentStream {

  def builder() = new Builder(
      targetOpt = None)

  class Builder(
      targetOpt: Option[String])
  {

    def target(target: String) = new Builder(Some(target))

    def build() = new PaymentStream(
      target = targetOpt.getOrElse("") )

  }

}
