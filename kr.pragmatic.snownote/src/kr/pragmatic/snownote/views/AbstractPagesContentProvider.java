package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.core.SnowNote;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractPagesContentProvider implements
		ITreeContentProvider, IPropertyChangeListener {
	protected StructuredViewer fViewer = null;
	protected static final Object[] NO_CHILDREN = new Object[0];

	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.fViewer = (StructuredViewer) viewer;

		if (oldInput != newInput) { // if not the same

			// remove listener from old - fires even if new is null
			if (oldInput != null) {
				((SnowNote) oldInput).removePropertyChangeListener(this);
			}

			// remove from new - fires even if old is null
			if (newInput != null) {
				((SnowNote) newInput).addPropertyChangeListener(this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		Control ctrl = fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			// Use an asyncExec to run this code on UI thread
			ctrl.getDisplay().asyncExec(new Runnable() {
				public void run() {
					fViewer.refresh();
				}
			});
		}
	}

}
