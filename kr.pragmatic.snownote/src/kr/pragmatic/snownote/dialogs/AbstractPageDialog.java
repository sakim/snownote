package kr.pragmatic.snownote.dialogs;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowPage;
import kr.pragmatic.snownote.views.PagesContentProvider;
import kr.pragmatic.snownote.views.PagesLabelProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * 새로운 페이지 생성 다이얼로그
 * 
 * @author sakim
 * 
 */
public class AbstractPageDialog extends Dialog {
	private static final int EXPAND_HEIGHT = 150;
	
	private String fTitle;
	private SnowPage fCurrentLocation;

	private Text fPageName;
	private Text fTags;
	private Link fLocation;
	private Label fLocationLabel;

	private ExpandableComposite fSection;

	private TreeViewer fViewer;
	private Tree fTree;

	public AbstractPageDialog(Shell shell, SnowPage location) {
		super(shell);
		String title = "새 페이지 만들기";
		setDialogTitle(title);

		this.fCurrentLocation = location;
	}

	@Override
	public void create() {
		// titlebar없는 modal 윈도우로 생성
		setShellStyle(SWT.NO_TRIM | SWT.APPLICATION_MODAL);
		super.create();
	}
	
	// Composite의 Style이 Border인 것을 제외하고는 동일한 메서드
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);
				
		return composite;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FillLayout());

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		final ScrolledForm form = toolkit.createScrolledForm(composite);
		GridLayout layout = new GridLayout(2, false);
		form.getBody().setLayout(layout);

		form.setText(getDialogTitle());
		toolkit.decorateFormHeading(form.getForm());

		// page name
		fPageName = createDecoratedTextField("이름", toolkit, form.getBody());

		// tags
		fTags = createDecoratedTextField("태그", toolkit, form.getBody());

		// page creation location
		createLocationLink("위치", toolkit, form.getBody());

		// select location expandable composite
		Composite expandComp = createExpandableComposite(toolkit, form);

		// select location tree
		createPagesTree(toolkit, expandComp);

		// initialize
		init();

		return composite;
	}

	private Composite createExpandableComposite(FormToolkit toolkit,
			final ScrolledForm form) {
		fSection = toolkit
				.createExpandableComposite(form.getBody(),
						ExpandableComposite.TWISTIE
								| ExpandableComposite.CLIENT_INDENT);
		fSection.setText("다른 위치 선택하기");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.heightHint = 10;
		fSection.setLayoutData(data);

		Composite sbody = toolkit.createComposite(fSection);
		sbody.setLayout(new GridLayout());
		fSection.setClient(sbody);

		fSection.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				Point point = getShell().getSize();

				if (!fSection.isExpanded()) {
					getShell().setSize(point.x, point.y - EXPAND_HEIGHT);
				} else {
					getShell().setSize(point.x, point.y + EXPAND_HEIGHT);
				}

				form.reflow(true);
			}
		});

		return sbody;
	}

	private Text createDecoratedTextField(String label, FormToolkit toolkit,
			Composite parent) {
		createLabel(label, toolkit, parent);

		return createText(toolkit, parent);
	}

	private void createLocationLink(String label, FormToolkit toolkit,
			Composite parent) {
		fLocationLabel = createLabel(label, toolkit, parent);
		fLocation = new Link(parent, SWT.WRAP);
		toolkit.adapt(fLocation, true, true);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 330;
		fLocation.setLayoutData(data);

		fLocation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedLocation = e.text;
				SnowPage selectedPage = findPage(fCurrentLocation,
						selectedLocation);
				if (selectedPage != null) {
					fCurrentLocation = selectedPage;
					setLocation(fCurrentLocation);
					fViewer.setSelection(new StructuredSelection(
							fCurrentLocation));
				}
			}
		});
	}

	private Label createLabel(String text, FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, text);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.widthHint = 30;
		label.setLayoutData(data);

		return label;
	}

	private Text createText(FormToolkit toolkit, Composite parent) {
		Text text = toolkit.createText(parent, "", SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 330;
		text.setLayoutData(data);

		return text;
	}

	private void createPagesTree(FormToolkit toolkit, Composite parent) {
		fTree = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

		GridData data = new GridData();
		data.heightHint = 135;
		data.widthHint = 330;
		fTree.setLayoutData(data);

		fViewer = new TreeViewer(fTree);
		fViewer.setAutoExpandLevel(1);
		PagesContentProvider fContentProvider = new PagesContentProvider();
		PagesLabelProvider fLabelProvider = new PagesLabelProvider(
				PagesLabelProvider.ALL_PAGES_MODE);

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(fLabelProvider);
		toolkit.adapt(fTree);

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				fCurrentLocation = (SnowPage) selection.getFirstElement();
				setLocation(fCurrentLocation);
			}
		});
	}

	/**
	 * 초기 데이터를 화면에 설정
	 */
	protected void init() {
		SnowNote note = SnowNotePlugin.getSnowNote();
		fViewer.setInput(note);

		if (fCurrentLocation != null) { // null == root
			fViewer.setSelection(new StructuredSelection(fCurrentLocation));
			setLocation(fCurrentLocation);
		}
	}

	private void setLocation(SnowPage page) {
		fLocation.setText("");
		if (page != null)
			createLink(page);
	}

	private void createLink(SnowPage page) {
		if (page.getParent() != null)
			createLink(page.getParent());
		fLocation.setText(fLocation.getText() + "<a>" + page.getTitle()
				+ "</a>" + " > ");
	}

	/**
	 * 페이지 생성/수정 시 위치선택을 가능하게 할지 여부를 결정한다. Root 페이지의 수정 시 위치선택이 불가능하다.
	 * 
	 * @param value
	 */
	protected void enableSelectLocation(boolean value) {
		fLocationLabel.setVisible(value);
		fLocation.setVisible(value);
		fSection.setVisible(value);
	}

	private SnowPage findPage(SnowPage page, String pageName) {
		if (page == null)
			return null;

		if (page.getTitle().equals(pageName))
			return page;

		return findPage(page.getParent(), pageName);
	}

	/**
	 * FormsUI를 사용하는 Contents부분과의 일관성을 위해서 버튼영역의 색상을 흰색으로 변경
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);

		parent.setBackground(getShell().getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		control.setBackground(getShell().getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		return control;
	}

	protected String getDialogTitle() {
		return fTitle;
	}

	protected void setDialogTitle(String title) {
		this.fTitle = title;
	}

	protected void setPageName(String name) {
		fPageName.setText(name);
	}

	protected String getPageName() {
		return fPageName.getText().trim();
	}

	protected void setTags(String tags) {
		fTags.setText(tags);
	}

	protected String getTags() {
		return fTags.getText().trim();
	}

	protected void setCurrentLocation(SnowPage location) {
		fCurrentLocation = location;
	}

	protected SnowPage getCurrnetLocation() {
		return fCurrentLocation;
	}
}
