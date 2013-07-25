package rath.toys.springnote.util;

/**
 *
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/05/06
 */
public class DateUtil
{
	public static String convertToJavaDate( String date )
	{
		if( date==null )
			return null;
		if( date.length() > 20 )
		{
			int i0 = date.lastIndexOf(':');
			if( i0>20 )
				return date.substring(0, i0).concat(date.substring(i0+1));
		}
		return date;
	}
}
