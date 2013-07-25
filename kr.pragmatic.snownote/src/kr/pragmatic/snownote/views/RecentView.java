package kr.pragmatic.snownote.views;

public class RecentView extends AbstractPagesView {
	public static final String ID = "kr.pragmatic.snownote.view.recent";

	@Override
	protected void createProviders() {
		fContentProvider = new RecentContentProvider();
		fLabelProvider = new PagesLabelProvider(
				PagesLabelProvider.RECENT_PAGES_MODE);
	}

	@Override
	protected void initDragAndDrop() {
		// do nothing
	}

	@Override
	protected void setSorter() {
		// do nothing
	}
}
