package kr.pragmatic.snownote.prespectives;

import kr.pragmatic.snownote.views.FilesView;
import kr.pragmatic.snownote.views.PagesView;
import kr.pragmatic.snownote.views.RecentView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	private static final String ID_PAGES_FOLDER = "kr.pragmatic.snownote.folder.pages";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout folder = layout.createFolder(ID_PAGES_FOLDER,
				IPageLayout.LEFT, 0.25f, editorArea);
		folder.addView(PagesView.ID);
		folder.addView(RecentView.ID);

		layout.addView(FilesView.ID, IPageLayout.BOTTOM, 0.80f,
				editorArea);

		layout.getViewLayout(PagesView.ID).setCloseable(false);
		layout.getViewLayout(RecentView.ID).setCloseable(false);
		layout.getViewLayout(FilesView.ID).setCloseable(false);
	}
}
