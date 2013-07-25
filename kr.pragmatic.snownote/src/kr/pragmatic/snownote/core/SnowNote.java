package kr.pragmatic.snownote.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.preferences.PreferenceKeys;
import kr.pragmatic.snownote.utils.HibernateUtil;
import kr.pragmatic.snownote.utils.NoteUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import rath.toys.springnote.Attachment;
import rath.toys.springnote.NoteException;
import rath.toys.springnote.Page;
import rath.toys.springnote.PageMeta;
import rath.toys.springnote.SpringNote;

public class SnowNote {

	// springnote open api application key
	public static final String APPLICATION_KEY = "5dbaab808a18a1c687870698025dce083a9043de";
	private SpringNote springnote;

	private Map<Integer, List<SnowPage>> pageHierarchyMap = new HashMap<Integer, List<SnowPage>>();
	private List<SnowPage> pageList = new ArrayList<SnowPage>();

	private ListenerList propertyChangeListeners = new ListenerList();

	private Session session;

	private List<SnowPage> roots;

	public SnowNote() throws Exception {
		springnote = createSpringNote();
		load();
	}

	/**
	 * Springnote API를 다루기 위한 객체 생성
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws MalformedURLException
	 */
	private SpringNote createSpringNote() throws ParserConfigurationException,
			MalformedURLException {
		SpringNote note = new SpringNote();

		IPreferenceStore store = SnowNotePlugin.getDefault()
				.getPreferenceStore();

		note.setOpenID(new URL(store.getString(PreferenceKeys.OPEN_ID)));
		note.setUsername(store.getString(PreferenceKeys.USER_DOMAIN));
		note.setApplicationKey(APPLICATION_KEY);
		note.setUserKey(store.getString(PreferenceKeys.USER_KEY));

		return note;
	}

	public List<SnowPage> getRootPages() {
		return roots;
	}

	public void setRootPages(List<SnowPage> roots) {
		this.roots = roots;
	}

	private SpringNote getSpringNote() {
		return springnote;
	}

	public List<SnowPage> getPages() {
		return pageList;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		getPropertyChangeListeners().add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		getPropertyChangeListeners().remove(listener);
	}

	/**
	 * @return Returns the propertyChangeListeners.
	 */
	private ListenerList getPropertyChangeListeners() {
		if (propertyChangeListeners == null)
			propertyChangeListeners = new ListenerList();
		return propertyChangeListeners;
	}

	private static final String EMPTY_PROPERTY = "";

	public void firePropertyChange() {
		Object[] listeners = getPropertyChangeListeners().getListeners();
		PropertyChangeEvent event = new PropertyChangeEvent(this,
				EMPTY_PROPERTY, null, null);
		for (int i = 0; i < listeners.length; i++) {
			((IPropertyChangeListener) listeners[i]).propertyChange(event);
		}

		Session session = getSession();
		Transaction tx = session.beginTransaction();
		tx.commit();
	}

	public List<PageMeta> getPageMetaList() throws IOException, SAXException,
			ParseException {
		return getSpringNote().getPages();
	}

	/**
	 * 새로운 페이지를 생성한다.
	 * 
	 * @param parent
	 *            페이지 생성 위치
	 * @param title
	 *            페이지 제목
	 * @param tags
	 *            페이지 태그
	 * @return 생성 성공/실패
	 */
	public boolean createNewSnowPage(SnowPage parent, String title, String tags) {

		title = NoteUtil.conciliateEmptyTitle(title);

		if (NoteUtil.hasSameTitledChild(parent, title))
			return false;

		SnowPage page = new SnowPage();
		page.setTitle(title);
		page.setTags(tags);

		parent.addChild(page);
		page.setCreated(true);

		firePropertyChange();

		return true;
	}

	/**
	 * 기존 페이지의 이름/태그/위치를 변경한다.
	 * 
	 * @param parent
	 *            생성위치
	 * @param page
	 *            변경 대상 페이지
	 * @param title
	 *            변경할 제목
	 * @param tags
	 *            변경할 태그
	 * @return 변경 성공/실패
	 */
	public boolean modifySnowPage(SnowPage parent, SnowPage page, String title,
			String tags) {
		if (parent == page || NoteUtil.isChild(page, parent))
			return false;

		title = NoteUtil.conciliateEmptyTitle(title);

		if (NoteUtil.hasSameTitledChild(parent, title))
			return false;

		// 검증이 끝난 후에 실제 페이지에 반영
		if (parent != null)
			parent.addChild(page);
		page.setTitle(title);
		page.setTags(tags);
		page.setModified(true);
		page.setModifiedAt(new Date());

		firePropertyChange();

		return true;
	}

