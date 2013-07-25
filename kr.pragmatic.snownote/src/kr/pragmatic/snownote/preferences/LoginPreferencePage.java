package kr.pragmatic.snownote.preferences;

import kr.pragmatic.snownote.SnowNotePlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LoginPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private IPreferenceStore store;

	public LoginPreferencePage() {
		store = SnowNotePlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setMessage("스프링노트: Snownote를 이용하려면 스프링노트 계정정보를 입력해야 합니다.");
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor autoLoginEditor = new BooleanFieldEditor(
				PreferenceKeys.AUTO_LOGIN, "정보 저장하고 프로그램 시작 시 물어보지 않기",
				getFieldEditorParent());
		StringFieldEditor openidEditor = new StringFieldEditor(
				PreferenceKeys.OPEN_ID, "오픈아이디:", getFieldEditorParent());
		StringFieldEditor domainEditor = new StringFieldEditor(
				PreferenceKeys.USER_DOMAIN, "스프링노트 도메인:",
				getFieldEditorParent());
		StringFieldEditor userKeyEditor = new StringFieldEditor(
				PreferenceKeys.USER_KEY, "사용자 키:", getFieldEditorParent());
		
		addField(autoLoginEditor);
		addField(openidEditor);
		addField(domainEditor);
		addField(userKeyEditor);
	}

	public void init(IWorkbench workbench) {
	}
}
