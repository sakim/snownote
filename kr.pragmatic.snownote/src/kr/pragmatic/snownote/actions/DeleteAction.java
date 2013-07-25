package kr.pragmatic.snownote.actions;

import java.util.List;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.dialogs.DeleteMessageDialog;
import kr.pragmatic.snownote.preferences.PreferenceKeys;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class DeleteAction implements IObjectActionDelegate {
	@SuppressWarnings("unchecked")
	private List fSelectionList;
	private IWorkbenchPart fTargetPart;

	public DeleteAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fTargetPart = targetPart;
	}

	public void run(IAction action) {
		IPreferenceStore store = SnowNotePlugin.getDefault()
				.getPreferenceStore();
		boolean informed = store
				.getBoolean(PreferenceKeys.DONOT_INFORM_PAGE_DELETION);

		// 다중 선택 처리하도록 구현했지만 현재 View에서 다중 선택 지원하지 않고 있음.
		// (삭제 메뉴 활성화 옵션 등 고려해서 지원 예정)
		for (Object obj : fSelectionList) {
			if (obj instanceof SnowPage) {
				SnowPage page = (SnowPage) obj;
				if (page.isRoot()) {
					String title = "알림";
					String message = "최상위 페이지는 삭제할 수 없습니다.";
					MessageDialog.openInformation(fTargetPart.getSite()
							.getShell(), title, message);
					continue;
				}

				page.setDeleted(true);
				if (!informed) {
					informed = true;
					String title = "알림";
					String message = "페이지는 다음 동기화 때 삭제됩니다. 동기화 이전에는 삭제를 취소할 수 있습니다.\n "
							+ "삭제될 페이지의 하위 페이지는 모두 상위 페이지로 옮겨집니다.";
					DeleteMessageDialog.openInformation(fTargetPart.getSite()
							.getShell(), title, message);
				}

				SnowNotePlugin.getSnowNote().firePropertyChange();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssl = (IStructuredSelection) selection;
			fSelectionList = ssl.toList();
		}
	}

}
