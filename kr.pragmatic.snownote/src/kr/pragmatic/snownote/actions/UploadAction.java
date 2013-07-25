package kr.pragmatic.snownote.actions;

import java.lang.reflect.InvocationTargetException;

import kr.pragmatic.snownote.core.Synchronizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class UploadAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;

	public UploadAction() {
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(fWindow
				.getShell());

		try {
			dialog.run(true, true, new Synchronizer(Synchronizer.MODE_UPLOAD));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}
}
