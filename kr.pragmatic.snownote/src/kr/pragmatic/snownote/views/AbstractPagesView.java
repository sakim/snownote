package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.actions.OpenEditorAction;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.utils.EditorUtil;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractPagesView extends ViewPart implements
		ISelectionChangedListener {
	protected TreeViewer fViewer;
	protected Tree fTree;

	protected AbstractPagesContentProvider fContentProvider;
	protected PagesLabelProvider fLabelProvider;

	protected OpenEditorAction fOpen;
	protected ISelection fLastOpenSelection;

	protected abstract void createProviders();

	protected abstract void initDragAndDrop();

	protected abstract void setSorter();

	public void createPartControl(Composite parent) {
		fTree = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		fViewer = new TreeViewer(fTree);

		createProviders();

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(fLabelProvider);
		fViewer.setAutoExpandLevel(2);
		fViewer.addPostSelectionChangedListener(this);

		setSorter();

		SnowNote note = SnowNotePlugin.getSnowNote();
		fViewer.setInput(note);

		fOpen = new OpenEditorAction(getSite());
		fViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fOpen.run();
				fLastOpenSelection = event.getSelection();
			}
		});

		getSite().setSelectionProvider(fViewer);

		hookContextMenu();
		initDragAndDrop();
	}

	protected boolean isActivePart() {
		return this == getSite().getPage().getActivePart();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (!selection.equals(fLastOpenSelection)) {
			linkToEditor((IStructuredSelection) selection);
		}
		fLastOpenSelection = null;
	}

	@Override
	public void dispose() {
		fViewer.removePostSelectionChangedListener(this);
		super.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	protected void hookContextMenu() {
	}

	/**
	 * Links to editor (if option enabled)
	 * 
	 * @param selection
	 *            the selection
	 */
	protected void linkToEditor(IStructuredSelection selection) {
		if (!isActivePart())
			return;
		Object obj = selection.getFirstElement();
		if (selection.size() == 1) {
			IEditorPart part = EditorUtil.isOpenInEditor(obj);
			if (part != null) {
				IWorkbenchPage page = getSite().getPage();
				page.bringToTop(part);
			}
		}
	}
}
