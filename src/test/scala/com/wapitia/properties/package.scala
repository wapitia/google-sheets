package com.wapitia

import java.util.{Properties => JavaProperties}

package object properties {

  def loadProperties(in: java.io.InputStream): JavaProperties = {
    val props = new JavaProperties
    props.load(in)
    props
  }
}
