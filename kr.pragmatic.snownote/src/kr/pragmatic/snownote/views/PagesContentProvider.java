package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;

public class PagesContentProvider extends AbstractPagesContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SnowPage) {
			return ((SnowPage) parentElement).getChildren().toArray();
		}

		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		if (element instanceof SnowPage) {
			return ((SnowPage) element).getParent();
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof SnowPage) {
			return ((SnowPage) element).hasChildren();
		}

		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SnowNote) {
			return ((SnowNote) inputElement).getRootPages().toArray();
		}
		return getChildren(inputElement);
	}
}
