package kr.pragmatic.snownote.editors.actions;

import java.io.File;
import java.net.MalformedURLException;

import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.editors.SnowNoteEditor;
import kr.pragmatic.snownote.editors.SnowNoteEditorInput;

import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.RichTextCommand;
import org.eclipse.epf.richtext.RichTextImages;
import org.eclipse.epf.richtext.RichTextPlugin;
import org.eclipse.epf.richtext.actions.RichTextAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * 다수의 이미지를 에디터에 삽입한다.
 * 
 * @author sakim
 * 
 */
public class AddImageAction extends RichTextAction {

	public AddImageAction(IRichText richText) {
		super(richText, IAction.AS_PUSH_BUTTON);
		setImageDescriptor(RichTextImages.IMG_DESC_ADD_IMAGE);
		setDisabledImageDescriptor(RichTextImages.DISABLED_IMG_DESC_ADD_IMAGE);
		setToolTipText("이미지 삽입");
	}

	@Override
	public void execute(IRichText richText) {
		FileDialog dialog = new FileDialog(Display.getDefault()
				.getActiveShell(), SWT.OPEN | SWT.MULTI);
		dialog.setFilterExtensions(new String[] { "*.gif", "*.jpg", "*.bmp" });
		String firstFile = dialog.open();

		if (firstFile != null && firstFile.length() > 0) {
			File path = new File(firstFile).getParentFile();
			String[] names = dialog.getFileNames();

			for (String name : names) {
				File file = new File(path, name);
				try {
					addImage(richText, file.toURL().toExternalForm());
				} catch (MalformedURLException e) {
					RichTextPlugin.getDefault().getLogger().logError(e);
				}
			}
		}
	}

	private void addImage(IRichText richText, String imageURL) {
		if (imageURL.length() > 0) {
			richText.executeCommand(RichTextCommand.ADD_IMAGE, imageURL);
		}
	}

	@Override
	public boolean disableInSourceMode() {
		return false;
	}
}
