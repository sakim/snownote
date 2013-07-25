//------------------------------------------------------------------------------
// Copyright (c) 2005, 2007 IBM Corporation and others.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// Contributors:
// IBM Corporation - initial implementation
//------------------------------------------------------------------------------
package kr.pragmatic.snownote.editors.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.epf.richtext.RichTextResources;

/**
 * Models a HTML font style.
 * <h1> 등 수정하고 한글 
 * 
 * @author Kelvin Low, sakim
 * @since 1.0
 */
public class FontStyle {

	// The user friendly names.
	private static final String NAME_H1 = "단락제목1";
	private static final String NAME_H2 = "단락제목2";
	private static final String NAME_H3 = "단락제목3";
	private static final String NAME_H4 = "단락제목4";
	private static final String NAME_H5 = "단락제목5";
	private static final String NAME_H6 = "단락제목6";
	
	private static final String VALUE_H1 = "<h1>";
	private static final String VALUE_H2 = "<h2>";
	private static final String VALUE_H3 = "<h3>";
	private static final String VALUE_H4 = "<h4>";
	private static final String VALUE_H5 = "<h5>";
	private static final String VALUE_H6 = "<h6>";
	
	private static final String NAME_NORMAL = RichTextResources.fontStyle_normal;

	private static final String NAME_SECTION_HEADING = RichTextResources.fontStyle_sectionHeading;

	private static final String NAME_SUBSECTION_HEADING = RichTextResources.fontStyle_subsectionHeading;

	private static final String NAME_SUB_SUBSECTION_HEADING = RichTextResources.fontStyle_subSubsectionHeading;

	private static final String NAME_QUOTE = RichTextResources.fontStyle_quote;

	private static final String NAME_CODE_SAMPLE = RichTextResources.fontStyle_codeSample;

	// The internal values.
	private static final String VALUE_NORMAL = "<p>"; //$NON-NLS-1$

	private static final String VALUE_SECTION_HEADING = "<h3>"; //$NON-NLS-1$

	private static final String VALUE_SUBSECTION_HEADING = "<h4>"; //$NON-NLS-1$

	private static final String VALUE_SUB_SUBSECTION_HEADING = "<h5>"; //$NON-NLS-1$

	private static final String VALUE_QUOTE = "<quote>"; //$NON-NLS-1$

	private static final String VALUE_CODE_SAMPLE = "<code>"; //$NON-NLS-1$
	
	static public final FontStyle H1 = new FontStyle(NAME_H1, VALUE_H1);
	static public final FontStyle H2 = new FontStyle(NAME_H2, VALUE_H2);
	static public final FontStyle H3 = new FontStyle(NAME_H3, VALUE_H3);
	static public final FontStyle H4 = new FontStyle(NAME_H4, VALUE_H4);
	static public final FontStyle H5 = new FontStyle(NAME_H5, VALUE_H5);
	static public final FontStyle H6 = new FontStyle(NAME_H6, VALUE_H6);

	/**
	 * Font style for normal text.
	 */
	static public final FontStyle NORMAL = new FontStyle(NAME_NORMAL,
			VALUE_NORMAL);

	/**
	 * Font style for section heading.
	 */
	static public final FontStyle SECTION_HEADING = new FontStyle(
			NAME_SECTION_HEADING, VALUE_SECTION_HEADING);

	/**
	 * Font style for sub section heading.
	 */
	static public final FontStyle SUBSECTION_HEADING = new FontStyle(
			NAME_SUBSECTION_HEADING, VALUE_SUBSECTION_HEADING);
	/**
	 * Font style for sub sub section heading.
	 */
	static public final FontStyle SUB_SUBSECTION_HEADING = new FontStyle(
			NAME_SUB_SUBSECTION_HEADING, VALUE_SUB_SUBSECTION_HEADING);

	/**
	 * Font style for quotations.
	 */
	static public final FontStyle QUOTE = new FontStyle(NAME_QUOTE, VALUE_QUOTE);

	/**
	 * Font style for displaying program codes.
	 */
	static public final FontStyle CODE_SAMPLE = new FontStyle(NAME_CODE_SAMPLE,
			VALUE_CODE_SAMPLE);

	// A list of <code>FontStyle</code> objects.
	static private final List<FontStyle> FONT_STYLES = new ArrayList<FontStyle>();
	static {
		FONT_STYLES.add(H1);
		FONT_STYLES.add(H2);
		FONT_STYLES.add(H3);
		FONT_STYLES.add(H4);
		FONT_STYLES.add(H5);
		FONT_STYLES.add(H6);
	}

	// The font style name.
	private String name;

	// The font style value.
	private String value;

	/**
	 * Creates a new instance.
	 * 
	 * @param name
	 *            the font style name
	 * @param value
	 *            the font style value
	 */
	public FontStyle(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the font style name.
	 * 
	 * @return the font style name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the font style value.
	 * 
	 * @return the font style value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the <code>FontStyle</code> object that is mapped to the given
	 * index.
	 * 
	 * @param index
	 *            an index into the <code>FontStyle</code> list
	 * @return a <code>FontStyle</code> object
	 */
	public static FontStyle getFontStyle(int index) {
		FontStyle result = (FontStyle) FONT_STYLES.get(index);
		if (result != null) {
			return result;
		}
		return NORMAL;
	}

	/**
	 * Gets the display name of the <code>FontStyle</code> object with the
	 * given value.
	 * 
	 * @param value
	 *            one of the FontStyles
	 * @return a display name of a FontStyle, or null if none found
	 */
	public static String getFontStyleName(String value) {
		for (Iterator<FontStyle> iter = FONT_STYLES.iterator(); iter.hasNext();) {
			FontStyle style = iter.next();
			if (style.getValue().equals(value)) {
				return style.getName();
			}
		}
		return null;
	}

	/**
	 * Gets the value of the <code>FontStyle</code> object with the
	 * given display name.
	 * 
	 * @param name
	 *            one of the FontStyles
	 * @return a value of a FontStyle, or null if none found
	 */
	public static String getFontStyleValue(String name) {
		for (Iterator<FontStyle> iter = FONT_STYLES.iterator(); iter.hasNext();) {
			FontStyle style = iter.next();
			if (style.getName().equals(name)) {
				return style.getValue();
			}
		}
		return null;
	}

}