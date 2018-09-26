package com.wapitia.common

trait ImmutableBuilder[A] {

  def build(): A
}
