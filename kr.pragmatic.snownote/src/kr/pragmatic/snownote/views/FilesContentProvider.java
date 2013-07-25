package kr.pragmatic.snownote.views;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import kr.pragmatic.snownote.core.SnowAttachment;
import kr.pragmatic.snownote.utils.FileUtil;
import kr.pragmatic.snownote.viewer.ITableContentProvider;

public class FilesContentProvider implements ITableContentProvider {

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		List<SnowAttachment> attachs = (List<SnowAttachment>) inputElement;
		return attachs.toArray(new SnowAttachment[attachs.size()]);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object getColumnValue(Object element, int columnIndex) {
		SnowAttachment attach = (SnowAttachment) element;

		if (columnIndex == 0) { // type
			return FileUtil.getType(FileUtil.getExtension(attach.getName()));
		}

		if (columnIndex == 1) { // name
			return attach.getName();
		}

		if (columnIndex == 2) { // size
			return attach.getSize();
		}

		if (columnIndex == 3) { // path
			return attach.getPath();
		}

		return null;
	}

}
