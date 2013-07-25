package kr.pragmatic.snownote.editors;

import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class SnowNoteEditorInput implements IEditorInput {
	private SnowPage fPage;

	public SnowNoteEditorInput(SnowPage page) {
		fPage = page;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return fPage.getTitle();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return fPage.getTitle();
	}

	public SnowPage getSourcePage() {
		return fPage;
	}

	public String getContents() {
		return fPage.getContents();
	}

	public int getId() {
		return fPage.getIdentifier();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SnowNoteEditorInput) {
			if (getId() == ((SnowNoteEditorInput) obj).getId()) {
				return true;
			}
		}
		return false;
	}

}
