package kr.pragmatic.snownote.dialogs;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 새로운 페이지 생성 다이얼로그
 * 
 * @author sakim
 * 
 */
public class CreatePageDialog extends AbstractPageDialog {
	public CreatePageDialog(Shell shell, SnowPage page) {
		super(shell, page);
		String title = "새 페이지 만들기";
		setDialogTitle(title);

		SnowNote note = SnowNotePlugin.getSnowNote();
		if (getCurrnetLocation() == null)
			setCurrentLocation(note.getRootPages().get(0));
	}

	@Override
	protected void okPressed() {
		SnowNote note = SnowNotePlugin.getSnowNote();

		boolean created = note.createNewSnowPage(getCurrnetLocation(),
				getPageName(), getTags());

		if (created) {
			super.okPressed();
		} else {
			MessageDialog.openError(getShell(), "실패",
					"이 위치에 같은 제목을 가진 페이지가 이미 있습니다." + " 다른 제목으로 만들어주세요.");
		}
	}
}
