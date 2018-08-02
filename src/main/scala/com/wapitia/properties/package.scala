package com.wapitia

import java.util.{Properties => JavaProperties}

/** Extension to Java Properties functionality */
package object properties {
  // TODO this is screaming for macros

  // == java.io.InputStream interface ==

  /** Create and load a new `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadJavaProperties(is: java.io.InputStream): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(is, javaProps)
    javaProps
  }

  /** Create and load a new `java.util.Properties` instance from an `InputStream`
   *  with some base `defaults`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadJavaProperties(is: java.io.InputStream, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(is, javaProps)
    javaProps
  }

  /** Populate an existing `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadIntoJavaProperties(is: java.io.InputStream, javaProps: JavaProperties) {
    javaProps.load(is)
  }

  // == java.io.Reader interface ==

  /** Load a new `java.util.Properties` instance from a `Reader`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadJavaProperties(reader: java.io.Reader): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(reader, javaProps)
    javaProps
  }

  /** Load a new `java.util.Properties` instance from a `Reader` with some base `defaults`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadJavaProperties(reader: java.io.Reader, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(reader, javaProps)
    javaProps
  }

  /** Populate an existing `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[java.io.IOException])
  def loadIntoJavaProperties(reader: java.io.Reader, javaProps: JavaProperties) {
    javaProps.load(reader)
  }

  // == Map[String,String] interface ==

  /** Load a new `java.util.Properties` instance from a `Map[String,String]`.
   *  @throws IOException if the input read cannot be read.
   */
  def loadJavaProperties(mappedProps: Map[String,String]): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(mappedProps, javaProps)
    javaProps
  }

  /** Load a new `java.util.Properties` instance from a `Map[String,String]` with some base `defaults`.
   *  @throws IOException if the input read cannot be read.
   */
  def loadJavaProperties(mappedProps: Map[String,String], defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(mappedProps, javaProps)
    javaProps
  }

  /** Populate an existing `java.util.Properties` instance from a `Map[String,String]`.
   *  @throws IOException if the input read cannot be read.
   */
  def loadIntoJavaProperties(mappedProps: Map[String,String], javaProps: JavaProperties) {
    mappedProps map { case (k,v) => javaProps.setProperty(k, v) }
  }
}
