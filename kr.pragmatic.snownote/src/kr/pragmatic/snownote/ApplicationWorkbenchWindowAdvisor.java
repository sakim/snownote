package kr.pragmatic.snownote;

import kr.pragmatic.snownote.dialogs.LoginDialog;
import kr.pragmatic.snownote.utils.HibernateUtil;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IPreferenceStore store = SnowNotePlugin.getDefault()
				.getPreferenceStore();
		if (store.getBoolean("auto_login"))
			return;

		// Platform.endSplash();
		final Display display = PlatformUI.getWorkbench().getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				LoginDialog dialog = new LoginDialog(display.getActiveShell());
				int result = dialog.open();
				if (result == Window.CANCEL) {
					display.close();
					System.exit(0);
				}
			}
		});

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("Snow Note");
	}

	@Override
	public boolean preWindowShellClose() {
		HibernateUtil.shutdown();

		return super.preWindowShellClose();
	}
}
