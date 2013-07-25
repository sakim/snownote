package kr.pragmatic.snownote.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.pragmatic.snownote.utils.NoteUtil;

import org.eclipse.ui.PartInitException;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;

public class SnowFlickr {
	public static final int STATUS_FAILED = -1;
	public static final int STATUS_REQUIRE_AUTHORIZE = 0;
	public static final int STATUS_REQUIRE_CONFIRM = 1;
	public static final int STATUS_AUTHORIZED = 2;

	private Flickr flickr = null;
	private AuthStore authStore = null;
	private String sharedSecret = null;

	private String frob;

	public SnowFlickr(String apiKey, String sharedSecret, String authsDir)
			throws IOException {
		this.flickr = new Flickr(apiKey);
		this.sharedSecret = sharedSecret;

		if (authsDir != null) {
			this.authStore = new FileAuthStore(new File(authsDir));
		}
	}

	/**
	 * 사용자 인증을 받는다.
	 * 
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws FlickrException
	 */
	public String authorize() throws IOException, SAXException,
			FlickrException, PartInitException {
		frob = this.flickr.getAuthInterface().getFrob();

		URL authUrl = this.flickr.getAuthInterface().buildAuthenticationUrl(
				Permission.READ, frob);

		return authUrl.toExternalForm();
	}

	public int confirmAuthorized() {
		try {
			Auth token = this.flickr.getAuthInterface().getToken(frob);
			RequestContext.getRequestContext().setAuth(token);
			this.authStore.store(token);

			return STATUS_AUTHORIZED;
		} catch (Exception e) {
			return STATUS_FAILED;
		}
	}

	/**
	 * 사용자 인증을 받았는지 여부를 검사한다.
	 * 
	 * @return
	 * @throws Exception
	 */
	public int checkAuthority() throws Exception {
		RequestContext rc = RequestContext.getRequestContext();
		rc.setSharedSecret(this.sharedSecret);

		if (this.authStore != null) {
			Auth[] auths = this.authStore.retrieveAll();

			if (auths.length == 0) {
				return STATUS_REQUIRE_AUTHORIZE;
			} else {
				rc.setAuth(auths[0]);
				return STATUS_AUTHORIZED;
			}
		}

		return STATUS_FAILED;
	}

	/**
	 * 첨부파일을 Flickr에 업로드한다.
	 * 
	 * @param attachment
	 * @return
	 * @throws IOException
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws PartInitException
	 */
	public String upload(SnowAttachment attachment) throws IOException,
			FlickrException, SAXException, PartInitException {
		Uploader uploader = new Uploader(flickr.getApiKey());

		UploadMetaData meta = new UploadMetaData();
		meta.setContentType(Flickr.CONTENTTYPE_PHOTO);
		meta.setPublicFlag(true);
		meta.setTitle(NoteUtil.currentDate()); // Flickrj가 한글처리 못하므로 날짜로 표현
		meta
				.setDescription("Uploaded by Snownote: a springnote(www.springnote.com) client");

		List<String> tags = new ArrayList<String>();
		tags.add("Springnote"); // 한글처리 못하므로 영문으로 고정함
		tags.add("Snownote");
		meta.setTags(tags);

		BufferedInputStream stream = null;
		String photoId;
		try {
			stream = new BufferedInputStream(new FileInputStream(new File(
					attachment.getPath())));
			photoId = uploader.upload(stream, meta);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					// do nothing
				}
		}

		Photo p = flickr.getPhotosInterface().getPhoto(photoId);

		return p.getMediumUrl();
	}
}
