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
import io.sf.carte.doc.style.css.CSSElement;
import io.sf.carte.doc.style.css.CSSRule;
import io.sf.carte.doc.style.css.CSSStyleDeclaration;
import io.sf.carte.doc.style.css.CSSUnit;
import io.sf.carte.doc.style.css.CSSValue;
import io.sf.carte.doc.style.css.CSSValue.Type;
import io.sf.carte.doc.style.css.CSSValueSyntax;
import io.sf.carte.doc.style.css.CSSValueSyntax.Match;
import io.sf.carte.doc.style.css.nsac.AttributeCondition;
import io.sf.carte.doc.style.css.nsac.CombinatorSelector;
import io.sf.carte.doc.style.css.nsac.Condition;
import io.sf.carte.doc.style.css.nsac.Condition.ConditionType;
import io.sf.carte.doc.style.css.nsac.ConditionalSelector;
import io.sf.carte.doc.style.css.nsac.ElementSelector;
import io.sf.carte.doc.style.css.nsac.LexicalUnit;
import io.sf.carte.doc.style.css.nsac.LexicalUnit.LexicalType;
import io.sf.carte.doc.style.css.nsac.Selector;
import io.sf.carte.doc.style.css.nsac.Selector.SelectorType;
import io.sf.carte.doc.style.css.nsac.SelectorList;
import io.sf.carte.doc.style.css.nsac.SimpleSelector;
import io.sf.carte.doc.style.css.om.AbstractCSSRule;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleDeclaration;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheet;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.StyleRule;
import io.sf.carte.doc.style.css.om.StyleSheetList;
import io.sf.carte.doc.style.css.parser.SyntaxParser;
import io.sf.carte.doc.style.css.property.LexicalValue;
import io.sf.carte.doc.style.css.property.NumberValue;
import io.sf.carte.doc.style.css.property.StyleValue;
import io.sf.carte.doc.style.css.property.VarValue;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;

/**
 * The purpose of this test is to verify that the css4j examples in the website
 * can be run with the given version of Java, its binaries being correctly
 * downloaded from its repository.
 */
public class Css4jTest {

