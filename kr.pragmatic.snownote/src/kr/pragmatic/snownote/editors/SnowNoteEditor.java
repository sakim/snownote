package kr.pragmatic.snownote.editors;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.epf.richtext.RichTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class SnowNoteEditor extends EditorPart {
	private boolean dirty = false;
	private DecoratedRichTextEditor editor;

	public static final String ID = "kr.pragmatic.snownote.editors.SnowNoteEditor";

	public SnowNoteEditor() {
	}

	@Override
	public void createPartControl(Composite parent) {
		editor = new DecoratedRichTextEditor(parent, SWT.FILL, getEditorSite());
		editor.setEditable(true);
		SnowNoteEditorInput input = (SnowNoteEditorInput) getEditorInput();
		editor.setText(input.getContents());

		editor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		setPartName(input.getName());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
		SnowNoteEditorInput input = (SnowNoteEditorInput) getEditorInput();
		SnowPage page = input.getSourcePage();
		page.setModified(true);
		page.setContents(editor.getText());
		
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(new java.util.Date().getTime());
		
		page.setModifiedAt(cal.getTime());
		

		SnowNote note = SnowNotePlugin.getSnowNote();
		note.firePropertyChange();
	}

	@Override
	public void doSaveAs() {
		doSave(null);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean value) {
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		getRichTextEditor().getRichTextControl().setFocus();
	}
	
	public RichTextEditor getRichTextEditor() {
		return editor;
	}
}
