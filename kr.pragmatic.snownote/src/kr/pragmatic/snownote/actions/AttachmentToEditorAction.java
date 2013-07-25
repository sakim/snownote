package kr.pragmatic.snownote.actions;

import java.io.File;

import kr.pragmatic.snownote.core.SnowAttachment;
import kr.pragmatic.snownote.editors.SnowNoteEditor;
import kr.pragmatic.snownote.utils.FileUtil;

import org.eclipse.epf.richtext.IRichText;
import org.eclipse.epf.richtext.RichTextCommand;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class AttachmentToEditorAction implements IObjectActionDelegate {
	private SnowAttachment fAttachment;

	public void init(IViewPart view) {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		SnowNoteEditor editor = (SnowNoteEditor) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IRichText richText = editor.getRichTextEditor();

		String path = fAttachment.getPath();
		File file = new File(path);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(editor);
		editor.setFocus();
		
		if (FileUtil.getTypeByName(fAttachment.getName()) == FileUtil.TYPE_PICTURE) { 
			richText.executeCommand(RichTextCommand.ADD_IMAGE, file.getAbsolutePath());
		} else {
//			richText.executeCommand(RichTextCommand.ADD_LINK, file.getAbsolutePath());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			fAttachment = (SnowAttachment) ((IStructuredSelection) selection)
					.getFirstElement();
		}
	}
}
