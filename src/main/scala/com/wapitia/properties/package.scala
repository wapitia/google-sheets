package com.wapitia

import java.util.{Properties => JavaProperties}
import java.io.{InputStream => JInStream, Reader => JReader, IOException => IOE}
import com.wapitia.common.{Enum,EValue}

/** Extension to Java Properties functionality */
package object properties {

  sealed trait InputType extends InputType.Value with EValue[InputType]

  object InputType extends Enum[InputType] {
    case object Flat extends InputType
    case object Xml extends InputType

    val enumValues = List(Flat, Xml)
  }

  // TODO this is screaming for macros

  // == java.io.InputStream interface ==

  /** Create and load a new `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[IOE])
  def loadJavaProperties(is: JInStream): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(is, javaProps, InputType.Flat)
    javaProps
  }

  @throws(classOf[IOE])
  def loadJavaPropertiesFromXML(is: JInStream): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(is, javaProps, InputType.Xml)
    javaProps
  }

  /** Create and load a new `java.util.Properties` instance from an `InputStream`
   *  with some base `defaults`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[IOE])
  def loadJavaProperties(is: JInStream, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(is, javaProps, InputType.Flat)
    javaProps
  }

  @throws(classOf[IOE])
  def loadJavaPropertiesFromXML(is: JInStream, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(is, javaProps, InputType.Xml)
    javaProps
  }

  /** Populate an existing `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input cannot be read.
   */
  @throws(classOf[IOE])
  def loadIntoJavaProperties(is: JInStream, javaProps: JavaProperties) {
    javaProps.load(is)
  }

  @throws(classOf[IOE])
  def loadIntoJavaPropertiesFromXML(is: JInStream, javaProps: JavaProperties) {
    javaProps.loadFromXML(is)
  }

  @throws(classOf[IOE])
  def loadIntoJavaProperties(is: JInStream, javaProps: JavaProperties, inputType: InputType) {
    inputType match {
      case InputType.Flat => loadIntoJavaProperties(is, javaProps)
      case InputType.Xml => loadIntoJavaPropertiesFromXML(is, javaProps)
    }
  }

  // == java.io.Reader interface ==

  /** Load a new `java.util.Properties` instance from a `Reader`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[IOE])
  def loadJavaProperties(reader: JReader): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(reader, javaProps, InputType.Flat)
    javaProps
  }

  @throws(classOf[IOE])
  def loadJavaPropertiesFromXML(reader: JReader): JavaProperties = {
    val javaProps = new JavaProperties()
    loadIntoJavaProperties(reader, javaProps, InputType.Xml)
    javaProps
  }

  /** Load a new `java.util.Properties` instance from a `Reader` with some base `defaults`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[IOE])
  def loadJavaProperties(reader: JReader, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(reader, javaProps, InputType.Flat)
    javaProps
  }

  @throws(classOf[IOE])
  def loadJavaPropertiesFromXML(reader: JReader, defaults: JavaProperties): JavaProperties = {
    val javaProps = new JavaProperties(defaults)
    loadIntoJavaProperties(reader, javaProps, InputType.Xml)
    javaProps
  }

  /** Populate an existing `java.util.Properties` instance from an `InputStream`.
   *  @throws IOException if the input read cannot be read.
   */
  @throws(classOf[IOE])
  def loadIntoJavaProperties(reader: JReader, javaProps: JavaProperties) {
    javaProps.load(reader)
  }

  @throws(classOf[IOE])
  def loadIntoJavaPropertiesFromXML(reader: JReader, javaProps: JavaProperties) {
    javaProps.loadFromXML(com.wapitia.io.ReaderInputStream(reader))
  }

  @throws(classOf[IOE])
  def loadIntoJavaProperties(reader: JReader, javaProps: JavaProperties, inputType: InputType) {
    inputType match {
      case InputType.Flat => loadIntoJavaProperties(reader, javaProps)
      case InputType.Xml => loadIntoJavaPropertiesFromXML(reader, javaProps)
    }
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
