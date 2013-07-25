package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.editors.SnowNoteEditorInput;
import kr.pragmatic.snownote.viewer.TableViewerSorter;
import kr.pragmatic.snownote.viewer.TableViewerSorterHandler;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class FilesView extends ViewPart {
	public static final String ID = "kr.pragmatic.snownote.view.attachments";

	private static final String[] COLUMN_HEADINGS = { "", "이름", "크기", "경로" };
	private static final ColumnLayoutData[] COLUMN_LAYOUT_DATA = {
			new ColumnWeightData(3, false), new ColumnWeightData(25, true),
			new ColumnWeightData(10, true), new ColumnWeightData(62, true) };
	private static final int[] COLUMN_ALIGNMENT = { SWT.LEFT, SWT.LEFT,
			SWT.RIGHT, SWT.LEFT };

	private TableViewer fViewer;

	private EditorListener fListener = new EditorListener();

	@Override
	public void createPartControl(Composite parent) {
		Table table = createTableWithColumns(parent);
		fViewer = new TableViewer(table);

		GridData gd = new GridData(GridData.FILL_BOTH);
		fViewer.getControl().setLayoutData(gd);

		FilesLabelProvider labelProvider = new FilesLabelProvider();
		FilesContentProvider contentProvider = new FilesContentProvider();
		TableViewerSorter sorter = new TableViewerSorter(fViewer,
				contentProvider);

		fViewer.setLabelProvider(labelProvider);
		fViewer.setContentProvider(contentProvider);
		fViewer.setSorter(sorter);
		fViewer.setInput(null);

		table.addSelectionListener(new TableViewerSorterHandler(table, sorter));

		getSite().setSelectionProvider(fViewer);

		getSite().getWorkbenchWindow().getPartService().addPartListener(
				fListener);

		hookContextMenu();
	}

	private Table createTableWithColumns(Composite parent) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.SINGLE | SWT.FULL_SELECTION);
		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for (int i = 0; i < COLUMN_HEADINGS.length; i++) {
			layout.addColumnData(COLUMN_LAYOUT_DATA[i]);
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(COLUMN_HEADINGS[i]);
			column.setAlignment(COLUMN_ALIGNMENT[i]);
			column.setResizable(true);
		}

		return table;
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getPartService().removePartListener(
				fListener);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}
	
	public void setInput(SnowPage page) {
		fViewer.setInput(page.getAttachments());
	}

	class EditorListener implements IPartListener {

		public void partBroughtToTop(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				SnowPage page = ((SnowNoteEditorInput) ((IEditorPart) part)
						.getEditorInput()).getSourcePage();
				fViewer.setInput(page.getAttachments());
			}
		}

		public void partClosed(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				fViewer.setInput(null); // clear
			}
		}

		public void partOpened(IWorkbenchPart part) {
			// do nothing: partBroughtToTop() is invoked after part opened.
		}

		public void partActivated(IWorkbenchPart part) {
			// do nothing
		}

		public void partDeactivated(IWorkbenchPart part) {
			// do nothing
		}

	}
}