	/**
	 * Springnote 계정으로부터 읽어온 페이지간의 연관 관계를 정의한다. identifier로 구별된 연관관계를 객체간의 연관관계로
	 * 변경한다. [Note: 재귀함수]
	 * 
	 * @param parent
	 */
	public void makeChildren(SnowPage parent) {
		List<SnowPage> children = pageHierarchyMap.get(parent.getIdentifier());
		if (children == null)
			return;

		parent.setChildren(children);

		for (SnowPage child : children) {
			child.setParent(parent);
			makeChildren(child);
		}
	}

	/**
	 * Springnote 계정으로부터 가져온 페이지 목록을 저장한다. 객체간의 관계 설정 후 Root 페이지를 저장한다 (모든 페이지
	 * 저장).
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		SnowPage root = pageHierarchyMap.get(Page.ROOT).get(0);
		makeChildren(root);
		pageHierarchyMap.clear();
		session.save(root);
		tx.commit();
	}

	/**
	 * 저장된 페이지를 불러온다. Root 페이지를 불러오면 연관관계에 따라서 모든 페이지를 불러온다.
	 */
	@SuppressWarnings("unchecked")
	public void load() {
		Session session = getSession();
		List<SnowPage> rootList = session.createQuery(
				"from SnowPage where PARENT_PAGE_ID = null").list();
		setRootPages(rootList);
	}

	// ////////////////////////////////
	// synchronize: uplaod, download //
	// ////////////////////////////////
	/**
	 * SnowNote와 SpringNote 서버의 내용을 동기화 한다.
	 * 
	 * @param monitor
	 *            진행 상태를 관리하는 상태 모니터
	 * @throws Exception
	 */
	public void synchronize(IProgressMonitor monitor) throws Exception {
		int uploadCount = countUpload();

		int total = uploadCount + getPageMetaList().size();
		monitor.beginTask("동기화 진행중 ...", total);

		upload(monitor);
		download(monitor);

		save();
		load();
		monitor.done();

		firePropertyChange();
	}

	/**
	 * 업로드 대상 작업의 개수를 구한다. 사용자에게 정확한 진행상태를 보여주기 위함.
	 * 
	 * @return 업로드 대상(생성 + 삭제 + 수정)의 개수
	 */
	private int countUpload() {
		int createdCount = ((Long) getSession()
				.createQuery(
						"select count(a) from SnowPage a where CREATED = true and DELETED = false")
				.list().get(0)).intValue();
		int deletedCount = ((Long) getSession().createQuery(
				"select count(a) from SnowPage a where DELETED = true").list()
				.get(0)).intValue();
		int updatedCount = ((Long) getSession().createQuery(
				"select count(a) from SnowPage a where MODIFIED = true").list()
				.get(0)).intValue();
		int newPicCount = ((Long) getSession().createQuery(
				"select count(a) from SnowAttachment a where CREATED = true")
				.list().get(0)).intValue();

		int uploadCount = createdCount + deletedCount + updatedCount
				+ newPicCount;
		return uploadCount;
	}

	public void syncUpload(IProgressMonitor monitor) throws Exception {
		int total = countUpload();

		monitor.beginTask("업로드 진행중 ...", total);
		upload(monitor);
		monitor.done();

		firePropertyChange();
	}

	public void syncDownload(IProgressMonitor monitor) throws Exception {
		int total = getPageMetaList().size();

		monitor.beginTask("다운로드 진행중 ...", total);

		download(monitor);

		save();
		load();
		monitor.done();

		firePropertyChange();
	}

	/**
	 * Snownote의 변경사항을 Springnote 계정에 반영한다. "생성, 삭제, 갱신"을 수행한다.
	 * 
	 * @param monitor
	 *            진행 상태를 관리하는 상태 모니터
	 * @throws Exception
	 */
	public void upload(IProgressMonitor monitor) throws Exception {
		uploadPicture(monitor);
		create(monitor);
		delete(monitor);
		update(monitor);

		Session session = getSession();
		Transaction tx = session.beginTransaction();
		tx.commit();
	}

