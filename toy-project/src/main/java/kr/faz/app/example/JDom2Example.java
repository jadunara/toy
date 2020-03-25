package kr.faz.app.example;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import kr.faz.app.utils.ExceptionUtils;


public class JDom2Example {
	public static void main(String[] args) {
		try {
	//	    String xmlFile = "employees.xml";
	//	    Document document = getSAXParsedDocument(xmlFile);
				Document document = getStringDataParsedDocument(getXmlSample());

				Element rootNode = document.getRootElement();
				System.out.println("Root Element :: " + rootNode.getName());

				XMLOutputter xmlOutput = getXMLOutter();

				xpathAllElementExample(document , xmlOutput);//employee element all find
				xpathFindElementValueExample(document, xmlOutput);
				xpathFindElementAttributeExample(document, xmlOutput);//속성(attribute)으로 찾기(find)


			
		} catch (Exception e) {
			String ex = ExceptionUtils.exceptionTraceToString(e);
			System.err.println(ex);
		}
	}

	private static void xpathFindElementAttributeExample(Document document, XMLOutputter xmlOutput) {
		XPathFactory xf = XPathFactory.instance();
		XPathExpression<Element> expr = xf.compile("//employee[@id='103']", Filters.element());
		List<Element> list = expr.evaluate(document);

		System.out.println("노드에서 속성 값으로 찾기 출력 Start \n");
		if ( list != null && list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {

				Element e = list.get(i);
				xmlNodePrint(xmlOutput, e);

			}
		}

		System.out.println("노드에서 속성 값으로 찾기 출력 End \n");

	}

	private static void xpathFindElementValueExample(Document document, XMLOutputter xmlOutput) {
		XPathFactory xf = XPathFactory.instance();

		//normalize-space is trim.(앞뒤 공백 제거)
		XPathExpression<Element> expr = xf.compile("//employee/lastName[normalize-space(text()) = 'Schultz']", Filters.element());
		List<Element> list = expr.evaluate(document);

		System.out.println("특정노드에 있는 값 찾기 출력 Start \n");
		if ( list != null && list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {

				Element e = list.get(i);
				xmlNodePrint(xmlOutput, e.getParentElement());

			}
		}

		System.out.println("특정노드에 있는 값 찾기 출력 End \n");
	}

	private static void xpathAllElementExample(Document document, XMLOutputter xmlOutput) {

		XPathFactory xf = XPathFactory.instance();

		//employee 라는 element 모두 찾음.
		XPathExpression<Element> expr = xf.compile("//employee" , Filters.element());
		List<Element> list = expr.evaluate(document);

		if ( list != null && list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				xmlNodePrint(xmlOutput, list.get(i));
			}
		}

	}

	private static XMLOutputter getXMLOutter() {
		XMLOutputter xmlOutput = new XMLOutputter();
//		Format xmlOutputFormat = Format.getCompactFormat() ;//한줄로(one line output)
		Format xmlOutputFormat = Format.getPrettyFormat() ;
//		Format xmlOutputFormat = Format.getRawFormat() ;//원 모양 그대로(original output)
		xmlOutput.setFormat(xmlOutputFormat);
		return xmlOutput;
	}

	private static void xmlNodePrint(XMLOutputter xmlOutput, Element element) {

		String data = xmlOutput.outputString(element);
		System.out.println(data );

	}

	public static String getXmlSample() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("\n<employees>");
		sb.append("\n    <employee id='101'>");
		sb.append("\n        <firstName>Lokesh</firstName>");
		sb.append("\n        <lastName>Gupta</lastName>");
		sb.append("\n        <country>India</country>");
		sb.append("\n        <department id='25'>");
		sb.append("\n            <name>ITS</name>");
		sb.append("\n        </department>");
		sb.append("\n    </employee>");
		sb.append("\n    <employee id='102'>");
		sb.append("\n        <firstName>Brian</firstName>");
		sb.append("\n        <lastName>  Schultz</lastName>");
		sb.append("\n        <country>USA</country>");
		sb.append("\n        <department id='26'>");
		sb.append("\n            <name>DEV</name>");
		sb.append("\n        </department>");
		sb.append("\n    </employee>");
		sb.append("\n    <employee id='103'>");
		sb.append("\n        <firstName>Quick Brian</firstName>");
		sb.append("\n        <lastName>Quick Schultz</lastName>");
		sb.append("\n        <country>USA</country>");
		sb.append("\n        <department id='29'>");
		sb.append("\n            <name>DEV</name>");
		sb.append("\n        </department>");
		sb.append("\n    </employee>");
		sb.append("\n</employees>");

		return sb.toString();
	}

	private static Document getStringDataParsedDocument(String data) {
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try {
//			StringReader characterStream = new StringReader(data);
//			InputSource characterStream =  null  ;
			document = builder.build(new StringReader(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
		
	}
	private static Document getSAXParsedDocument(final String fileName) {
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try {
			document = builder.build(fileName);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		return document;
	}
}
