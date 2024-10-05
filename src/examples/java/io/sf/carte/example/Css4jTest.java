/*

 Copyright (c) 2017-2021, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import io.sf.carte.doc.dom.CSSDOMImplementation;
import io.sf.carte.doc.dom.HTMLDocument;
import io.sf.carte.doc.dom.XMLDocumentBuilder;
import io.sf.carte.doc.dom4j.XHTMLDocument;
import io.sf.carte.doc.dom4j.XHTMLDocumentFactory;
import io.sf.carte.doc.style.css.BooleanCondition;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.CSSElement;
import io.sf.carte.doc.style.css.CSSRule;
import io.sf.carte.doc.style.css.CSSStyleDeclaration;
import io.sf.carte.doc.style.css.CSSTypedValue;
import io.sf.carte.doc.style.css.CSSUnit;
import io.sf.carte.doc.style.css.CSSValue;
import io.sf.carte.doc.style.css.CSSValue.Type;
import io.sf.carte.doc.style.css.CSSValueSyntax;
import io.sf.carte.doc.style.css.CSSValueSyntax.Match;
import io.sf.carte.doc.style.css.MediaFeaturePredicate;
import io.sf.carte.doc.style.css.MediaQuery;
import io.sf.carte.doc.style.css.MediaQueryList;
import io.sf.carte.doc.style.css.MediaQueryPredicate;
import io.sf.carte.doc.style.css.nsac.AttributeCondition;
import io.sf.carte.doc.style.css.nsac.CombinatorSelector;
import io.sf.carte.doc.style.css.nsac.Condition;
import io.sf.carte.doc.style.css.nsac.Condition.ConditionType;
import io.sf.carte.doc.style.css.nsac.ConditionalSelector;
import io.sf.carte.doc.style.css.nsac.DeclarationCondition;
import io.sf.carte.doc.style.css.nsac.ElementSelector;
import io.sf.carte.doc.style.css.nsac.LexicalUnit;
import io.sf.carte.doc.style.css.nsac.LexicalUnit.LexicalType;
import io.sf.carte.doc.style.css.nsac.Parser;
import io.sf.carte.doc.style.css.nsac.Selector;
import io.sf.carte.doc.style.css.nsac.Selector.SelectorType;
import io.sf.carte.doc.style.css.nsac.SelectorList;
import io.sf.carte.doc.style.css.nsac.SimpleSelector;
import io.sf.carte.doc.style.css.om.AbstractCSSRule;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleDeclaration;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheet;
import io.sf.carte.doc.style.css.om.AbstractCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.MediaFeature;
import io.sf.carte.doc.style.css.om.StyleRule;
import io.sf.carte.doc.style.css.om.StyleSheetList;
import io.sf.carte.doc.style.css.om.SupportsRule;
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

	private static final String htmlString1 =
			"<html><head><title>Example</title><style>div>.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	private static final String xmlString1 =
			"<html><head><title>Example</title><style><![CDATA[div>.myClass{font-size:14pt;color:var(--myColor,#46f)}]]></style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	private static final String htmlTransformedString1 =
			"<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Example</title><style>div>.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	private static final String dom4jHtmlTransformedString1 =
			"<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Example</title><style>div>.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	/*
	 * DOM4J does not serialize the STYLE element as raw, nor is being appropriately
	 * transformed by the Transformer.
	 */
	private static final String dom4jXmlString1 =
			"<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Example</title><style>div&gt;.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	/*
	 * The DOM4J namespace DOM bug
	 */
	private static final String dom4jXmlString1_bug =
			"<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Example</title><style>div&gt;.myClass{font-size:14pt;color:var(--myColor,#46f)}</style></head><body><div><p xmlns=\"\" id=\"hi\" style=\"--myColor: #54e; \">Hi</p></div></body></html>";

	private static final String sheet1 = "html{font-size:12pt}p{font-size:10pt}";

	private static Transformer transformer;

	private static Transformer transformerHTML;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "script style");

		transformerHTML = tf.newTransformer();
		transformerHTML.setOutputProperty(OutputKeys.METHOD, "html");
		transformerHTML.setOutputProperty(OutputKeys.INDENT, "no");
		transformerHTML.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
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

		// Change the value to 23px
		numericValue.setFloatValue(CSSUnit.CSS_PX, 23f);

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
		 * IMPORTANT: once we are done updating a style sheet, if the sheet is internal
		 * to the document (that is, it is located inside a <style> element), the actual
		 * contents of the <style> element will not be updated until you execute
		 * normalize() on that element node:
		 */
		// Obtain the owner node
		org.w3c.dom.Node styleNode = sheet.getOwnerNode();
		assertNotNull(styleNode);

		// Which type is it?
		short nodeType = styleNode.getNodeType();
		// It is a ELEMENT_NODE (could also be a PROCESSING_INSTRUCTION_NODE)
		assertEquals(org.w3c.dom.Node.ELEMENT_NODE, nodeType);

		// Get the name of the element
		String nodeName = styleNode.getNodeName();
		// It is "style", as expected
		assertEquals("style", nodeName);

		// Get the element content (the initial serialized style sheet)
		String preContent = styleNode.getTextContent();

		// Serialize the current style sheet into the inner text node
		styleNode.normalize();

		// Get the normalized element content (the current serialization)
		String afterContent = styleNode.getTextContent();
		// preContent and afterContent are different

		assertNotEquals(preContent, afterContent);
		assertFalse(preContent.contains("important"));
		assertTrue(afterContent.contains("important"));

		/*
		 * Using iterators
		 */
		/*
		 * It is possible to use iterators to retrieve the rules.
		 */
		sheet = sheets.item(0);
		Iterator<AbstractCSSRule> it = sheet.getCssRules().iterator();
		// Assume it.hasNext() returns true
		assertTrue(it.hasNext());

		// Access the first rule in the sheet
		rule = it.next();
		// What kind of rule is it?
		ruleType = rule.getType();
		// It is a style rule (CSSRule.STYLE_RULE)
		assertEquals(CSSRule.STYLE_RULE, ruleType);

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

		// Transform to XHTML
		StringWriter sw = new StringWriter(xmlString1.length());
		transformer.transform(new DOMSource(otherDOMdocument), new StreamResult(sw));

		assertEquals(xmlString1, sw.toString());

		DOMCSSStyleSheetFactory cssFactory = new DOMCSSStyleSheetFactory();
		CSSDocument document = cssFactory.createCSSDocument(otherDOMdocument);

		sw = new StringWriter(xmlString1.length());
		transformer.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(xmlString1, sw.toString());

		// Transform to HTML
		sw = new StringWriter(htmlTransformedString1.length());
		transformerHTML.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(htmlTransformedString1, sw.toString());
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

		// Transform to HTML
		sw = new StringWriter(dom4jHtmlTransformedString1.length());
		transformerHTML.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(dom4jHtmlTransformedString1, sw.toString());
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

		// Transform to HTML
		StringWriter sw = new StringWriter(dom4jHtmlTransformedString1.length());
		transformerHTML.transform(new DOMSource(document), new StreamResult(sw));

		assertEquals(dom4jHtmlTransformedString1, sw.toString());
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
		AbstractCSSStyleSheetFactory cssFactory = new CSSDOMImplementation(
				EnumSet.allOf(Parser.Flag.class));
		AbstractCSSStyleSheet sheet = cssFactory.createStyleSheet(null, null);
		Reader re = new StringReader(sheet1);
		assertTrue(sheet.parseStyleSheet(re));

		assertEquals(sheet1, sheet.toMinifiedString());
	}

	@Test
	public void testUsageMediaQuery() throws Exception {
		// We instantiate a factory based on CSSDOMImplementation,
		// but could do the same with any of the other factories
		AbstractCSSStyleSheetFactory cssFactory = new CSSDOMImplementation();

		/*
		 * Parsing a Media Query
		 */
		MediaQueryList mql = cssFactory.createMediaQueryList("screen and (600px <= width < 1200px)",
				null);
		assertNotNull(mql);

		// How many queries we got?
		int numQueries = mql.getLength();
		// One
		assertEquals(1, numQueries);

		// Get the first and only query
		MediaQuery mediaQ = mql.getMediaQuery(0);
		assertNotNull(mediaQ);

		// The media type
		String medium = mediaQ.getMediaType();
		// It is "screen"
		assertEquals("screen", medium);

		/*
		 * Have a look at the associated boolean condition(s)
		 */
		BooleanCondition cond = mediaQ.getCondition();
		assertNotNull(cond);

		// The condition type
		BooleanCondition.Type condType = cond.getType();
		// It is BooleanCondition.Type.AND
		assertEquals(BooleanCondition.Type.AND, condType);

		// As the AND javadoc suggests, call getSubConditions()
		List<BooleanCondition> andConds = cond.getSubConditions();
		// andConds.size() is 2
		assertEquals(2, andConds.size());

		// The first operand of the AND
		BooleanCondition cond1 = andConds.get(0);
		// The condition type
		BooleanCondition.Type cond1Type = cond1.getType();
		// It is BooleanCondition.Type.PREDICATE
		assertEquals(BooleanCondition.Type.PREDICATE, cond1Type);

		// Now, following the indication from the getCondition() javadoc,
		// we cast the predicate to a MediaQueryPredicate
		MediaQueryPredicate predicate1 = (MediaQueryPredicate) cond1;
		// predicate1.getPredicateType() is MediaQueryPredicate.MEDIA_TYPE
		// predicate1.getName() is "screen"
		assertEquals(MediaQueryPredicate.MEDIA_TYPE, predicate1.getPredicateType());
		assertEquals("screen", predicate1.getName());

		// The second operand of the AND
		BooleanCondition cond2 = andConds.get(1);
		// The condition type
		BooleanCondition.Type cond2Type = cond2.getType();
		// It is BooleanCondition.Type.PREDICATE
		assertEquals(BooleanCondition.Type.PREDICATE, cond2Type);

		// Again, following the indication from the getCondition() javadoc,
		// we cast the predicate to a MediaQueryPredicate
		MediaQueryPredicate predicate2 = (MediaQueryPredicate) cond2;
		// predicate2.getPredicateType() is MediaQueryPredicate.MEDIA_FEATURE
		// predicate2.getName() is "width"
		assertEquals(MediaQueryPredicate.MEDIA_FEATURE, predicate2.getPredicateType());
		assertEquals("width", predicate2.getName());

		// Cast predicate2 to MediaFeature
		MediaFeature feature = (MediaFeature) predicate2;
		// feature.getRangeType() is MediaFeaturePredicate.FEATURE_LE_AND_LT
		assertEquals(MediaFeaturePredicate.FEATURE_LE_AND_LT, feature.getRangeType());

		// Obtain the first value in the LE_AND_LT range
		CSSTypedValue value = feature.getValue();
		// value.getPrimitiveType() is Type.NUMERIC
		// value.getUnitType() is CSSUnit.CSS_PX (pixels)
		assertEquals(Type.NUMERIC, value.getPrimitiveType());
		assertEquals(CSSUnit.CSS_PX, value.getUnitType());

		// Get the numeric value
		float floatValue = value.getFloatValue(CSSUnit.CSS_PX);
		// Value is 600 (px)
		assertEquals(600f, floatValue, 1e-5);

		// Now the second value in the LE_AND_LT range
		CSSTypedValue value2 = feature.getRangeSecondValue();
		// value.getPrimitiveType() is Type.NUMERIC
		// value.getUnitType() is CSSUnit.CSS_PX
		assertEquals(Type.NUMERIC, value2.getPrimitiveType());
		assertEquals(CSSUnit.CSS_PX, value2.getUnitType());

		// Get the numeric value
		float floatValue2 = value2.getFloatValue(CSSUnit.CSS_PX);
		// Value is 1200 (px)
		assertEquals(1200f, floatValue2, 1e-5);

		// Finally, create a sheet attached to that query
		AbstractCSSStyleSheet sheet = cssFactory.createStyleSheet(null, mql);

		assertSame(mql, sheet.getMedia());
	}

	@Test
	public void testUsageSupportsCondition() throws Exception {
		// Instantiate a css factory (we could also obtain it from a
		// pre-existing css4j object)
		AbstractCSSStyleSheetFactory cssFactory = new CSSDOMImplementation();

		// Create a style sheet
		AbstractCSSStyleSheet sheet = cssFactory.createStyleSheet(null, null);
		// Create a @supports rule
		SupportsRule supports = sheet.createSupportsRule("(display: flex)");

		// Get the condition
		BooleanCondition cond = supports.getCondition();
		assertNotNull(cond);

		// Get the condition type
		BooleanCondition.Type condType = cond.getType();
		// It is a BooleanCondition.Type.PREDICATE
		assertEquals(BooleanCondition.Type.PREDICATE, condType);

		// So we cast it to DeclarationCondition
		DeclarationCondition dCond = (DeclarationCondition) cond;
		String predName = dCond.getName();
		// It is "display"
		assertEquals("display", predName);

		// Now obtain the value
		LexicalUnit value = dCond.getValue();
		LexicalUnit.LexicalType luType = value.getLexicalUnitType();
		// It is a IDENT
		assertEquals(LexicalUnit.LexicalType.IDENT, luType);

		// Get the string value
		String condValue = value.getStringValue();
		// It is "flex"
		assertEquals("flex", condValue);
	}

}
