package kr.pragmatic.snownote.core;

import java.io.File;
import java.util.Date;

import rath.toys.springnote.Attachment;

/**
 * Springnote로부터 내려받은 하나의 첨부파일을 표현하는 SnowNote의 클래스이다.
 * 
 * @author sakim
 * 
 */
public class SnowAttachment {
	private Long id;

	/**
	 * Attachment의 고유 ID (Hibernate가 관리하는 ID와 별개로 관리함.)
	 */
	private int identifier;
	private String name;
	private long size;
	private Date createdAt;
	private String path;

	/**
	 * SnowNote에서 새롭게 추가된 첨부파일인지 여부
	 */
	private boolean created = false;

	private SnowPage page;

	public SnowAttachment() {
	}

	/**
	 * Snownote에서 생성한 파일에 대한 정보를 저장하기 위한 생성자 "생성(created)" 상태를 참(true)로 기록해 추후
	 * 업로드 대상이된다.
	 * 
	 * @param file
	 */
	public SnowAttachment(File file) {
		setName(file.getName());
		setSize(file.length());
		setCreated(true);
		setPath(file.getAbsolutePath());
	}

	/**
	 * Springnote로부터 가져온 첨부파일 정보를 저장하기 위한 생성자
	 * 
	 * @param attachment
	 */
	public SnowAttachment(Attachment attachment) {
		load(attachment);
	}

	private void load(Attachment attachment) {
		setIdentifier(attachment.getId());
		setName(attachment.getTitle());
		setSize(attachment.getFileSize());
		setCreatedAt(attachment.getCreationDate());
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

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SnowPage getPage() {
		return page;
	}

	public void setPage(SnowPage page) {
		this.page = page;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SnowAttachment) {
			SnowAttachment a = (SnowAttachment) obj;
			if (this.getIdentifier() == a.getIdentifier()) {
				return true;
			}
		}

		return super.equals(obj);
	}

	protected boolean isCreated() {
		return created;
	}

	protected void setCreated(boolean created) {
		this.created = created;
	}
}