	/**
	 * Snownote에서 첨부한 이미지 파일을 Flickr 계정에 업로드 한다.
	 * 
	 * @param monitor
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void uploadPicture(IProgressMonitor monitor) throws Exception {
		IPreferenceStore store = SnowNotePlugin.getDefault()
				.getPreferenceStore();
		String apiKey = store.getString(PreferenceKeys.FLICKR_API_KEY);
		String sharedSecret = store
				.getString(PreferenceKeys.FLICKR_SHARED_SECRET);

		// TODO 현재는 Flickr 사진 업로드 이외에 동작 허용하지 않기 때문에
		// 생성된 파일은 무조건 사진으로 간주하고 Flickr에 업로드.
		List<SnowAttachment> createdFiles = getSession().createQuery(
				"from SnowAttachment where CREATED = true").list();
		SnowFlickr flickr = new SnowFlickr(apiKey, sharedSecret, NoteUtil
				.getUserDirPath());
		if (flickr.checkAuthority() == SnowFlickr.STATUS_AUTHORIZED) {
			for (SnowAttachment a : createdFiles) {
				monitor.subTask("Flickr 사진 업로드 중: " + a.getName());
				String path = flickr.upload(a);

				NoteUtil.internalLinksToFlickr(a, path);

				a.setCreated(false);
				a.setPath(path);
				monitor.worked(1);
			}
		} else {
			monitor.worked(createdFiles.size());
		}
	}

	/**
	 * SnowNote에서 생성된 모든 페이지를 Springnote 계정에 생성한다.
	 * 
	 * @param monitor
	 *            진행 상태를 관리하는 상태 모니터
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void create(IProgressMonitor monitor) throws IOException,
			SAXException, ParseException {
		List<SnowPage> createdPages = getSession().createQuery(
				"from SnowPage where CREATED = true and DELETED = false")
				.list();

		for (SnowPage page : createdPages) {
			if (monitor.isCanceled())
				return;
			monitor.subTask("생성중: " + page.getTitle());

			create(page);

			monitor.worked(1);
		}
	}

	/**
	 * SnowPage를 Springnote 계정에 생성한다. 생성된 정보로 부터 생성시간 등의 정보를 갱신하고 페이지 상태를 동기화
	 * 상태로 처리한다. 중복된 이름의 페이지가 서버에 존재하면 "이름(1)"의 형식으로 변경해서 생성한다.
	 * 
	 * @param page
	 *            스프링노트 계정에 생성하려는 대상 페이지
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParseException
	 */
	private void create(SnowPage page) throws IOException, SAXException,
			ParseException {
		Page ret = null;
		try {
			ret = getSpringNote().addPage(page.toPage());
		} catch (NoteException e) { // page name conflict
			page.setTitle(NoteUtil.conciliateTitle(page.getTitle()));
			create(page);
			return;
		}

		page.load(ret);
		page.setCreated(false);
	}

	/**
	 * SnowNote에서 삭제된 페이지를 Springnote 서버에서 삭제한다. 삭제대상 페이지에 하위 페이지가 존재하면 하위 페이지를
	 * 상위로 옮기고 삭제 대상 페이지만을 삭제한다.
	 * 
	 * @param monitor
	 *            진행 상태를 관리하는 상태 모니터
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void delete(IProgressMonitor monitor) throws IOException,
			SAXException, ParseException {
		List<SnowPage> deletedPages = getSession().createQuery(
				"from SnowPage where DELETED = true").list();
		for (SnowPage page : deletedPages) {
			if (monitor.isCanceled())
				return;
			monitor.subTask("삭제중: " + page.getTitle());

			SnowPage parent = page.getParent();

			for (SnowPage child : page.getChildren()) {
				move(child, parent);
			}

			page.setChildren(null);
			parent.removeChild(page);

			getSession().delete(page);

			// SnowNote에서 생성되고/삭제 대상인 페이지는 서버에는 없으므로 반영 필요없음
			if (!page.isCreated()) {
				try {
					getSpringNote().removePage(page.getIdentifier());
				} catch (java.io.FileNotFoundException e) {
					// do nothing. Server에서 이미 삭제됨.
				}
			}

			page = null;

			monitor.worked(1);
		}
	}

	/**
	 * Springnote 서버에서 source 페이지를 Parent의 하위 페이지로 옮긴다. 중복된 이름의 페이지가 서버에 존재하면
	 * "이름(1)"의 형식으로 변경해서 생성한다. <br />
	 * [Note: springnote서버에서는 move를 갱신으로 처리(시간변경)하지 않으므로 옮긴 후의 내용 다시 load필요 없음.
	 * 
	 * @param source
	 *            이동하려는 페이지
	 * @param target
	 *            페이지를 옮기려는 위치 (Parent)
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParseException
	 */
	private void move(SnowPage source, SnowPage target) throws IOException,
			SAXException, ParseException {
		source.setParent(target);

		try {
			getSpringNote().updatePage(source.toPage());
		} catch (IOException e) {
			source.setTitle(NoteUtil.conciliateTitle(source.getTitle()));
			// TODO IOException 발생 후 이름 변경 후 시도해도 계속해서 IOException 발생함.
			// 생성의 경우와 다른 점 찾아서 수정할 것. 보류.
			// move(source, target);
		}
	}

