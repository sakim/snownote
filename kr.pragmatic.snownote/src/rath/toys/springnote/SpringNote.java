package rath.toys.springnote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import rath.toys.springnote.event.ProgressEvent;
import rath.toys.springnote.event.ProgressListener;
import rath.toys.springnote.util.BASE64;

/**
 * 
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/02/18 1.1, 2007/05/06
 */
public class SpringNote {
	private static final SimpleDateFormat fmtDate = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	private boolean isDebug = false;
	private URL openId;
	private String username;
	private String userKey;
	private String applicationKey;
	private List<String> pageList = new ArrayList<String>();

	private Random rng = new Random(System.currentTimeMillis());

	private DocumentBuilder docBuilder;

	/**
	 * 스프링노트 접근 객체를 생성한다.
	 */
	public SpringNote() throws ParserConfigurationException {
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		HttpURLConnection.setFollowRedirects(false);
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public boolean getDebug() {
		return this.isDebug;
	}

	/**
	 * 자신의 OpenID를 설정한다. 예를들어 http://rath.myid.net/ 이다.
	 */
	public void setOpenID(URL openId) {
		this.openId = openId;
	}

	/**
	 * 이 스프링노트의 오픈아이디를 가져온다.
	 */
	public URL getOpenID() {
		return this.openId;
	}

	/**
	 * 주어진 키워드로 검색된 페이지들의 메타정보를 담은 목록을 가져온다.
	 * 
	 * @param keyword
	 *            검색어
	 */
	public List<PageMeta> searchPages(String keyword) throws IOException,
			SAXException {
		return searchPages(keyword, false);
	}

	/**
	 * 주어진 키워드로 검색된 페이지들의 메타정보를 담은 목록을 가져온다.
	 * 
	 * @param keyword
	 *            검색어
	 * @param fulltext
	 *            true일 경우 내용까지 검색, false일 경우 페이지 이름에서만 검색.
	 */
	public List<PageMeta> searchPages(String keyword, boolean fulltext)
			throws IOException, SAXException {
		List<PageMeta> searchResults = searchPagesImpl(keyword, false);

		if (fulltext) {
			// fulltext 검색일 경우, 검색엔진이 제목을 빼고 검색을 하기 때문에
			// 제목 검색 결과에 내용 검색 결과를 합쳐준다.
			// TODO 만약, 검색엔진이 제대로 fulltext 검색을 지원한다면, 이 부분을
			// 제외해도 된다.
			List<PageMeta> contentSearchResults = searchPagesImpl(keyword, true);
			for (PageMeta pageMeta : contentSearchResults) {
				if (!searchResults.contains(pageMeta)) {
					searchResults.add(pageMeta);
				}
			}
		}

		return searchResults;

	}

	/**
	 * 주어진 키워드로 검색된 페이지들의 메타 정보를 담은 목록을 가져온다. 실제 구현은 여기서 한다.
	 * 
	 * @param keyword
	 *            검색어
	 * @param fulltext
	 *            true일 경우 내용까지 검색, false일 경우 페이지 이름에서만 검색.
	 */
	protected List<PageMeta> searchPagesImpl(String keyword, boolean fulltext)
			throws IOException, SAXException {
		String url = String.format("https://api.springnote.com/pages/search?"
				+ (username == null ? "" : "domain=" + username + "&")
				+ "q=%s&fulltext=%d", URLEncoder.encode(keyword, "UTF-8"),
				fulltext ? 1 : 0);
		Document doc = request(url, "GET");

		List<PageMeta> ret = new ArrayList<PageMeta>();

		NodeList nodeList = doc.getElementsByTagName("page");
		int len = Math.min(5, nodeList.getLength());
		for (int i = 0; i < len; i++) {
			Element elemPage = (Element) nodeList.item(i);
			int id = Integer.parseInt(getTextAsName(elemPage, "identifier"));
			String name = getTextAsName(elemPage, "title");

			PageMeta pm = new PageMeta();
			pm.setId(id);
			pm.setName(name);

			ret.add(pm);
		}

		return ret;
	}

	/**
	 * http://<b>xxxx</b>.springnote.com/ 에서 Bold로 표시된 부분에 들어갈, 스프링노트 이름을
	 * 지정한다.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * http://<b>xxxx</b>.springnote.com/ 에서 Bold로 표시된 부분에 들어가있는 스프링노트 이름을
	 * 가져온다.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 스프링노트 기본설정 페이지에 나와있는 '사용자 키'를 입력한다.
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	/**
	 * 스프링노트 '사용자 키'를 가져온다.
	 */
	public String getUserKey() {
		return this.userKey;
	}

	/**
	 * 스프링노트 개발자 커뮤니티에서 발급받은 <b>애플리케이션 인증키</b>를 지정한다.
	 */
	public void setApplicationKey(String key) {
		this.applicationKey = key;
	}

	/**
	 * 스프링노트 개발자 커뮤니티에서 발급받은 <b>애플리케이션 인증키</b>를 가져온다.
	 */
	public String getApplicationKey() {
		return this.applicationKey;
	}

	private String getHexaDecimal(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int v = (int) b[i];
			if (v < 0)
				v += 0x100;
			String s = Integer.toHexString(v);
			if (s.length() == 1)
				sb.append('0');
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * 현재 노트에 등록된 모든 페이지의 목록을 가져온다.
	 * 
	 * @deprecated 메서드 이름에 일관성이 없음.
	 */
	public List<PageMeta> loadIndices() throws IOException, SAXException,
			ParseException {
		return getPages();
	}

	/**
	 * 현재 노트에 등록된 모든 페이지의 목록을 가져온다.
	 */
	public List<PageMeta> getPages() throws IOException, SAXException,
			ParseException {
		Document doc = request("https://api.springnote.com/pages"
				+ (username == null ? "" : "?domain=" + username), "GET");

		pageList.clear();
		List<PageMeta> metas = new ArrayList<PageMeta>();

		NodeList nl = doc.getElementsByTagName("page");
		for (int i = 0; i < nl.getLength(); i++) {
			Element page = (Element) nl.item(i);
			int id = Integer.parseInt(getTextAsName(page, "identifier"));
			String name = getTextAsName(page, "title");
			String modified = getTextAsName(page, "date_modified");

			pageList.add(name);

			PageMeta meta = new PageMeta();
			meta.setId(id);
			meta.setName(name);
			meta.setLastModifiedDate(modified);
			metas.add(meta);
		}

		return metas;
	}

	/**
	 * 주어진 이름으로 새 페이지를 생성한다.
	 * 
	 * @param Page
	 *            생성할 페이지
	 * @return 생성된 페이지
	 * 
	 * @exception NoteException
	 *                이미 해당 이름으로 페이지가 존재할 때.
	 */
	public Page addPage(Page page) throws IOException, SAXException,
			ParseException {
		Document doc = pageAsDocument(page);
		
		Document ret = request("https://api.springnote.com/pages"
				+ (username == null ? "" : "?domain=" + username), "POST", doc);

		try {
			page = getPageAsDocument(ret);
		} catch (NoteException e) {
			throw e;
		}

		return page;
	}

	/**
	 * 요청한 이름을 가진 페이지를 가져온다.
	 */
	public Page getPage(String pageName) throws IOException, SAXException,
			ParseException {
		List<PageMeta> metas = loadIndices();
		for (PageMeta m : metas) {
			if (m.getName().equals(pageName))
				return getPage(m.getId());
		}

		return null;
	}

	/**
	 * 요청한 페이지 아이디를 가진 페이지 내용을 Document로 가져온다. 전체 페이지 내용을 XML 요소로 필요로 하는 경우를 위해서
	 * 분리함.
	 * 
	 * @author sakim
	 */
	public Document getPageDocument(int pageId) throws IOException,
			SAXException, ParseException {
		Document doc = request(String.format(
				"https://api.springnote.com/pages/%d", pageId)
				+ (username == null ? "" : "?domain=" + username), "GET");

		return doc;
	}

	/**
	 * 요청한 페이지 아이디를 가진 페이지 내용을 가져온다.
	 */
	public Page getPage(int pageId) throws IOException, SAXException,
			ParseException {
		Document doc = getPageDocument(pageId);
		return getPageAsDocument(doc);
	}

	/**
	 * 보유중인 페이지 개수를 가져온다. loadIndices()를 호출한 후에만 정상적인 작동을 할 것이며 그렇지 않은 경우 항상 0을
	 * 리턴할 것이다.
	 */
	public int getPageCount() {
		return pageList.size();
	}

	/**
	 * OpenAPI 호출 결과로 리턴된 DOM 내용을 기반으로 Page 객체를 생성해준다.
	 * 
	 * @param doc
	 *            API 호출 결과로 리턴된 DOM object
	 * @return Page 객체
	 */
	public Page getPageAsDocument(Document doc) throws ParseException {
		NodeList nl = doc.getElementsByTagName("error");
		if (nl.getLength() > 0) {
			NodeList nl2 = ((Element) nl.item(0))
					.getElementsByTagName("description");
			throw new NoteException(nl2.item(0).getFirstChild().getNodeValue());
		}

		Page page = new Page();
		page.setId(Integer.parseInt(getTextAsName(doc, "identifier")));
		page.setName(getTextAsName(doc, "title"));
		page.setContent(getTextAsName(doc, "source"));
		page.setVersion(Integer.parseInt(getTextAsName(doc, "version")));
		page.setCreationDate(getTextAsName(doc, "date_created"));
		page.setLastModifiedDate(getTextAsName(doc, "date_modified"));
		page.setTags(getTextAsName(doc, "tags"));
		page.setParentId(getTextAsName(doc, "relation_is_part_of"));
		try {
			page.setLastContributor(new URL(getTextAsName(doc,
					"contributor_modified")));
		} catch (MalformedURLException e) {
		} catch (NullPointerException e) {
		}
		return page;
	}

	/**
	 * 요청한 페이지를 지운다.
	 * 
	 * @return 삭제된 페이지의 이전 내용
	 */
	public Page removePage(String pageName) throws IOException, SAXException,
			ParseException {
		Integer pageId = null;
		if (pageId == null)
			throw new IllegalArgumentException(
					"Requested page name didn't exist: " + pageName);
		return removePage(pageId);
	}

	/**
	 * 요청한 아이디를 가지는 페이지를 지운다.
	 * 
	 * @return 삭제된 페이지의 이전 내용
	 */
	public Page removePage(int pageId) throws IOException, SAXException,
			ParseException {
		Document doc = request(String.format(
				"https://api.springnote.com/pages/%d", pageId)
				+ (username == null ? "" : "?domain=" + username), "DELETE");

		return getPageAsDocument(doc);
	}

	/**
	 * 요청한 페이지의 내용을 갱신한다.
	 * 
	 * @return 갱신된 내용을 반영한 페이지.
	 */
	public Page updatePage(Page page) throws IOException, SAXException,
			ParseException {
		Document doc = pageAsDocument(page);

		Document ret = request(String.format(
				"https://api.springnote.com/pages/%d", page.getId())
				+ (username == null ? "" : "?domain=" + username), "PUT", doc);

		return getPageAsDocument(ret);
	}

	/**
	 * 페이지를 REST 통신을 위한 XML Document로 변환한다.
	 * @param page 서버에 전송하기 위한 페이지
	 * @return 변환된 XML Document
	 */
	private Document pageAsDocument(Page page) {
		Document doc = docBuilder.newDocument();
		Element epage = doc.createElement("page");
		Element etitle = doc.createElement("title");
		etitle.appendChild(doc.createTextNode(page.getName()));
		Element econtent = doc.createElement("source");
		econtent.appendChild(doc.createTextNode(page.getContent()));
		Element etags = doc.createElement("tags");
		etags.appendChild(doc.createTextNode(page.getTags()));
		Element eparent = doc.createElement("relation_is_part_of");
		eparent.appendChild(doc.createTextNode(Integer.toString(page
				.getParentId())));

		epage.appendChild(etitle);
		epage.appendChild(econtent);
		epage.appendChild(etags);
		epage.appendChild(eparent);
		doc.appendChild(epage);

		return doc;
	}

	/**
	 * 페이지의 이름으로 Index를 검색해준다.
	 * 
	 * @return 만약 요청한 이름이 존재하지 않거나, loadIndices를 부르지 않고 호출했을 경우 -1을 리턴하고, 그렇지
	 *         않으면 검색된 index를 리턴한다. public int getIndexAsName( String name ) { //
	 *         FIXME: 검색해서 리턴해주도록 한다. return -1; }
	 */

	private String getTextAsName(Document parent, String childName) {
		return getTextAsName(parent.getDocumentElement(), childName);
	}

	private String getTextAsName(Element parent, String childName) {
		NodeList nl = parent.getElementsByTagName(childName);
		if (nl.getLength() == 0)
			return null;
		Element child = (Element) nl.item(0);
		Node firstChild = child.getFirstChild();
		if (firstChild != null)
			return firstChild.getNodeValue();
		return "";
	}

	/**
	 * 준비된 Attribute를 담아 실제로 HTTP 요청을 날려 응답결과를 Document 객체에 담아준다.
	 * 
	 * @param strUrl -
	 *            요청할 URL
	 * @param method -
	 *            GET/POST/PUT/DELETE
	 */
	protected Document request(String strUrl, String method)
			throws IOException, SAXException {
		return request(strUrl, method, null);
	}

	/**
	 * 준비된 Attribute를 담아 실제로 HTTP 요청을 날려 응답결과를 Document 객체에 담아준다.
	 * 
	 * @param strUrl -
	 *            요청할 URL
	 * @param method -
	 *            GET/POST/PUT/DELETE
	 * @param doc -
	 *            송신할 xml document (optional)
	 */
	protected Document request(String strUrl, String method, Document doc)
			throws IOException, SAXException {
		return request(new URL(strUrl), method, doc);
	}

	/**
	 * 준비된 Attribute를 담아 실제로 HTTP 요청을 날려 응답결과를 Document 객체에 담아준다.
	 * 
	 * @param url -
	 *            요청할 URL
	 * @param method -
	 *            GET/POST/PUT/DELETE
	 * @param doc -
	 *            송신할 xml document (optional)
	 */
	protected Document request(URL url, String method, Document doc)
			throws IOException, SAXException {
		if (applicationKey == null || userKey == null || openId == null)
			throw new IllegalStateException(
					"Insufficient parameters: OpenID, UserKey, AppKey");

		String authKey = String.format("Basic %s", new BASE64(false)
				.encode(String.format("%s:%s.%s", URLEncoder.encode(openId
						.toString(), "UTF-8"), this.userKey,
						this.applicationKey)));

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("Content-Type", "application/xml");
		con.addRequestProperty("Accept", "application/xml");
		con.setRequestProperty("Authorization", authKey);
		if (doc != null) {
			con.setDoOutput(true);

			TransformerFactory tf = TransformerFactory.newInstance();
			// tf.setAttribute("indent-number", new Integer(4));
			Transformer t = null;
			try {
				t = tf.newTransformer();
			} catch (TransformerConfigurationException e) {
				throw new RuntimeException(e);
			}

			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			OutputStream out = con.getOutputStream();
			try {
				if (isDebug) {
					StringWriter debugWriter = new StringWriter();
					t.transform(new DOMSource(doc), new StreamResult(
							debugWriter));
					System.out.println("== Request Output ==");
					System.out.println(debugWriter.toString());
				}

				t.transform(new DOMSource(doc), new StreamResult(
						new OutputStreamWriter(out, "utf-8")));
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			} finally {
				out.close();
			}
		}

		InputStream in = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			in = con.getInputStream();
			byte[] b = new byte[8192];
			while (true) {
				int readlen = in.read(b);
				if (readlen < 1)
					break;
				bos.write(b, 0, readlen);
			}

			if (con.getResponseCode() == 302) {
				String location = con.getHeaderField("Location");
				return request(new URL(location), "GET", null);
			}
		} catch (IOException e) {
			int err = con.getResponseCode();
			if (err == 401)
				throw new IllegalArgumentException("Invalid Application-Key!");
			if (err == 403)
				throw new IllegalArgumentException("Invalid username!");
			if (err == 500) {
				InputStream errin = con.getErrorStream();
				bos.reset();
				byte[] b = new byte[8192];
				while (true) {
					int readlen = errin.read(b);
					if (readlen < 1)
						break;
					bos.write(b, 0, readlen);
				}
				String errmsg = new String(bos.toByteArray(), "UTF-8");
				System.err.println(errmsg);
			}
			throw e;
		} finally {
			if (in != null)
				in.close();
		}
		byte[] bData = bos.toByteArray();

		if (con.getContentType().startsWith("text/html")) {
			throw new LicenseAgreementException(String.format(
					"https://%s.springnote.com/pages", getUsername()));
		}
		
		String str = new String(bData, "UTF-8");
		if (isDebug) {
			System.out.println("== Response Output ==");
			System.out.println(str);
		}
		return docBuilder.parse(new InputSource(new StringReader(str)));
	}

	/**
	 * 주어진 페이지에 첨부된 파일들의 목록을 가져온다. 이 메서드를 호출하는 것은 목적 페이지의 첨부파일 목록을 가져올 뿐, 실제로
	 * 첨부된 파일의 내용을 가져오는 operation을 수행하는 것은 아니다.
	 */
	public List<Attachment> getAttachments(PageMeta page) throws IOException,
			SAXException, ParseException {
		return getAttachments(page.getId());
	}

	/**
	 * 주어진 페이지에 첨부된 파일들의 목록을 가져온다. 이 메서드를 호출하는 것은 목적 페이지의 첨부파일 목록을 가져올 뿐, 실제로
	 * 첨부된 파일의 내용을 가져오는 operation을 수행하는 것은 아니다.
	 */
	public List<Attachment> getAttachments(int pageId) throws IOException,
			SAXException, ParseException {
		Document doc = request(String.format(
				"https://api.springnote.com/pages/%d/attachments", pageId),
				"GET");

		List<Attachment> ret = new ArrayList<Attachment>(2);
		NodeList nodeList = doc.getElementsByTagName("attachment");
		int len = nodeList.getLength();
		for (int i = 0; i < len; i++) {
			Element elemAttach = (Element) nodeList.item(i);
			int id = Integer.parseInt(getTextAsName(elemAttach, "identifier"));
			String title = getTextAsName(elemAttach, "title");
			String strDate = getTextAsName(elemAttach, "date_created");
			String desc = getTextAsName(elemAttach, "description");

			Attachment at = new Attachment();
			at.setId(id);
			at.setTitle(title);
			at.setCreationDate(strDate);
			at.setPageId(pageId);
			at.setDescription(desc);
			ret.add(at);
		}

		return ret;
	}

	/**
	 * 주어진 Attachment를 다운로드하여 지정된 OutputStream에 write 해준다.
	 */
	public void downloadAttachment(Attachment attachment, OutputStream out)
			throws IOException {
		downloadAttachmentImpl(attachment, out);
	}

	/**
	 * 주어진 Attachment를 로컬시스템의 File에 저장한다.
	 */
	public void downloadAttachment(Attachment attachment, File file)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			downloadAttachment(attachment, fos);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 주어진 pageId의 attachmentId를 다운로드하여 지정된 OutputStream에 write 해준다.
	 */
	protected void downloadAttachmentImpl(Attachment at, OutputStream out)
			throws IOException {
		int pid = at.getPageId();
		int aid = at.getId();
		ProgressListener l = at.getProgressListener();

		String authKey = String.format("Basic %s", new BASE64(false)
				.encode(String.format("%s:%s.%s", URLEncoder.encode(openId
						.toString(), "UTF-8"), this.userKey,
						this.applicationKey)));

		URL url = new URL(String.format(
				"https://api.springnote.com/pages/%d/attachments/%d", pid, aid));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream in = null;
		try {
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", authKey);

			long total = at.getFileSize();
			long current = 0L;

			in = con.getInputStream();
			byte[] buf = new byte[16384];
			while (true) {
				int readlen = in.read(buf);
				if (readlen < 1)
					break;
				out.write(buf, 0, readlen);

				current += readlen;
				if (l != null)
					l.transferProgress(new ProgressEvent(this, current, total));
			}
			out.flush();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			con.disconnect();
		}
	}

	/**
	 * 주어진 첨부파일을 해당 페이지에서 삭제한다.
	 */
	public void removeAttachment(Attachment at) throws IOException,
			SAXException {
		request(String.format(
				"https://api.springnote.com/pages/%d/attachments/%d", at
						.getPageId(), at.getId()), "DELETE");
	}

	/**
	 * 주어진 파일을 해당 페이지에 첨부합니다.
	 * <p>
	 * Attachment 객체를 생성한 후, 반드시 setPageId를 통해 첨부파일을 업로드하고자 하는 페이지 아이디를 지정해주어야
	 * 합니다. 그리고 setTitle를 통해 파일이름을 지정하지 않았을 경우 지정된 파일의 이름이 첨부파일의 title로 지정됩니다.
	 * <p>
	 * 
	 * <pre><code>
	 * PageMeta page = springnote.searchPages(&quot;임시 자료실&quot;).get(0);
	 * Attachment at = new Attachment();
	 * at.setPageId(page.getId());
	 * at.setTitle(&quot;데스노트.pdf&quot;);
	 * at.setProgressListener(new ProgressListener() {
	 * 	public void transferProgress(ProgressEvent e) {
	 * 		doSomething(e);
	 * 	}
	 * });
	 * 
	 * springnote.addAttachment(at, file);
	 * </code></pre>
	 * 
	 */
	public void addAttachment(Attachment at, File file) throws IOException,
			SAXException, ParseException {
		if (at.getTitle() == null)
			at.setTitle(file.getName());

		at.setDescription(String.valueOf(file.length()));

		FileInputStream fis = new FileInputStream(file);
		try {
			addAttachment(at, fis);
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	/**
	 * 주어진 InputStream의 끝(EOF)까지 읽어서 지정한 파일명으로 페이지에 첨부합니다.
	 */
	public void addAttachment(Attachment at, InputStream in)
			throws IOException, SAXException, ParseException {
		int pid = at.getPageId();
		ProgressListener l = at.getProgressListener();

		String authKey = String.format("Basic %s", new BASE64(false)
				.encode(String.format("%s:%s.%s", URLEncoder.encode(openId
						.toString(), "UTF-8"), this.userKey,
						this.applicationKey)));

		String boundary = "349832898984244898448024464570528145";

		URL url = new URL(String.format(
				"https://api.springnote.com/pages/%d/attachments", pid));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", authKey);
		con.setRequestProperty("Accept", "application/xml");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ boundary);
		con.setDoInput(true);
		con.setDoOutput(true);

		OutputStream out = con.getOutputStream();
		InputStream cin = null;
		try {
			PrintWriter pout = new PrintWriter(new OutputStreamWriter(out,
					"UTF-8"), true);
			pout.write("--" + boundary + "\r\n");
			pout
					.write("Content-Disposition: form-data; name=\"Filedata\"; filename=\""
							+ at.getTitle() + "\"\r\n");
			pout.write("Content-Transfer-Encoding: binary\r\n");
			pout.write("Content-Type: application/octet-stream\r\n");
			pout.write("\r\n");
			pout.flush();

			long total = at.getFileSize();
			long current = 0L;

			byte[] buf = new byte[16384];
			while (true) {
				int readlen = in.read(buf);
				if (readlen < 1)
					break;

				out.write(buf, 0, readlen);

				current += readlen;
				if (l != null)
					l.transferProgress(new ProgressEvent(this, current, total));
			}
			out.flush();

			pout.write("\r\n--" + boundary + "--\r\n\r\n");
			pout.flush();

			cin = con.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (true) {
				int readlen = cin.read(buf);
				if (readlen < 1)
					break;
				bos.write(buf, 0, readlen);
			}

			String str = new String(bos.toByteArray(), "UTF-8");
			if (isDebug)
				System.out.println(str);

			Document doc = docBuilder.parse(new InputSource(new StringReader(
					str)));

			NodeList nodeList = doc.getElementsByTagName("attachment");

			if (nodeList.getLength() > 0) {
				Element elemAttach = (Element) nodeList.item(0);
				int id = Integer.parseInt(getTextAsName(elemAttach,
						"identifier"));
				String title = getTextAsName(elemAttach, "title");
				String strDate = getTextAsName(elemAttach, "date_created");
				int parentId = Integer.parseInt(getTextAsName(elemAttach,
						"relation_is_part_of"));
				String desc = getTextAsName(elemAttach, "description");

				at.setId(id);
				at.setTitle(title);
				at.setCreationDate(strDate);
				at.setPageId(parentId);
				at.setDescription(desc);
			}
		} finally {
			if (cin != null) {
				try {
					cin.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			con.disconnect();
		}
	}
}
