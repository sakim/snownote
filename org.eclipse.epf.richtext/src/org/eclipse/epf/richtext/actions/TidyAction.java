//------------------------------------------------------------------------------
// Copyright (c) 2005, 2006 IBM Corporation and others.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// Contributors:
// IBM Corporation - initial implementation
//------------------------------------------------------------------------------
package org.eclipse.epf.richtext.actions;

import java.io.UnsupportedEncodingException;

import org.eclipse.epf.common.html.HTMLFormatter;
import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.RichTextEditor;
import org.eclipse.epf.richtext.RichTextImages;
import org.eclipse.epf.richtext.RichTextPlugin;
import org.eclipse.epf.richtext.RichTextResources;
import org.eclipse.jface.action.IAction;

/**
 * Runs JTidy on the HTML
 * 
 * @author Jeff Hardy
 * @since 1.2
 */
public class TidyAction extends RichTextAction {
	
	boolean forceOutput = false;
	boolean makeBare = false;
	boolean word2000 = false;
	
	// The HTML source formatter.
	protected HTMLFormatter htmlFormatter;

	

	/**
	 * Creates a new instance.
	 */
	public TidyAction(IRichText richText, boolean forceOutput, boolean makeBare, boolean word2000) {
		super(richText, IAction.AS_PUSH_BUTTON);
		this.forceOutput = forceOutput;
		this.makeBare = makeBare;
		this.word2000 = word2000;
		// TODO need image
		setImageDescriptor(RichTextImages.IMG_DESC_TIDY);
		// TODO need image
//		setDisabledImageDescriptor(RichTextImages.DISABLED_IMG_DESC_PASTE);
		setToolTipText(RichTextResources.pastePlainTextAction_toolTipText);
		htmlFormatter = new HTMLFormatter();
	}
	
	@Override
	public void execute(IRichText richText) {
		// get current text
		String html;
		if (richText instanceof RichTextEditor) {
			html = ((RichTextEditor)richText).getText();
		} else {
			html = richText.getText();
		}
		// call JTidy with the options
		try {
			html = htmlFormatter.formatHTML(html, false, forceOutput, makeBare, word2000);
		} catch (UnsupportedEncodingException e) {
			RichTextPlugin.getDefault().getLogger().logError(e);
		}
		// set text
		if (richText instanceof RichTextEditor) {
			((RichTextEditor)richText).setText(html);
		} else {
			richText.setText(html);
		}
		richText.checkModify();
		
	}
	
	@Override
	public boolean disableInSourceMode() {
		return false;
	}
}