	/**
	 * SnowNote에서 수정된 페이지를 Springnote 서버에 반영한다.
	 * 
	 * @param monitor
	 *            진행 상태를 관리하는 상태 모니터
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void update(IProgressMonitor monitor) throws IOException,
			SAXException, ParseException {
		List<SnowPage> modifiedPages = getSession().createQuery(
				"from SnowPage where MODIFIED = true").list();

		for (SnowPage page : modifiedPages) {
			if (monitor.isCanceled())
				return;
			monitor.subTask("수정중: " + page.getTitle());

			Page remotePage = getSpringNote().getPage(page.getIdentifier());

			// local이 remote보다 최신인 경우에만 갱신
			if (remotePage.getLastModifiedDate()
					.compareTo(page.getModifiedAt()) < 0) {
				NoteUtil.internalLinksToExternal(page);
				Page ret = getSpringNote().updatePage(page.toPage());
				page.load(ret);
				// remote가 최신이라면 download 과정에서 변경할 것.
				page.setModified(false);
			}
			monitor.worked(1);
		}
	}

	public void download(IProgressMonitor monitor) throws IOException,
			SAXException, ParseException {
		List<PageMeta> pagesMeta = getPageMetaList();

		for (PageMeta pageMeta : pagesMeta) {
			if (monitor.isCanceled())
				return;

			monitor.subTask("다운로드: " + pageMeta.getName());

			SnowPage page = createSnowPageFromPageMeta(pageMeta);
			List<Attachment> attachments = getSpringNote().getAttachments(
					page.getIdentifier());

			download(page, attachments);
			monitor.worked(1);
		}
	}

	private SnowPage createSnowPageFromPageMeta(PageMeta pageMeta)
			throws IOException, SAXException, ParseException {
		int pageId = pageMeta.getId();
		Document doc = springnote.getPageDocument(pageId);
		Page page = createPageFromDocument(doc);
		SnowPage notePage = new SnowPage(page);

		return notePage;
	}

	private Page createPageFromDocument(Document doc) throws ParseException {
		return getSpringNote().getPageAsDocument(doc);
	}

	@SuppressWarnings("unchecked")
	public void download(SnowPage page, List<Attachment> attachments)
			throws IOException {
		Session session = getSession();

		Query query = session.createQuery("from SnowPage where IDENTIFIER = ?");
		query.setInteger(0, page.getIdentifier());
		List list = query.list();

		if (list.size() == 1) {
			SnowPage localPage = (SnowPage) list.get(0);

			// 같거나 local이 최신
			if (page.getModifiedAt().compareTo(localPage.getModifiedAt()) > 0) {
				localPage.load(page.toPage());
				localPage.setModified(false);
			}

			page = localPage;
		}

		NoteUtil.externalLinksToInternal(page);

		// TODO Refactor me!
		for (Attachment a : attachments) {
			SnowAttachment attachment = new SnowAttachment(a);
			attachment.setPage(page);

			if (!page.contains(attachment))
				page.addAttachment(attachment);

			File file = new File(SnowNotePlugin.getWorkspacePath()
					+ File.separator + SnowNotePlugin.getUserDomain()
					+ File.separator + attachment.getPage().getIdentifier()
					+ File.separator + attachment.getIdentifier());
			attachment.setPath(file.getAbsolutePath());
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				getSpringNote().downloadAttachment(a, file);
			}

		}

		List<SnowPage> children;

		if (pageHierarchyMap.containsKey(page.getParentIdentifier())) {
			children = pageHierarchyMap.get(page.getParentIdentifier());
		} else {
			children = new ArrayList<SnowPage>();
			pageHierarchyMap.put(page.getParentIdentifier(), children);
		}

		children.add(page);
	}

	/**
	 * 수정시간에 따른 페이지 목록을 반환한다.
	 * 
	 * @param begin
	 *            검색 시작일 (포함)
	 * @param end
	 *            검색 종료일 (포함하지 않음)
	 * @return begin과 end 사이에 수정된 모든 페이지
	 */
	@SuppressWarnings("unchecked")
	public List<SnowPage> getPageByDate(Date begin, Date end) {
		Query query = getSession().createQuery(
				"from SnowPage where MODIFIED_AT <= ? and MODIFIED_AT > ?");
		query.setTimestamp(0, begin);
		query.setTimestamp(1, end);

		List<SnowPage> pages = query.list();

		return pages != null ? pages : new ArrayList<SnowPage>();
	}

	/**
	 * 사용자 계정 DB와 엮인 Hibernate 세션을 반환한다.
	 * 
	 * @return
	 */
	public Session getSession() {
		if (session == null)
			session = HibernateUtil.getSessionFactory().openSession();
		return session;
	}
}