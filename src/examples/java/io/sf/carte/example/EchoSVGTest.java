/*

 Copyright (c) 2017-2024, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */
/*
 * SPDX-License-Identifier: BSD-3-Clause
 */

package io.sf.carte.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import io.sf.carte.echosvg.anim.dom.SVGDOMImplementation;
import io.sf.carte.echosvg.dom.util.SAXDocumentFactory;
import io.sf.carte.echosvg.svggen.SVGGeneratorContext;
import io.sf.carte.echosvg.svggen.SVGGeneratorContext.GraphicContextDefaults;
import io.sf.carte.echosvg.svggen.SVGGraphics2D;
import io.sf.carte.echosvg.transcoder.ErrorHandler;
import io.sf.carte.echosvg.transcoder.TranscoderException;
import io.sf.carte.echosvg.transcoder.TranscoderInput;
import io.sf.carte.echosvg.transcoder.TranscoderOutput;
import io.sf.carte.echosvg.transcoder.image.PNGTranscoder;
import io.sf.carte.echosvg.transcoder.util.CSSTranscodingHelper;

/**
 * The purpose of this test is to verify that EchoSVG can be run with the given
 * version of Java, its binaries being correctly downloaded from its repository.
 */
public class EchoSVGTest {

	@Test
	public void testSVGGraphics2D() throws Exception {
		FontDecorationPainter painter = new FontDecorationPainter();

		SVGGraphics2D g2d = createSVGGraphics2D();

		// Set some appropriate dimension
		g2d.setSVGCanvasSize(new Dimension(300, 400));

		painter.paint(g2d);

		CharArrayWriter wri = new CharArrayWriter(1900);
		g2d.stream(wri);

		int len = wri.toCharArray().length;
		assertTrue(len > 1800, "Char array had length of only " + len + ".");
	}

	/**
	 * Creates a <code>SVGGraphics2D</code> with certain defaults.
	 * 
	 * @return the <code>SVGGraphics2D</code>.
	 */
	static SVGGraphics2D createSVGGraphics2D() {
		// We need a Document that holds an SVG root element.
		// First obtain a DocumentBuilder as a way to get it.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}

		// Now the document which is what is needed
		Document doc = builder.newDocument();

		// Create a SVG DTD
		DocumentType dtd = builder.getDOMImplementation().createDocumentType("svg",
				"-//W3C//DTD SVG 1.1//EN", "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
		// And the root element in the SVG namespace
		Element svgRoot = doc.createElementNS("http://www.w3.org/2000/svg", "svg");

		// Append those to the document
		doc.appendChild(dtd);
		doc.appendChild(svgRoot);

		/*
		 * Now the document is ready: let's create some context objects and then the
		 * SVGGraphics2D.
		 */

		// For simplicity, create a generator context with some defaults
		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(doc);
		// Set a comment to put in the SVG documents, overriding the default
		ctx.setComment("Generated by My Application");

		// Set a compression a bit higher than the default
		ctx.setCompressionLevel(6);

		// Create the context defaults, with a default font just in case
		GraphicContextDefaults defaults = new GraphicContextDefaults();
		defaults.setFont(new Font("Arial", Font.PLAIN, 12));
		// Set the defaults
		ctx.setGraphicContextDefaults(defaults);

		return new SVGGraphics2D(ctx, false);
	}

	@Test
	public void testTranscodingHelper() throws Exception {
		PNGTranscoder transcoder = new PNGTranscoder();
		CSSTranscodingHelper helper = new CSSTranscodingHelper(transcoder);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream(700);
		TranscoderOutput output = new TranscoderOutput(ostream);
		ErrorHandler handler = new ExceptionErrorHandler();
		transcoder.setErrorHandler(handler);
		try (Reader re = loadDocumentFromClasspath("/io/sf/carte/example/mermaid/block.svg")) {
			TranscoderInput input = new TranscoderInput(re);
			helper.transcode(input, output);
		}
		assertTrue(ostream.size() > 7000, "Mermaid image had size of only " + ostream.size() + ".");
	}

	@Test
	public void testTranscoding() throws Exception {
		org.w3c.dom.DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		SAXDocumentFactory f = new SAXDocumentFactory(impl);
		Reader re = loadDocumentFromClasspath("/hdrbg.svg");
		org.w3c.dom.Document document = f.createDocument("https://css4j.github.io/imag/hdrbg.svg",
				re);

		PNGTranscoder trans = new PNGTranscoder();
		TranscoderInput input = new TranscoderInput(document);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream(700);
		TranscoderOutput output = new TranscoderOutput(ostream);
		ErrorHandler handler = new ExceptionErrorHandler();
		trans.setErrorHandler(handler);

		String[] text = { "Copyright", "Copyright © 2024 ACME Inc.", "Description",
				"Header background", "Software", "EchoSVG" };

		String[] iText = { "Description", "en", "Description", "My image.", "Description", "es",
				"Descripción", "Mi imagen." };

		trans.addTranscodingHint(PNGTranscoder.KEY_KEYWORD_TEXT, text);

		trans.addTranscodingHint(PNGTranscoder.KEY_COMPRESSED_TEXT, text);

		trans.addTranscodingHint(PNGTranscoder.KEY_INTERNATIONAL_TEXT, iText);

		trans.addTranscodingHint(PNGTranscoder.KEY_COMPRESSION_LEVEL, 4);

		trans.transcode(input, output);

		assertTrue(ostream.size() > 1000, "Image had size of only " + ostream.size() + ".");
	}

	private Reader loadDocumentFromClasspath(String filename) {
		InputStream stream = getClass().getResourceAsStream(filename);
		return stream != null ? new InputStreamReader(stream, StandardCharsets.UTF_8) : null;
	}

	private static class ExceptionErrorHandler implements ErrorHandler {

		@Override
		public void error(TranscoderException ex) throws TranscoderException {
			throw ex;
		}

		@Override
		public void fatalError(TranscoderException ex) throws TranscoderException {
			throw ex;
		}

		@Override
		public void warning(TranscoderException ex) throws TranscoderException {
		}

	}

}
