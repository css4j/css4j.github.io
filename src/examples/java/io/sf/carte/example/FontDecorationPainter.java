/*

 Copyright (c) 2024, contributors to EchoSVG project.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */
/*
 * SPDX-License-Identifier: BSD-3-Clause
 */
package io.sf.carte.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Draw text with decoration attributes.
 */
public class FontDecorationPainter {

	public void paint(Graphics2D g) {
		// Set anti-aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Set a background color
		Color backgroundColor = new Color(0x08081a);
		g.setBackground(backgroundColor);

		// Set default font
		g.setFont(new Font("sans-serif", Font.BOLD, 12));

		// Create a font with the desired attributes, including STRIKETHROUGH
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.FAMILY, "serif");
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		attributes.put(TextAttribute.SIZE, 20);
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		Font fontST = new Font(attributes);

		// A similar font but with UNDERLINE instead of STRIKETHROUGH
		Map<TextAttribute, Object> attributes2 = new HashMap<>(attributes);
		attributes2.remove(TextAttribute.STRIKETHROUGH);
		attributes2.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		Font fontUL = new Font(attributes2);

		// Set the STRIKETHROUGH font and a color
		g.setFont(fontST);
		g.setPaint(new Color(0x666699));
		// Draw a string
		g.drawString("Strike Through", 10, 40);

		// Embed an image
		BufferedImage image = createImage();
		g.drawImage(image, 25, 60, new Color(0xcccccc), null);

		// Now draw with a different color and the UNDERLINE font
		g.setPaint(Color.black);
		g.setFont(fontUL);
		g.translate(0, 30);
		// Draw a new string
		g.drawString("Underline", 10, 70);
	}

	private static BufferedImage createImage() {
		// Create an Image
		// Load a profile
		ICC_Profile prof;
		try (InputStream iccStream = FontDecorationPainter.class.getResourceAsStream(
				"/io/sf/carte/example/profiles/Display P3.icc")) {
			prof = ICC_Profile.getInstance(iccStream);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		ICC_ColorSpace cs = new ICC_ColorSpace(prof);

		// Create the image
		int[] bits = { 16, 16, 16, 16 };
		ComponentColorModel cm = new ComponentColorModel(cs, bits, true, false,
				Transparency.TRANSLUCENT, DataBuffer.TYPE_USHORT);
		WritableRaster raster = cm.createCompatibleWritableRaster(100, 75);
		BufferedImage image = new BufferedImage(cm, raster, false, null);

		Graphics2D ig = image.createGraphics();
		ig.scale(.5, .5);
		ig.setPaint(new Color(128, 0, 0));
		ig.fillRect(0, 0, 100, 50);
		ig.setPaint(Color.orange);
		ig.fillRect(100, 0, 100, 50);
		ig.setPaint(Color.yellow);
		ig.fillRect(0, 50, 100, 50);
		ig.setPaint(Color.red);
		ig.fillRect(100, 50, 100, 50);
		ig.setPaint(new Color(255, 127, 127));
		ig.fillRect(0, 100, 100, 50);
		ig.setPaint(Color.black);
		ig.draw(new Rectangle2D.Double(0.5, 0.5, 199, 149));
		ig.dispose();

		return image;
	}

}
