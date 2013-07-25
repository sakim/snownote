package kr.pragmatic.snownote.editors.actions;

import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.actions.RichTextAction;
import org.eclipse.jface.action.IAction;

public class HeadingAction extends RichTextAction {

	public HeadingAction(IRichText richText) {
		super(richText, IAction.AS_PUSH_BUTTON);
		setText("H1");
	}

	@Override
	public void execute(IRichText richText) {
	}

}
