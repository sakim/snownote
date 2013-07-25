package rath.toys.springnote.event;

import java.util.EventObject;
/**
 * 첨부파일 다운로드 및 업로드 시 진행상황을 알려주는 이벤트. 
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0, $Id$ since 2007/05/06
 */
public class ProgressEvent extends EventObject
{
	private long offset;
	private long total;

	public ProgressEvent( Object src, long offset, long total )
	{
		super( src );

		this.offset = offset;
		this.total = total;
	}

	/**
	 * 전송된 바이트 수.
	 */
	public long getTransferredBytes()
	{
		return this.offset;
	}

	/**
	 * 전체 바이트 수.
	 */
	public long getTotalBytes()
	{
		return this.total;
	}
}
