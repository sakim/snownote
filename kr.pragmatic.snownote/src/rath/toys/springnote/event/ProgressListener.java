package rath.toys.springnote.event;

import java.util.EventListener;
/**
 * 첨부파일 다운로드 및 업로드 시 진행상황을 알려주는 이벤트. 
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0, $Id$ since 2007/05/06
 */
public interface ProgressListener extends EventListener
{
	/**
	 * 첨부파일이 16KB 전송될때마다 해당 사실을 알려준다.
	 */
	public void transferProgress( ProgressEvent e );
}
