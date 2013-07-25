package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.views.dnd.PageDragAdapter;
import kr.pragmatic.snownote.views.dnd.PageDropAdapter;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Menu;

public class PagesView extends AbstractPagesView {
	public static final String ID = "kr.pragmatic.snownote.view.pages";

	@Override
	protected void createProviders() {
		fContentProvider = new PagesContentProvider();
		fLabelProvider = new PagesLabelProvider(
				PagesLabelProvider.ALL_PAGES_MODE);
	}

	@Override
	protected void initDragAndDrop() {
		fViewer.addDragSupport(DND.DROP_MOVE,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new PageDragAdapter(fViewer));

		fViewer.addDropSupport(DND.DROP_MOVE,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new PageDropAdapter(fViewer));
	}

	@Override
	protected void setSorter() {
		fViewer.setSorter(new ViewerSorter());
	}
	
	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}
}
