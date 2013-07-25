package kr.pragmatic.snownote.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.pragmatic.snownote.actions.SnowPageActionFilter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IActionFilter;

import rath.toys.springnote.Page;

public class SnowPage implements IAdaptable {

	private Long id;

	/**
	 * page의 고유 (새로 생성한 Page는 Springnote에 동기화 전 없을 수 있음. Hibernate가 관리하는 Id와 별개로
	 * 관리함.)
	 */
	private int identifier;

	private String title = "";

	private String contents = "";

	private String tags = "";

	private Date createdAt;

	private Date modifiedAt;

	private boolean deleted;

	private boolean modified;

	private boolean created;

	private int version;

	private int parentIdentifier;

	private SnowPage parent;
	private List<SnowPage> children = new ArrayList<SnowPage>();

	private List<SnowAttachment> attachments = new ArrayList<SnowAttachment>();

	private static IActionFilter actionFilter = new SnowPageActionFilter();

	public SnowPage() {
	}

	public SnowPage(Page page) {
		load(page);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents == null ? "" : contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTags() {
		return tags == null ? "" : tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isCreated() {
		return created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public int getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(int parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isRoot() {
		if (parent == null)
			return true;
		return false;
	}

	public boolean hasChildren() {
		return children.size() > 0 ? true : false;
	}

	/**
	 * Allow instances of this class to be adapted to other interfaces. This is
	 * required to implement action filters.
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 * @see kr.pragmatic.snownote.actions.SnowPageActionFilter
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IActionFilter.class) {
			return actionFilter;
		}

		return null;
	}

	public SnowPage getParent() {
		return parent;
	}

	public void setParent(SnowPage parent) {
		this.parent = parent;
	}

	public void setChildren(List<SnowPage> children) {
		if (children == null)
			children = new ArrayList<SnowPage>();
		this.children = children;
	}

	public boolean addChild(SnowPage child) {
		
		if (child.getParent() != null)
			child.getParent().removeChild(child);
		child.setParent(this);
		child.setParentIdentifier(getIdentifier());

		return children.add(child);
	}

	public boolean removeChild(SnowPage child) {
		child.setParent(null);
		
		return children.remove(child);
	}

	public List<SnowPage> getChildren() {
		return children;
	}

	public void setAttachments(List<SnowAttachment> attachments) {
		this.attachments = attachments;
	}

	public boolean addAttachment(SnowAttachment attachment) {
		
		if (attachment.getPage() != null)
			attachment.getPage().removeAttachment(attachment);
		attachment.setPage(this);
		
		return attachments.add(attachment);
	}

	public boolean removeAttachment(SnowAttachment attachment) {
		attachment.setPage(null);
		
		return attachments.remove(attachment);
	}

	public List<SnowAttachment> getAttachments() {
		return attachments;
	}

	public void load(Page page) {
		setIdentifier(page.getId());
		setTitle(page.getName());
		setContents(page.getContent());
		setTags(page.getTags());
		setParentIdentifier(page.getParentId());
		setVersion(page.getVersion());

		setCreatedAt(page.getCreationDate());
		setModifiedAt(page.getLastModifiedDate());
	}

	public Page toPage() {
		Page page = new Page();
		page.setName(getTitle());
		page.setId(getIdentifier());
		page.setContent(getContents());
		page.setTags(getTags());
		page.setVersion(getVersion());
		page.setCreationDate(getCreatedAt());
		page.setLastModifiedDate(getModifiedAt());
		page.setParentId(Integer.toString(getParentIdentifier()));

		return page;
	}

	public boolean contains(SnowAttachment attachment) {
		return attachments.contains(attachment);
	}
}
