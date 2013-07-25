package rath.toys.springnote;

/**
 *
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/04/02
 */
public class LicenseAgreementException extends RuntimeException
{
	private String sourcePage = null;

	public LicenseAgreementException( String sourcePage )
	{
		setSourcePage(sourcePage);
	}

	public void setSourcePage( String url )
	{
		this.sourcePage = url;
	}

	public String getSourcePage()
	{
		return this.sourcePage;
	}
}
