package rath.toys.springnote;

import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import rath.toys.springnote.util.DateUtil;

/**
 * 
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/02/18
 */
public class Page extends PageMeta implements Serializable {
	private static final long serialVersionUID = 0x10203040501111L;

	public static final int ROOT = 0;
	public static final int NO_PARENT = -1;

	private String content;
	private Date creationDate;
	private int version;
	private URL lastContributor;
	private String tags;
	private int parentId;
	private boolean isRoot = false;

	public Page() {

	}

	/**
	 * 이 페이지의 최종 수정자 OpenID를 지정한다.
	 */
	public void setLastContributor(URL openid) {
		this.lastContributor = openid;
	}

	/**
	 * 이 페이지의 최소 수정자 OpenID를 가져온다.
	 */
	public URL getLastContributor() {
		return this.lastContributor;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addContent(String str) {
		StringBuilder sb = new StringBuilder(content);
		sb.append("\n");
		sb.append("<P>");
		sb.append(str);
		sb.append("</P>");
		this.content = sb.toString();
	}

	public String getContent() {
		return this.content;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public String getCreationDateAsString() {
		return fmtDate.format(this.creationDate);
	}

	public void setCreationDate(String date) throws ParseException {
		if (date != null)
			setCreationDate(fmtDate.parse(DateUtil.convertToJavaDate(date)));
	}

	public void setCreationDate(Date d) {
		this.creationDate = d;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return this.version;
	}

	public String toString() {
		return "[Page#" + getId() + "] created=" + this.creationDate
				+ ", name=" + getName() + "]";
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}

	public void setParentId(String parentId) {
		if (parentId.trim().equals("")) {
			this.parentId = ROOT;
			isRoot = true;
			return;
		}

		try {
			this.parentId = Integer.parseInt(parentId);
		} catch (NumberFormatException e) {
			this.parentId = NO_PARENT;
		}
	}

	public int getParentId() {
		return parentId;
	}

	public boolean isRoot() {
		return isRoot;
	}

}
