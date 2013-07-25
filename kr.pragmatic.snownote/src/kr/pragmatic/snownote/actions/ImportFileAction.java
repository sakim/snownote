package kr.pragmatic.snownote.actions;

import java.io.File;

import kr.pragmatic.snownote.core.SnowAttachment;
import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.editors.SnowNoteEditor;
import kr.pragmatic.snownote.editors.SnowNoteEditorInput;
import kr.pragmatic.snownote.views.FilesView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

public class ImportFileAction implements IViewActionDelegate {
	private FilesView fView;

	public void init(IViewPart view) {
		fView = (FilesView) view;
	}

	public void run(IAction action) {
		// get currently opened editor's page
		SnowNoteEditor editor = (SnowNoteEditor) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editor == null)
			return;

		SnowPage page = ((SnowNoteEditorInput) editor.getEditorInput())
				.getSourcePage();

		FileDialog dialog = new FileDialog(Display.getDefault()
				.getActiveShell(), SWT.OPEN | SWT.MULTI);
		dialog.setFilterExtensions(new String[] { "*.gif", "*.jpg", "*.png",
				"*.bmp" });
		String firstFile = dialog.open();

		if (firstFile != null && firstFile.length() > 0) {
			File path = new File(firstFile).getParentFile();
			String[] names = dialog.getFileNames();

			for (String name : names) {
				File file = new File(path, name);
				SnowAttachment a = new SnowAttachment(file);
				page.addAttachment(a);
			}
		}

		fView.setInput(page);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
