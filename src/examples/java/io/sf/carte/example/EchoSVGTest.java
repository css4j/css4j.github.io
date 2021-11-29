/*

 Copyright (c) 2017-2021, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.example;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.sf.carte.echosvg.anim.dom.SVGDOMImplementation;
import io.sf.carte.echosvg.dom.util.SAXDocumentFactory;
import io.sf.carte.echosvg.transcoder.ErrorHandler;
import io.sf.carte.echosvg.transcoder.TranscoderException;
import io.sf.carte.echosvg.transcoder.TranscoderInput;
import io.sf.carte.echosvg.transcoder.TranscoderOutput;
import io.sf.carte.echosvg.transcoder.image.PNGTranscoder;

/**
 * The purpose of this test is to verify that EchoSVG can be run with the given
 * version of Java, its binaries being correctly downloaded from its repository.
 */
public class EchoSVGTest {

	@Test
	public void testTranscoding() throws Exception {
		org.w3c.dom.DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		SAXDocumentFactory f = new SAXDocumentFactory(impl);
		Reader re = loadDocumentFromClasspath("/hdrbg.svg");
		org.w3c.dom.Document document = f.createDocument("https://css4j.github.io/imag/hdrbg.svg", re);

		PNGTranscoder trans = new PNGTranscoder();
		TranscoderInput input = new TranscoderInput(document);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream(700);
		TranscoderOutput output = new TranscoderOutput(ostream);
		ErrorHandler handler = new ExceptionErrorHandler();
		trans.setErrorHandler(handler);
		trans.transcode(input, output);

		assertTrue(ostream.size() > 400);
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
