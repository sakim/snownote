package kr.pragmatic.snownote.utils;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.editors.SnowNoteEditorInput;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class EditorUtil {
	/**
	 * 너무 긴 Page 이름을 줄여준다.
	 * 
	 * @param fullName
	 *            full name of page
	 * @return short name
	 */
	public static String getPartName(String fullName) {
		if (fullName.length() < 20) {
			return fullName;
		}

		return fullName.substring(0, 19).concat("...");
	}

	/**
	 * Input에 대해서 이미 에디터가 열려져있는지 판단하고 에디터를 반환한다.
	 * 
	 * @param inputElement
	 *            Input
	 * @return 열려진 에디터가 있다면 에디터 반환 없으면 null
	 */
	public static IEditorPart isOpenInEditor(Object inputElement) {
		IEditorInput input = null;

		input = getEditorInput(inputElement);

		if (input != null) {
			IWorkbenchPage p = SnowNotePlugin.getActivePage();
			if (p != null) {
				return p.findEditor(input);
			}
		}

		return null;
	}

	public static IEditorInput getEditorInput(Object input) {
		if (input instanceof SnowPage) {
			return new SnowNoteEditorInput((SnowPage) input);
		}

		return null;
	}
}