	private static final String htmlString1 = "<html><head><title>Example</title><style>div>.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	private static final String xmlString1 = "<html><head><title>Example</title><style><![CDATA[div>.myClass{font-size:14pt;color:var(--myColor,#46f)}]]></style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	/*
	 * DOM4J does not serialize the STYLE element as raw, nor is
	 * being appropriately transformed by the Transformer.
	 */
	private static final String dom4jXmlString1 = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Example</title><style>div&gt;.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	/*
	 * The DOM4J namespace DOM bug
	 */
	private static final String dom4jXmlString1_bug = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Example</title><style>div&gt;.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p xmlns=\"\" id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

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

		/*
		 * Access style sheet(s) and its rule(s)
		 */
		// Obtain the list of CSS style sheets
		StyleSheetList sheets = document.getStyleSheets();
		// Access the first sheet
		AbstractCSSStyleSheet sheet = sheets.item(0);
		// The first rule in the sheet
		AbstractCSSRule rule = sheet.getCssRules().item(0);
		// What kind of rule is it?
		int ruleType = rule.getType();
		// It is a style rule (CSSRule.STYLE_RULE)
		assertEquals(CSSRule.STYLE_RULE, ruleType);

		StyleRule styleRule = (StyleRule) rule;
		// Obtain the selector list
		SelectorList selist = styleRule.getSelectorList();
		// First (and only) selector
		Selector selector = selist.item(0);
		// Selector type (which we can use to cast the selector)
		SelectorType selType = selector.getSelectorType();
		// Type is SelectorType.CHILD (div > .myClass)
		assertEquals(SelectorType.CHILD, selType);

		// Cast to CombinatorSelector
		CombinatorSelector comb = (CombinatorSelector) selector;
		// Obtain the two selectors that are combined by ">"
		Selector firstSel = comb.getSelector();
		SimpleSelector secondSel = comb.getSecondSelector();

		// Examine the first selector
		SelectorType firstSelType = firstSel.getSelectorType();
		// Type is SelectorType.ELEMENT (div)
		assertEquals(SelectorType.ELEMENT, firstSelType);

		// Cast to ElementSelector (see ELEMENT javadoc)
		ElementSelector elemSel = (ElementSelector) firstSel;
		String elemName = elemSel.getLocalName();
		// It is "div"
		assertEquals("div", elemName);

		// Now the second selector
		SelectorType secondSelType = secondSel.getSelectorType();
		// Type is SelectorType.CONDITIONAL (.myClass)
		assertEquals(SelectorType.CONDITIONAL, secondSelType);

		// Cast to ConditionalSelector
		ConditionalSelector condSel = (ConditionalSelector) secondSel;
		Condition condition = condSel.getCondition();
		ConditionType condType = condition.getConditionType();
		// The condition type is ConditionType.CLASS
		assertEquals(ConditionType.CLASS, condType);

		// Cast condition to AttributeCondition (as suggested by the CLASS javadoc)
		AttributeCondition attrCond = (AttributeCondition) condition;
		// Retrieve the condition (class) name
		String cssClassName = attrCond.getValue();
		// It is "myClass"
		assertEquals("myClass", cssClassName);

		// Obtain the style from the rule
		AbstractCSSStyleDeclaration style = styleRule.getStyle();
		// Obtain the number of properties
		int pCount = style.getLength();
		// Two properties
		assertEquals(2, pCount);

		/*
		 * The first property
		 */
		// Retrieve the name of the first property
		String pname0 = style.item(0);
		// pname0 is "font-size"
		assertEquals("font-size", pname0);

		// Obtain the value of "font-size"
		StyleValue fontSize = style.getPropertyCSSValue("font-size");

		// Is it a length?
		SyntaxParser synparser = new SyntaxParser();
		CSSValueSyntax syntaxLength = synparser.parseSyntax("<length>");
		Match match0 = fontSize.matches(syntaxLength);
		// Yes it is a length
		assertEquals(Match.TRUE, match0);

		// Let's check the primitive type
		CSSValue.Type primiType0 = fontSize.getPrimitiveType();
		// It is numeric
		assertEquals(CSSValue.Type.NUMERIC, primiType0);

		// Obtain the CSS unit and numeric value
		NumberValue numericValue = (NumberValue) fontSize;
		short unit = numericValue.getUnitType();
		// Unit is typographic points
		assertEquals(CSSUnit.CSS_PT, unit);
		float floatVal = numericValue.getFloatValue(unit);
		// Value is 14 (14pt)
		assertEquals(14f, floatVal, 1e-5);

		/*
		 * The second property
		 */
		// Retrieve the name of the second property
		String pname1 = style.item(1);
		// pname1 is "color"
		assertEquals("color", pname1);
		// Obtain the value of "color"
		StyleValue color = style.getPropertyCSSValue("color");
		// Let's check the global type
		CSSValue.CssType type1 = color.getCssValueType();
		// It is a PROXY, which means that it is either a custom property value
		// (it is not) or a type not known in advance
		assertEquals(CSSValue.CssType.PROXY, type1);

		// But may it be a color?
		CSSValueSyntax syntaxColor = synparser.parseSyntax("<color>");
		Match match1 = color.matches(syntaxColor);
		// Yes it could be a color, but we do not know for sure
		// until the value is computed
		assertEquals(Match.PENDING, match1);

		// Check the primitive type
		CSSValue.Type primiType1 = color.getPrimitiveType();
		// It is a var()
		assertEquals(CSSValue.Type.VAR, primiType1);
		// Obtain the custom property name
		VarValue varValue = (VarValue) color;
		String customPropertyName = varValue.getName();
		// Name is --myColor
		assertEquals("--myColor", customPropertyName);

		// In this case we have a fallback value (otherwise next line returns null)
		LexicalUnit lexUnit = varValue.getFallback();
		// Let's see its unit type
		LexicalType luType = lexUnit.getLexicalUnitType();
		// It is a RGBCOLOR
		assertEquals(LexicalType.RGBCOLOR, luType);
		// And the color is...
		String strColor = lexUnit.getCssText();
		// Is '#46f'
		assertEquals("#46f", strColor);

		// Access the color components:
		// RGB colors are accessed as a rgb() function, so we retrieve
		// the function parameters:
		LexicalUnit firstComponent = lexUnit.getParameters();
		// Let's see the lexical type to see how we access the value
		LexicalType firstLexUnitType = firstComponent.getLexicalUnitType();
		// It is a LexicalType.INTEGER (RGB components could be PERCENTAGE as well)
		assertEquals(LexicalType.INTEGER, firstLexUnitType);

		// Therefore we use getIntegerValue()
		int color_R = firstComponent.getIntegerValue();
		// Is 0x44 hex (68 decimal)
		assertEquals(0x44, color_R);

		// Second component is the next in the linked list.
		// Beware that if the color was specified with commas,
		// like "rgb(20, 30, 40)", the next one could be a comma!
		LexicalUnit secondComponent = firstComponent.getNextLexicalUnit();
		int color_G = secondComponent.getIntegerValue();
		// Is 0x66 (102 decimal)
		assertEquals(0x66, color_G);

		// Third component
		LexicalUnit thirdComponent = secondComponent.getNextLexicalUnit();
		int color_B = thirdComponent.getIntegerValue();
		// Is 0xff (255 decimal)
		assertEquals(0xff, color_B);

		/*
		 * Set another property, with important priority
		 */
		style.setProperty("background-color", "#d0dfee7a", "important");

		pCount = style.getLength();
		// Now we have three properties
		assertEquals(3, pCount);

		/*
		 * Access a style attribute (inline styles)
		 */
		// First obtain the desired element, for example:
		CSSElement element = document.getElementById("hi");
		// Now the inline style. It can be directly accessed
		CSSStyleDeclaration inlineStyle = element.getStyle();

		// How many properties are defined?
		int inlinePCount = inlineStyle.getLength();
		// Just one
		assertEquals(1, inlinePCount);
		// Retrieve its name
		String inlinePname0 = inlineStyle.item(0);
		// It is "--myColor"
		assertEquals("--myColor", inlinePname0);

		// Now get the value
		CSSValue myColor = inlineStyle.getPropertyCSSValue(inlinePname0);

		/*
		 * Handle the custom property
		 */
		// Check the global type
		CSSValue.CssType type = myColor.getCssValueType();
		// Being a custom property, it must be a PROXY
		assertEquals(CSSValue.CssType.PROXY, type);

		// But is it (or could be) a color?
		Match matchCP = myColor.matches(syntaxColor);
		// Match.TRUE: Yes it is for sure!
		assertEquals(Match.TRUE, matchCP);

		// Let's see the primitive type, although...
		CSSValue.Type primiType = myColor.getPrimitiveType();
		// custom properties are *always* Type.LEXICAL values
		assertEquals(CSSValue.Type.LEXICAL, primiType);
		LexicalValue lvColor = (LexicalValue) myColor;
		// Can we know its final value once it is processed?
		Type finalType = lvColor.getFinalType();
		// Yes, as suggested by the result of calling 'matches'
		// it is a Type.COLOR
		assertEquals(CSSValue.Type.COLOR, finalType);

		// Let's retrieve its value
		LexicalUnit lexUnitCP = lvColor.getLexicalUnit();
		// First see its unit type
		LexicalType luTypeCP = lexUnitCP.getLexicalUnitType();
		// It is a RGBCOLOR
		assertEquals(LexicalType.RGBCOLOR, luTypeCP);
		// And the color is...
		String strlexUnitCP = lexUnitCP.getCssText();
		// Is '#54e'. We could also access its components
		assertEquals("#54e", strlexUnitCP);

		// Let's change the value of the custom property
		// We want to set '#667'
		inlineStyle.setProperty(inlinePname0, "#667", null);

		// Check that we successfully assigned the new value:
		String newInlineValue = inlineStyle.getPropertyValue(inlinePname0);
		// Yes it is '#667' as we wanted
		assertEquals("#667", newInlineValue);
	}

	@Test
	public void testUsageDOMWrapper() throws Exception {
		DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

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

		assertEquals(dom4jXmlString1_bug, s);
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
