package kr.pragmatic.snownote.preferences;

import kr.pragmatic.snownote.SnowNotePlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PagesPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private IPreferenceStore store;

	public PagesPreferencePage() {
		store = SnowNotePlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor deleteInformEditor = new BooleanFieldEditor(
				PreferenceKeys.DONOT_INFORM_PAGE_DELETION,
				"페이지 삭제 시 동기화 후 삭제됨을 알리지 않기", getFieldEditorParent());
		addField(deleteInformEditor);
	}

	public void init(IWorkbench workbench) {
	}

}
