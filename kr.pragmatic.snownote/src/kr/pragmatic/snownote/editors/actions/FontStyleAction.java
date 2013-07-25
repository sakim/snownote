//------------------------------------------------------------------------------
package kr.pragmatic.snownote.editors.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kr.pragmatic.snownote.editors.html.FontStyle;

import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.RichTextCommand;
import org.eclipse.epf.richtext.RichTextResources;
import org.eclipse.epf.richtext.actions.RichTextComboAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Sets the font style for the selected text in a rich text control.
 * 
 * @author Kelvin Low
 * @author Jeff Hardy
 * @since 1.0
 */
public class FontStyleAction extends RichTextComboAction {

	/**
	 * Creates a new instance.
	 */
	public FontStyleAction(final IRichText richText) {
		super(richText);
		setToolTipText(RichTextResources.fontStyleAction_toolTipText);

		input = new ArrayList<String>();
		input.add(FontStyle.H1.getName());
		input.add(FontStyle.H2.getName());
		input.add(FontStyle.H3.getName());
		input.add(FontStyle.H4.getName());
		input.add(FontStyle.H5.getName());
		input.add(FontStyle.H6.getName());

		// add listener
		richText.addListener(SWT.SELECTED, new Listener() {
			public void handleEvent(Event event) {
				String blockStyle = richText.getSelected().getBlockStyle();
				String name = FontStyle.getFontStyleName(blockStyle);

				int index = ((List<String>) input).indexOf(name);
				setNotifyListeners(false);
				getCCombo().select(index);
				setNotifyListeners(true);
			}
		});
	}

	/**
	 * Executes the action.
	 * 
	 * @param richText
	 *            a rich text control
	 * @param index
	 *            the index of the selected item
	 */
	public void execute(IRichText richText) {
		if (richText != null) {
			String selected = getCComboSelection();
			String value = FontStyle.getFontStyleValue(selected);
			richText.executeCommand(RichTextCommand.SET_FONT_STYLE, value);
		}
	}

	@Override
	public Collection<String> getInput() {
		return input;
	}
}