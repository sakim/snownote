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

import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.RichTextCommand;
import org.eclipse.epf.richtext.RichTextImages;
import org.eclipse.epf.richtext.RichTextPlugin;
import org.eclipse.epf.richtext.RichTextResources;
import org.eclipse.jface.action.IAction;

/**
 * Clears the content of a rich text control.
 * 
 * @author Kelvin Low
 * @since 1.0
 */
public class ClearContentAction extends RichTextAction {

	/**
	 * Creates a new instance.
	 */
	public ClearContentAction(IRichText richText) {
		super(richText, IAction.AS_PUSH_BUTTON);
		setImageDescriptor(RichTextImages.IMG_DESC_CLEAR_CONTENT);
		setDisabledImageDescriptor(RichTextImages.DISABLED_IMG_DESC_CLEAR_CONTENT);
		setToolTipText(RichTextResources.clearContentAction_toolTipText); 
	}

	/**
	 * Executes the action.
	 * 
	 * @param richText
	 *            s rich text control
	 */
	public void execute(IRichText richText) {
		if (richText != null) {
			if (RichTextPlugin.getDefault().getMsgDialog().displayPrompt(
					RichTextResources.clearContentDialog_title,
					RichTextResources.clearContentDialog_text)) {
				richText.executeCommand(RichTextCommand.CLEAR_CONTENT);
			}
		}
	}

}
