package kr.pragmatic.snownote.dialogs;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.preferences.PreferenceKeys;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DeleteMessageDialog extends MessageDialog {

	private Button donotAskButton;

	public DeleteMessageDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		donotAskButton = new Button(parent, SWT.CHECK);

		donotAskButton.setText("다시 알리지 않기");
		return donotAskButton;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			IPreferenceStore store = SnowNotePlugin.getDefault()
					.getPreferenceStore();
			store.setValue(PreferenceKeys.DONOT_INFORM_PAGE_DELETION,
					donotAskButton.getSelection());
		}

		super.buttonPressed(buttonId);
	}

	public static void openInformation(Shell parent, String title,
			String message) {
		MessageDialog dialog = new DeleteMessageDialog(
				parent,
				title,
				null, // accept
				// the
				// default
				// window
				// icon
				message, INFORMATION,
				new String[] { IDialogConstants.OK_LABEL }, 0);
		// ok is the default
		dialog.open();
		return;
	}
}
