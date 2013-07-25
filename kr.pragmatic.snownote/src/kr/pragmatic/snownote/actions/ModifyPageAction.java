package kr.pragmatic.snownote.actions;

import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.dialogs.ModifyPageDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ModifyPageAction implements IObjectActionDelegate,
		IWorkbenchWindowActionDelegate {
	private SnowPage fCurrentPage;

	public ModifyPageAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		ModifyPageDialog dialog = new ModifyPageDialog(Display.getDefault()
				.getActiveShell(), fCurrentPage);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof SnowPage)
				fCurrentPage = (SnowPage) obj;
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

}
