package kr.pragmatic.snownote.views.dnd;

import java.util.Iterator;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.utils.NoteUtil;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class PageDropAdapter extends ViewerDropAdapter {
	private SnowPage targetPage;

	public PageDropAdapter(StructuredViewer viewer) {
		super(viewer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean performDrop(Object data) {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getSelection();
		for (Iterator it = selection.iterator(); it.hasNext();) {
			SnowPage page = ((SnowPage) it.next());

			targetPage.addChild(page);

			page.setModified(true);
			page.setModifiedAt(new java.util.Date());
		}

		SnowNote note = SnowNotePlugin.getSnowNote();
		note.firePropertyChange();

		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		if (!validateHierarchy((SnowPage) target))
			return false;

		if (target instanceof SnowPage) {
			targetPage = (SnowPage) target;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean validateHierarchy(SnowPage target) {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getSelection();
		for (Iterator it = selection.iterator(); it.hasNext();) {
			SnowPage page = (SnowPage) it.next();
			// root?
			if (page.isRoot())
				return false;
			// direct parent?
			if (page.getParent().equals(target))
				return false;
			// self?
			if (page.equals(target))
				return false;
			// child of source?
			if (NoteUtil.isChild(page, target))
				return false;
			// target already has a same titled page
			if (NoteUtil.hasSameTitledChild(target, page.getTitle()))
				return false;
		}

		return true;
	}
}
