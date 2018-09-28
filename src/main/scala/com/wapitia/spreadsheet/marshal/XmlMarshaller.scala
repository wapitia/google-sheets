package com.wapitia
package spreadsheet
package marshal

import org.w3c.dom.Element
import com.wapitia.common.EValue
import com.wapitia.common.Enum

sealed trait XmlItemPackagePolicy extends XmlItemPackagePolicy.Value with EValue[XmlItemPackagePolicy]

object XmlItemPackagePolicy extends Enum[XmlItemPackagePolicy] {
  case object Attr extends XmlItemPackagePolicy
  case object ChildElement extends XmlItemPackagePolicy

  val enumValues = List(Attr, ChildElement)
}

class ElementRowMarshal(
    cellMarshalRepo: CellMarshalRepo,
    resultElt: Element,
    marshalColumnName: String => String,
    packagePolicy: String => XmlItemPackagePolicy)
extends RowMarshal[Element](marshalColumnName) {

  override def make(): Element = resultElt

  override def getCellMarshal[C](name: String): CellMarshal[C] = cellMarshalRepo.getCellMarshal[C](name)

  override def setMarshalled[C](marshalledName: String, value: C) {
    packagePolicy(marshalledName) match {
      case XmlItemPackagePolicy.Attr =>
        resultElt.setAttribute(marshalledName, value.toString())

      case XmlItemPackagePolicy.ChildElement => {
        val childElement = resultElt.getOwnerDocument.createElement(marshalledName)
        childElement.setTextContent(value.toString())
        resultElt.appendChild(childElement)
      }

    }
  }

}

abstract class XmlMarshaller(containerBldr: RowDocElementAccumulator, marshalColumnName: String => String,
    packagePolicy: String => XmlItemPackagePolicy)
extends ConfiguredSheetMarshal[Element] {

  type RM = ElementRowMarshal

//  def marshalColumnName(name: String): String = {
//    name.trim().toLowerCase().map {
//      case ' ' => '-'
//      case ch => ch
//    }
//  }

  override def makeRowMarshaller(): RM = new RM(marshalChainRepo.repoCellMarshal, containerBldr.newResultElement(),
      marshalColumnName, packagePolicy)
}

object XmlMarshaller {

  def setter[C, RM <: RowMarshal[Element]] = (m: RM, name: String, value: C) => m.setMarshalled[C](name, value)

  def xmlNormName(name: String): String = {
    name.trim().toLowerCase().map {
      case ' ' => '-'
      case ch => ch
    }
  }

  def attrPolicy(s: String): XmlItemPackagePolicy = XmlItemPackagePolicy.Attr

  def elementPolicy(s: String): XmlItemPackagePolicy = XmlItemPackagePolicy.ChildElement
}
