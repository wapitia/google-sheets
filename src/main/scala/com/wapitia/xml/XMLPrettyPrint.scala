package com.wapitia.xml

import java.io.Writer

import org.w3c.dom.Node
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import com.wapitia.common.BLDR
import org.w3c.dom.ls.LSSerializerFilter
import org.w3c.dom.traversal.NodeFilter

class XMLPrettyPrint(xmlDeclaration: Boolean, keepComments: Boolean, newLineOpt: Option[String], filterOpt: Option[LSSerializerFilter]) {

  def configure(serializer: LSSerializer) {
    val domConfig: DOMConfiguration = serializer.getDomConfig
    domConfig.setParameter("format-pretty-print", true)
    domConfig.setParameter("xml-declaration", xmlDeclaration)
    domConfig.setParameter("comments", keepComments)
    newLineOpt.foreach(serializer.setNewLine(_))
    filterOpt.foreach(serializer.setFilter(_))
  }

  def writeNode(node: Node, out: Writer): Boolean = {
    val document = node.getOwnerDocument
    val domls = document.getImplementation.asInstanceOf[DOMImplementationLS]
    val serializer = domls.createLSSerializer()
    configure(serializer)
    val outp = domls.createLSOutput()
    outp.setCharacterStream(out)
    val result = serializer.write(node, outp)
    result
  }

}

object XMLPrettyPrint {

  def apply() = builder().build()

  def builder(): Builder = new Builder(None, None, None, None)

  class Builder(xmlDeclarationOpt: Option[Boolean], keepCommentsOpt: Option[Boolean], newLineOpt: Option[String],
      filterOpt: Option[LSSerializerFilter]) extends BLDR[XMLPrettyPrint] {

    def xmlDeclaration(set: Boolean): Builder = new Builder(Some(set), keepCommentsOpt, newLineOpt, filterOpt)
    def keepComments(set: Boolean): Builder = new Builder(xmlDeclarationOpt, Some(set), newLineOpt, filterOpt)
    def newLine(newLineStr: String): Builder = new Builder(xmlDeclarationOpt, keepCommentsOpt, Some(newLineStr), filterOpt)
    def filter(f: LSSerializerFilter): Builder = new Builder(xmlDeclarationOpt, keepCommentsOpt, newLineOpt, Some(f))

    def build() = new XMLPrettyPrint(
        xmlDeclarationOpt.getOrElse(false),
        keepCommentsOpt.getOrElse(true),
        newLineOpt,
        filterOpt)
  }

  def buildFilter(): FilterBuilder = new FilterBuilder(0)

  class FilterBuilder(whatToShow: Int) extends BLDR[LSSerializerFilter] {

    def showAll: FilterBuilder = new FilterBuilder(NodeFilter.SHOW_ALL)

    def showElement: FilterBuilder = new FilterBuilder(whatToShow | NodeFilter.SHOW_ELEMENT)

    def showAttribute: FilterBuilder = new FilterBuilder(whatToShow | NodeFilter.SHOW_ATTRIBUTE)

    def showText: FilterBuilder = new FilterBuilder(whatToShow | NodeFilter.SHOW_TEXT)

    def showCData: FilterBuilder = new FilterBuilder(whatToShow | NodeFilter.SHOW_CDATA_SECTION)

    def showComment: FilterBuilder = new FilterBuilder(whatToShow | NodeFilter.SHOW_COMMENT)

    def build(): LSSerializerFilter = new LSSerializerFilter {
      def acceptNode(n: Node): Short = NodeFilter.FILTER_ACCEPT
      def getWhatToShow: Int = whatToShow
    }
  }

  def write(node: Node, out: Writer): Boolean = {
    XMLPrettyPrint().writeNode(node, out)
  }
}
