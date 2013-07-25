package kr.pragmatic.snownote.actions;

import java.lang.reflect.InvocationTargetException;

import kr.pragmatic.snownote.core.Synchronizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SyncAction implements IWorkbenchWindowActionDelegate {
	public static final String ID = "kr.pragmatic.snownote.actions.SyncAction";

	private IWorkbenchWindow fWindow;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(fWindow
				.getShell());

		try {
			dialog.run(true, true, new Synchronizer(Synchronizer.MODE_FULL_SYNC));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
