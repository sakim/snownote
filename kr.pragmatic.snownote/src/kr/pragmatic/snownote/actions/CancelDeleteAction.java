package kr.pragmatic.snownote.actions;

import java.util.List;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CancelDeleteAction implements IObjectActionDelegate {
	@SuppressWarnings("unchecked")
	private List fSelectionList;

	public CancelDeleteAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		for (Object obj : fSelectionList) {
			if (obj instanceof SnowPage) {
				SnowPage page = (SnowPage) obj;

				page.setDeleted(false);
			}
		}
		SnowNotePlugin.getSnowNote().firePropertyChange();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssl = (IStructuredSelection) selection;
			fSelectionList = ssl.toList();
		}
	}

}
