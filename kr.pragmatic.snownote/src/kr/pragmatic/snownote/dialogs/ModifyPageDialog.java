package kr.pragmatic.snownote.dialogs;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ModifyPageDialog extends AbstractPageDialog {
	private SnowPage fTargetPage;

	public ModifyPageDialog(Shell shell, SnowPage page) {
		super(shell, page.getParent());
		fTargetPage = page;
		setDialogTitle("페이지 수정하기");
	}

	@Override
	protected void init() {
		super.init();

		setPageName(fTargetPage.getTitle());
		setTags(fTargetPage.getTags());

		if (getCurrnetLocation() == null)
			enableSelectLocation(false);
	}

	@Override
	protected void okPressed() {
		SnowNote note = SnowNotePlugin.getSnowNote();

		boolean modified = note.modifySnowPage(getCurrnetLocation(),
				fTargetPage, getPageName(), getTags());

		if (modified) {
			super.okPressed();
		} else {
			MessageDialog.openError(getShell(), "실패", "동일한 제목의 페이지가 이미 존재합니다."
					+ " 다른 제목/위치로 변경해 주세요.");
		}
	}
}
