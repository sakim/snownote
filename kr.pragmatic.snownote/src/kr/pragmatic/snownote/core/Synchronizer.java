package kr.pragmatic.snownote.core;

import java.lang.reflect.InvocationTargetException;

import kr.pragmatic.snownote.SnowNotePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import rath.toys.springnote.Page;

public class Synchronizer implements IRunnableWithProgress {
	public static final int MODE_FULL_SYNC = 0;
	public static final int MODE_UPLOAD = 1;
	public static final int MODE_DOWNLOAD = 2;

	private SnowNote fNote;

	private int fMode;

	private boolean fConfirm;

	public Synchronizer(int mode) {
		fNote = SnowNotePlugin.getSnowNote();
		this.fMode = mode;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				int openCount = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getEditorReferences().length;

				if (openCount == 0) {
					fConfirm = true;
					return;
				}
				
				fConfirm = MessageDialog.openConfirm(Display.getDefault()
						.getActiveShell(), "확인",
						"모든 내용을 저장하고 열려진 에디터를 닫습니다. 계속 진행하시겠습니까?");

				if (fConfirm) {
					PlatformUI.getWorkbench().saveAllEditors(false);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().closeAllEditors(false);
				}
			}
		});

		if (fConfirm == false)
			return;

		try {
			if (fMode == MODE_FULL_SYNC) {
				fNote.synchronize(monitor);
			}

			if (fMode == MODE_UPLOAD) {
				fNote.syncUpload(monitor);
			}

			if (fMode == MODE_DOWNLOAD) {
				fNote.syncDownload(monitor);
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"에러", "동기화 과정에 에러가 발생했습니다. 다시 확인하고 재시도 해주세요.");
		}
	}

	public void createAttachments(Page page) {

	}
}