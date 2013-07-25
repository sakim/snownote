package kr.pragmatic.snownote.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class BrowserUtil {
	/**
	 * Open a link
	 * 
	 * @throws PartInitException
	 * @throws MalformedURLException
	 */
	public static void openLink(String href) throws PartInitException,
			MalformedURLException {
		// format the href for an html file (file:///<filename.html>
		// required for Mac only.
		if (href.startsWith("file:")) { //$NON-NLS-1$
			href = href.substring(5);
			while (href.startsWith("/")) { //$NON-NLS-1$
				href = href.substring(1);
			}
			href = "file:///" + href; //$NON-NLS-1$
		}
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench()
				.getBrowserSupport();
		IWebBrowser browser = support.getExternalBrowser();
		browser.openURL(new URL(urlEncodeForSpaces(href.toCharArray())));
	}

	/**
	 * This method encodes the url, removes the spaces from the url and replaces
	 * the same with <code>"%20"</code>. This method is required to fix Bug
	 * 77840.
	 * 
	 * @since 3.0.2
	 */
	private static String urlEncodeForSpaces(char[] input) {
		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++) {
			if (input[i] == ' ') {
				retu.append("%20"); //$NON-NLS-1$
			} else {
				retu.append(input[i]);
			}
		}
		return retu.toString();
	}
}
