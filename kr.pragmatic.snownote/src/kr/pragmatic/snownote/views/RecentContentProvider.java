package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.core.RecentPages;
import kr.pragmatic.snownote.core.SnowNote;

public class RecentContentProvider extends AbstractPagesContentProvider {

	private RecentPages fRecent = null;

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String)
			return fRecent.getPages((String) parentElement);
		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof String)
			return true;
		return false;
	}

	public Object[] getElements(Object inputElement) {
		fRecent = new RecentPages((SnowNote) inputElement);

		return fRecent.getCategories();
	}
}
