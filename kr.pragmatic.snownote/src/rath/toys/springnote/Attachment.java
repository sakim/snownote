package rath.toys.springnote;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import rath.toys.springnote.event.*;
import rath.toys.springnote.util.DateUtil;
/**
 * 스프링노트 특정 페이지에 첨부된 파일 하나를 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $id$ since 2007/05/06
 */
public class Attachment
{
	private static final SimpleDateFormat fmtDate = 
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private int id;
	private String title;
	private Date createdDate;
	private int pageId;
	private String description;

	private ProgressListener listener;

	public Attachment()
	{
		fmtDate.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public Attachment( int pageId )
	{
		this.pageId = pageId;
	}

	/** 
	 * 첨부 파일의 고유 아이디를 설정한다.
	 */
	public void setId( int id )
	{
		this.id = id;
	}

	/**
	 * 첨부 파일의 고유 아이디를 가져온다.
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * 첨부 파일의 제목을 설정한다.
	 */
	public void setTitle( String title )
	{
		this.title = title;
	}

	/**
	 * 첨부 파일의 제목을 가져온다.
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * 파일을 첨부한 시간을 설정한다.
	 */
	public void setCreationDate( Date date )
	{
		this.createdDate = date;
	}

	/**
	 * 파일을 첨부한 시간을 설정한다.
	 */
	public void setCreationDate( String date ) throws ParseException
	{
		setCreationDate( fmtDate.parse(DateUtil.convertToJavaDate(date)) );
	}

	/**
	 * 파일을 첨부한 시간을 가져온다.
	 */
	public Date getCreationDate()
	{
		return this.createdDate;
	}

	/**
	 * 첨부된 파일을 포함하고 있는 페이지 번호를 설정한다.
	 */
	public void setPageId( int pageId )
	{
		this.pageId = pageId;
	}

	/**
	 * 첨부된 파일을 포함하고 있는 페이지 번호를 가져온다.
	 */
	public int getPageId()
	{
		return this.pageId;
	}

	/**
	 * 첨부된 파일의 세부사항을 설정한다.
	 */
	public void setDescription( String desc )
	{
		this.description = desc;
	}

	/**
	 * 첨부된 파일의 세부사항(2007-05-06 현재 파일의 크기)을 가져온다.
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * 이 첨부된 파일의 크기를 가져온다. 이 내용은 getDescription()의 내용과 동일하지만,
	 * 언젠가부터 동작하지 않을 수도 있으니 주의한다.
	 */
	public long getFileSize() 
	{
		return Long.parseLong(this.description);
	}

	public void setFileSize( long size )
	{
		this.description = String.valueOf(size);
	}

	/**
	 * 이 첨부파일을 전송할 경우 진행상황을 보고받을 이벤트 리스너를 지정한다.
	 */
	public void setProgressListener( ProgressListener l )
	{
		this.listener = l;
	}

	/**
	 * 이 첨부파일을 전송할 경우 진행상황을 보고받을 이벤트 리스너를 가져온다.
	 */
	public ProgressListener getProgressListener()
	{
		return this.listener;
	}
}
