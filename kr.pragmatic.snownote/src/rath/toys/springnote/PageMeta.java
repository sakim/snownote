package rath.toys.springnote;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import rath.toys.springnote.util.DateUtil;

/**
 * 페이지의 메타 정보를 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/04/02
 */
public class PageMeta implements Serializable
{
	protected static final SimpleDateFormat fmtDate = 
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private int id;
	private String name;
	private Date lastModified;

	public PageMeta()
	{
		fmtDate.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * 페이지 번호를 설정한다.
	 */
	public void setId( int id )
	{
		this.id = id;
	}

	/**
	 * 페이지 번호를 가져온다.
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * 페이지 이름을 설정한다.
	 */
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * 페이지 이름을 가져온다.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * 이 페이지의 마지막 수정일을 가져온다.
	 */
	public Date getLastModifiedDate()
	{
		return this.lastModified;
	}

	/**
	 * 이 페이지의 마지막 수정일을 String 형태로 설정한다.
	 */
	public void setLastModifiedDate( String date ) throws ParseException
	{
		if( date==null )
			return;
		setLastModifiedDate( fmtDate.parse(DateUtil.convertToJavaDate(date)) );
	}
	
	/**
	 * 이 페이지의 마지막 수정일을 설정한다.
	 */
	public void setLastModifiedDate( Date d )
	{
		this.lastModified = d;
	}

	public String toString()
	{
		return "[Page#" + this.id + "] modified=" + this.lastModified + ", name=" + this.name;
	}

	public boolean equals(Object o)
	{
		if (o instanceof PageMeta) {
			PageMeta other = (PageMeta)o;
			if (this.id == other.getId() && this.name.equals(other.getName())) {
				return true;
			}
		}
		return false;

	}

	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + new Integer(this.id).hashCode();
		hash = hash * 31 + (this.name == null ? 0 : this.name.hashCode());
		return hash;
	}
}
