package kr.pragmatic.snownote.views.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

public class PageDragAdapter extends DragSourceAdapter {
	private StructuredViewer fViewer;

	public PageDragAdapter(StructuredViewer viewer) {
		fViewer = viewer;
	}

	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) fViewer
				.getSelection();
		if (selection.isEmpty()) {
			event.doit = false;
			return;
		}
		super.dragStart(event);
	}
}
