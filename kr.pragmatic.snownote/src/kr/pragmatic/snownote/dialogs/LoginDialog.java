package kr.pragmatic.snownote.dialogs;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.preferences.PreferenceKeys;
import kr.pragmatic.snownote.utils.BrowserUtil;
import kr.pragmatic.snownote.utils.NoteUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LoginDialog extends TitleAreaDialog {
	private static final String SHELL_TITLE = "SnowNote 사용자 인증";
	private static final String TITLE = "사용자 정보";
	private static final String MESSAGE = "Snownote는 사용자의 스프링노트 계정 정보와 사용자 키를 필요로 합니다. ";

	private static String USER_KEY_URL = "https://api.openmaru.com/delegate_key/springnote?app_id=2def9265&openid=";

	private IPreferenceStore store;

	private Button autoLogin;
	private Text openId;
	private Text domain;
	private Text userKey;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
		store = SnowNotePlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(SHELL_TITLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(TITLE);
		setMessage(MESSAGE);
		Composite areaComposite = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(areaComposite, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);

		GridData labelData = new GridData();
		labelData.widthHint = 110;

		GridData fieldData = new GridData();
		fieldData.widthHint = 260;

		// user id
		createOpenIdSection(composite, labelData, fieldData);

		// user domain
		createUserDomainSection(composite, labelData);

		// user key
		createUserKeySection(composite, labelData, fieldData);

		// login automatically
		createAutoLoginSection(composite);

		loadPreference();

		return composite;
	}

	private void createAutoLoginSection(Composite composite) {
		GridData loginData = new GridData();
		loginData.horizontalSpan = 2;
		loginData.horizontalAlignment = GridData.CENTER;
		autoLogin = new Button(composite, SWT.CHECK);
		autoLogin.setText("정보 저장하고 다시 묻지않기");
		autoLogin.setLayoutData(loginData);
	}

	private void createUserKeySection(Composite composite, GridData labelData,
			GridData fieldData) {
		Label userKeyLabel = new Label(composite, SWT.RIGHT);
		userKeyLabel.setText("사용자 키:");
		userKeyLabel.setLayoutData(labelData);

		Composite keyComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		keyComposite.setLayout(layout);

		userKey = new Text(keyComposite, SWT.BORDER);
		userKey.setLayoutData(fieldData);

		Button issueButton = new Button(keyComposite, SWT.BORDER);
		issueButton.setText("사용자 키 발급 받기");
		issueButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String url = USER_KEY_URL + getOpenId();
				openBrowser(url);
			}
		});
	}

	private void createOpenIdSection(Composite composite, GridData labelData,
			GridData fieldData) {
		Label openIdLabel = new Label(composite, SWT.RIGHT);
		openIdLabel.setText("오픈아이디:");
		openIdLabel.setLayoutData(labelData);

		openId = new Text(composite, SWT.BORDER);
		openId.setLayoutData(fieldData);
		openId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO extract to Utility
				String id = openId.getText().trim().split("\\.")[0];

				if (id.indexOf("http://") == 0) {
					id = id.substring("http://".length());
				}

				domain.setText(id);
			}
		});
	}

	private void createUserDomainSection(Composite composite, GridData labelData) {
		Label domainLabel = new Label(composite, SWT.RIGHT);
		domainLabel.setText("스프링노트 도메인:");
		domainLabel.setLayoutData(labelData);

		GridLayout domainLayout = new GridLayout(3, false);
		domainLayout.horizontalSpacing = 0;
		domainLayout.marginWidth = 0;

		Composite domainComposite = new Composite(composite, SWT.NONE);
		domainComposite.setLayout(domainLayout);
		GridData domainData = new GridData();
		domainData.widthHint = 130;

		Label domain0 = new Label(domainComposite, SWT.None);
		domain0.setText("http://");

		domain = new Text(domainComposite, SWT.BORDER);
		domain.setLayoutData(domainData);

		Label domain2 = new Label(domainComposite, SWT.NONE);
		domain2.setText(".springnote.com");
	}

	private void loadPreference() {
		openId.setText(store.getString(PreferenceKeys.OPEN_ID));
		domain.setText(store.getString(PreferenceKeys.USER_DOMAIN));
		userKey.setText(store.getString(PreferenceKeys.USER_KEY));
	}

	private void openBrowser(String url) {
		try {
			BrowserUtil.openLink(url);
		} catch (Exception e) {
			MessageDialog.openError(getParentShell(), "에러",
					"웹 브라우저를 열 수 없습니다. 직접 " + url + "로 \n접속해서 사용자 키를 발급받으세요.");
		}
	}

	public String getOpenId() {
		return NoteUtil.conciliateOpenId(openId.getText().trim());
	}

	public String getDomain() {
		return domain.getText().trim();
	}

	public String getUserKey() {
		return userKey.getText().trim();
	}

	@Override
	protected void okPressed() {
		store.setValue(PreferenceKeys.AUTO_LOGIN, autoLogin.getSelection());
		store.setValue(PreferenceKeys.OPEN_ID, getOpenId());
		store.setValue(PreferenceKeys.USER_DOMAIN, getDomain());
		store.setValue(PreferenceKeys.USER_KEY, getUserKey());

		super.okPressed();
	}
}
