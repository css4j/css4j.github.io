/*

 Copyright (c) 2017-2021, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import io.sf.carte.doc.dom.CSSDOMImplementation;
import io.sf.carte.doc.dom.HTMLDocument;
import io.sf.carte.doc.dom.XMLDocumentBuilder;
import io.sf.carte.doc.dom4j.XHTMLDocument;
import io.sf.carte.doc.dom4j.XHTMLDocumentFactory;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheet;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory;
import io.sf.carte.doc.xml.dtd.DefaultEntityResolver;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;

/**
 * The purpose of this test is to verify that the css4j examples in the website
 * can be run with the given version of Java, its binaries being correctly
 * downloaded from its repository.
 */
public class Css4jTest {

	private static final String htmlString1 = "<html><head><title>Example</title><style>div>p{font-size:14pt}</style></head><body><div><p>Hi</p></div></body></html>";

	private static final String xmlString1 = "<html><head><title>Example</title><style><![CDATA[div>p{font-size:14pt}]]></style></head><body><div><p>Hi</p></div></body></html>";

	/*
	 * DOM4J does not serialize the STYLE element as raw, nor is
	 * being appropriately transformed by the Transformer.
	 */
	private static final String dom4jXmlString1 = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Example</title><style>div&gt;p{font-size:14pt}</style></head><body><div><p>Hi</p></div></body></html>";

	private static final String sheet1 = "html{font-size:12pt}p{font-size:10pt}";

	private static Transformer transformer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "script style");
	}

	@Test
	public void testUsageNativeDOM() throws Exception {
		// Instantiate DOM implementation (with default settings: no IE hacks accepted)
		// and configure it
		CSSDOMImplementation impl = new CSSDOMImplementation();
		// Alternatively, impl = new CSSDOMImplementation(flags);
		// Now load default HTML user agent sheets
		impl.setDefaultHTMLUserAgentSheet();
		// Prepare parser
		HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
		parser.setCommentPolicy(XmlViolationPolicy.ALLOW);
		parser.setXmlnsPolicy(XmlViolationPolicy.ALLOW);
		// Prepare builder
		XMLDocumentBuilder builder = new XMLDocumentBuilder(impl);
		builder.setHTMLProcessing(true);
		builder.setXMLReader(parser);

		// Read the document to parse, and prepare source object
		Reader re = new StringReader(htmlString1);
		InputSource source = new InputSource(re);
		// Parse. If the document is not HTML, you want to use DOMDocument instead
		HTMLDocument document = (HTMLDocument) builder.parse(source);
		re.close();

		// Set document URI
		document.setDocumentURI("http://www.example.com/mydocument.html");

		assertEquals(htmlString1, document.toString());
	}

	@Test
	public void testUsageDOMWrapper() throws Exception {
		DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		docbuilder.setEntityResolver(new DefaultEntityResolver());

		// Read the document to parse, and prepare source object
		Reader re = new StringReader(htmlString1);
		InputSource source = new InputSource(re);
		// Parse
		Document otherDOMdocument = docbuilder.parse(source);
		re.close();

		// Set document URI
		otherDOMdocument.setDocumentURI("http://www.example.com/mydocument.html");

		StringWriter sw = new StringWriter(xmlString1.length());
		transformer.transform(new DOMSource(otherDOMdocument), new StreamResult(sw));

		assertEquals(xmlString1, sw.toString());

		DOMCSSStyleSheetFactory cssFactory = new DOMCSSStyleSheetFactory();
		CSSDocument document = cssFactory.createCSSDocument(otherDOMdocument);

		sw = new StringWriter(xmlString1.length());
		transformer.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(xmlString1, sw.toString());
	}

	@Test
	public void testUsageDOM4J() throws Exception {
		XHTMLDocumentFactory factory = XHTMLDocumentFactory.getInstance();
		// Next line is optional: default is TRUE, and is probably what you want
		// factory.getStyleSheetFactory().setLenientSystemValues(false);

		// Prepare parser
		HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
		parser.setCommentPolicy(XmlViolationPolicy.ALLOW);
		parser.setXmlnsPolicy(XmlViolationPolicy.ALLOW);
		// Configure the SAXReader with the HtmlParser
		SAXReader builder = new SAXReader(factory);
		builder.setXMLReader(parser);
		// Provide an error handler to avoid exceptions
		ErrorHandler errorHandler = new DefaultHandler();
		builder.setErrorHandler(errorHandler);
		// We do not set the EntityResolver, the HtmlParser does not need it

		// Read the document to parse
		Reader re = new StringReader(htmlString1);
		XHTMLDocument document = (XHTMLDocument) builder.read(re);
		re.close();

		// Set document URI
		document.setDocumentURI("http://www.example.com/mydocument.html");

		String s = serializeDOM4J(document);

		assertEquals(dom4jXmlString1, s);

		StringWriter sw = new StringWriter(xmlString1.length());
		transformer.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(dom4jXmlString1, sw.toString());
	}

	@Test
	public void testUsageDOM4J_XMLBuilder() throws Exception {
		XHTMLDocumentFactory factory = XHTMLDocumentFactory.getInstance();
		// Next line is optional: default is TRUE, and is probably what you want
		// factory.getStyleSheetFactory().setLenientSystemValues(false);

		// Prepare parser
		HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
		parser.setCommentPolicy(XmlViolationPolicy.ALLOW);
		parser.setXmlnsPolicy(XmlViolationPolicy.ALLOW);
		XMLDocumentBuilder builder = new XMLDocumentBuilder(factory);
		builder.setHTMLProcessing(true);
		builder.setXMLReader(parser);
		// We do not set the EntityResolver, the HtmlParser does not need it

		// Read the document to parse, and prepare source object
		Reader re = new StringReader(htmlString1);
		InputSource source = new InputSource(re);
		XHTMLDocument document = (XHTMLDocument) builder.parse(source);
		re.close();

		// Set document URI
		document.setDocumentURI("http://www.example.com/mydocument.html");

		String s = serializeDOM4J(document);

		assertEquals(dom4jXmlString1, s);
	}

	private String serializeDOM4J(XHTMLDocument document) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setIndent(false);
		format.setNewlines(false);
		format.setNewLineAfterDeclaration(false);
		format.setXHTML(true);
		StringWriter sw = new StringWriter(dom4jXmlString1.length());
		HTMLWriter writer = new HTMLWriter(sw, format);
		writer.write(document);
		return sw.toString();
	}

	@Test
	public void testUsageCSSFactory() throws Exception {
		AbstractCSSStyleSheetFactory cssFactory = new CSSDOMImplementation();
		AbstractCSSStyleSheet sheet = cssFactory.createStyleSheet(null, null);
		Reader re = new StringReader(sheet1);
		assertTrue(sheet.parseStyleSheet(re));
		re.close();

		assertEquals(sheet1, sheet.toMinifiedString());
	}

}
