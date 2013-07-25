package kr.pragmatic.snownote.actions;

import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.editors.SnowNoteEditor;
import kr.pragmatic.snownote.editors.SnowNoteEditorInput;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;

public class OpenEditorAction extends Action {

	private IWorkbenchSite fSite;
	private ISelectionProvider fSpecialSelectionProvider;

	public OpenEditorAction(IWorkbenchPartSite site) {
		fSite = site;
	}

	@Override
	public void run() {
		IWorkbenchPage page = fSite.getWorkbenchWindow().getActivePage();

		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) selection;
			Object fe = iss.getFirstElement();

			if (fe instanceof SnowPage) {
				try {
					page.openEditor(new SnowNoteEditorInput(
							(SnowPage) fe), SnowNoteEditor.ID);

				} catch (PartInitException e) {
					// ignore
				}
			}
		}

	}

	/**
	 * Returns the selection provided by the site owning this action.
	 * 
	 * @return the site's selection
	 */
	public ISelection getSelection() {
		ISelectionProvider selectionProvider = getSelectionProvider();
		if (selectionProvider != null)
			return selectionProvider.getSelection();
		else
			return null;
	}

	/**
	 * Returns the selection provider managed by the site owning this action or
	 * the selection provider explicitly set in
	 * {@link #setSpecialSelectionProvider(ISelectionProvider)}.
	 * 
	 * @return the site's selection provider
	 */
	public ISelectionProvider getSelectionProvider() {
		if (fSpecialSelectionProvider != null) {
			return fSpecialSelectionProvider;
		}
		return fSite.getSelectionProvider();
	}

}
