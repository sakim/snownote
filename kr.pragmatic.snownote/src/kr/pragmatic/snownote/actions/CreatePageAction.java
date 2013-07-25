package kr.pragmatic.snownote.actions;

import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.dialogs.CreatePageDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreatePageAction implements IWorkbenchWindowActionDelegate,
		IObjectActionDelegate {
	public static final String ID = "kr.pragmatic.snownote.actions.CreatePageAction";

	private SnowPage fPage;

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		CreatePageDialog dialog = new CreatePageDialog(Display.getDefault()
				.getActiveShell(), fPage);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object element = ((IStructuredSelection) selection).getFirstElement();

		if (element != null && element instanceof SnowPage) {
			fPage = (SnowPage) element;
		}
	}

	public void dispose() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
