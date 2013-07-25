package kr.pragmatic.snownote.preferences;

import java.io.IOException;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowFlickr;
import kr.pragmatic.snownote.utils.BrowserUtil;
import kr.pragmatic.snownote.utils.NoteUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FlickrPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private IPreferenceStore store;

	private Text keyText;
	private Text secretText;
	private Label statusLabel;

	public FlickrPreferencePage() {
		store = SnowNotePlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setMessage("Flickr: 사진 올리기 기능을 이용하려면 사용자의 Flickr API 키를 발급받아야 합니다.");
		setDescription("API 키를 발급받고 Flickr 사이트에서 Snownote의 사용을 허가해야 사진 올리기 기능을 이용할 수 있습니다.");
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = SWT.BEGINNING;
		composite.setLayoutData(data);

		Button apiButton = new Button(composite, SWT.NONE);
		apiButton.setText("API 키 발급받으로 가기");
		apiButton.addSelectionListener(new BrowserLauncher());
		data = new GridData();
		data.horizontalSpan = 2;
		apiButton.setLayoutData(data);

		// http://www.flickr.com/services/api/keys/apply/

		Label keyLabel = new Label(composite, SWT.NONE);
		keyLabel.setText("API  Key:");

		keyText = new Text(composite, SWT.BORDER);
		data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 250;
		keyText.setLayoutData(data);

		Label secretLabel = new Label(composite, SWT.NONE);
		secretLabel.setText("Shared Sectet:");

		secretText = new Text(composite, SWT.BORDER);
		data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 250;
		secretText.setLayoutData(data);

		Button authButton = new Button(composite, SWT.NONE);
		authButton.setText("Flick계정 사용 허용하기");
		authButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				authorize();
			}
		});

		statusLabel = new Label(composite, SWT.NONE);
		statusLabel.setText("사용허가 필요함");
		statusLabel.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_RED));

		load();

		return parent;
	}

	private boolean checkAuthority() {
		SnowFlickr flickr;
		try {
			flickr = new SnowFlickr(getApiKey(), getSharedSecret(), NoteUtil
					.getUserDirPath());
			int result = flickr.checkAuthority();

			if (result == SnowFlickr.STATUS_AUTHORIZED)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private void authorize() {
		boolean value = false;

		try {
			SnowFlickr flickr = new SnowFlickr(getApiKey(), getSharedSecret(),
					NoteUtil.getUserDirPath());
			int result = flickr.checkAuthority();

			if (result == SnowFlickr.STATUS_AUTHORIZED) {
				value = true;
			} else if (result == SnowFlickr.STATUS_REQUIRE_AUTHORIZE) {
				String url = flickr.authorize();
				BrowserUtil.openLink(url);

				MessageDialog.openInformation(Display.getDefault()
						.getActiveShell(), "확인",
						"브라우저에서 Snownote의 사용을 허용한 후 확인 버튼을 누르세요.");

				int confirm = flickr.confirmAuthorized();

				if (confirm == SnowFlickr.STATUS_AUTHORIZED)
					value = true;
			} else {
				value = false;
			}

			setAuthorized(value);

		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"에러", "Flickr 계정 사용허용 중 에러가 발생함.");
		}
	}

	private String getApiKey() {
		return keyText.getText().trim();
	}

	private String getSharedSecret() {
		return secretText.getText().trim();
	}

	private void setAuthorized(boolean value) {
		if (value) {
			statusLabel.setText("사용허용됨");
			statusLabel.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		} else {
			statusLabel.setText("사용허용 필요함");
			statusLabel.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_RED));
		}
	}

	@Override
	public boolean performOk() {
		store.setValue(PreferenceKeys.FLICKR_API_KEY, getApiKey());
		store.setValue(PreferenceKeys.FLICKR_SHARED_SECRET, getSharedSecret());

		return true;
	}

	@Override
	protected void performDefaults() {
		load();
	}

	private void load() {
		keyText.setText(store.getString(PreferenceKeys.FLICKR_API_KEY));
		secretText
				.setText(store.getString(PreferenceKeys.FLICKR_SHARED_SECRET));
		setAuthorized(checkAuthority());
	}

	class BrowserLauncher extends SelectionAdapter {
		private static final String URL_FLICKR_API = "http://www.flickr.com/services/api/keys/apply/";

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				BrowserUtil.openLink(URL_FLICKR_API);
			} catch (Exception e1) {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"에러", "웹 브라우저를 열 수 없습니다. " + URL_FLICKR_API
								+ "에 직접 방문해서 사용자키를 발급받으세요.");
			}
		}
	}
}
