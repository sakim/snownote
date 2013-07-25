package kr.pragmatic.snownote.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowAttachment;
import kr.pragmatic.snownote.core.SnowPage;

public class NoteUtil {

	/**
	 * 페이지 이름에 충돌이 발생하면 "이름(숫자)"형식의 충돌을 피할 수 있는 이름을 만들어 준다.
	 * 
	 * @param title
	 * @return 이름의 중복을 피한 페이지 이름
	 */
	public static String conciliateTitle(String title) {
		// TODO windows 이름 규칙에서 사용되는 중복된 이름 처리 방식 적용.
		return title + "(1)";
	}

	/**
	 * 페이지 이름이 없으면 날짜를 페이지 이름으로 설정
	 * 
	 * @param title
	 *            페이지 이름
	 * @return 이름이 있으면 입력 이름 그대로 반환/입력된 이름이 없으면 "yyyy-MM-dd"형식의 날짜를 반환
	 */
	public static String conciliateEmptyTitle(String title) {
		if (title.equals("")) {
			title = currentDate();
		}
		return title;
	}

	public static String currentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}

	/**
	 * Page에 포함된 모든 Springnote 링크를 SnowNote에서 사용하기 위한 링크로 변환한다. 이미지, 파일 등의 첨부파일을
	 * SnowNote에서 사용하기 위함.
	 * 
	 * @param page
	 *            모든 Springnote링크를 SnowNote 링크로 변환하기 위한 페이지
	 * @return 모든 내부링크가 외부 링크로 치환된 페이지
	 */
	public static SnowPage externalLinksToInternal(SnowPage page) {
		String path = getUserDirPath();
		String replaced = externalLinksToInternal(page.getContents(), path);
		page.setContents(replaced);

		return page;
	}

	public static String externalLinksToInternal(String contents, String path) {
		// TODO 링크이외의 모든 패턴 매칭 텍스트를 치환한다. <img>와 <a>태그에 대해서만 처리해야 한다.
		String regex = "/pages/([0-9]*)/attachments/([0-9]*)";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(contents);

		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			m.appendReplacement(sb, path + m.group(1) + "/" + m.group(2));
		}

		m.appendTail(sb); // append remaining text

		return sb.toString();
	}

	/**
	 * Page에 포함된 모든 Snownote용 내부 링크를 다시 Springnote 서버 계정용 외부 링크로 치환한다.
	 * 
	 * @param page
	 * @return
	 */
	public static SnowPage internalLinksToExternal(SnowPage page) {
		String path = getUserDirPath();
		String replaced = internalLinksToExternal(page.getContents(), path);
		page.setContents(replaced);
		return page;
	}

	public static String internalLinksToExternal(String contents, String path) {
		// TODO 링크이외의 모든 패턴 매칭 텍스트를 치환한다. <img>와 <a>태그에 대해서만 처리해야 한다.
		String regex = path + "([0-9]*)/([0-9]*)";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(contents);

		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			m.appendReplacement(sb, "/pages/" + m.group(1) + "/attachments/"
					+ m.group(2));
		}

		m.appendTail(sb);

		return sb.toString();
	}

	/**
	 * Flickr 파일 업로드 후 내부 파일 시스템을 가르키던 경로는 Flickr 경로를 가르키도록 변경한다.
	 * 
	 * @param attachment
	 * @param path
	 */
	public static void internalLinksToFlickr(SnowAttachment attachment,
			String path) {
		SnowPage page = attachment.getPage();
		String contents = page.getContents();
		String src = attachment.getPath().replaceAll("\\\\", "\\\\\\\\");
		contents = contents.replaceAll(src, path);
		page.setContents(contents);
		page.setModified(true);
		page.setModifiedAt(new Date());
	}

	/**
	 * 해당 위치에 동일한 이름의 페이지가 있는지 여부 확인
	 * 
	 * @param parent
	 *            확인하고자 하는 위치
	 * @param title
	 *            검사하고자 하는 이름
	 * @return 이미 존재하면 참/없으면 거짓
	 */
	public static boolean hasSameTitledChild(SnowPage parent, String title) {
		// 페이지 이름 중복 검사 (이름이 없는 페이지 이름에 "날짜 (2)"식의 이름 생성 고려하지 않았음.)
		if (parent != null) { // null == root
			for (SnowPage p : parent.getChildren()) {
				if (p.getTitle().equals(title)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Source 페이지가 Target의 하위 페이지인지 여부를 재귀적으로 따라가면서 검사한다.
	 * 
	 * @param source
	 *            Source 페이지
	 * @param target
	 *            Taregt 페이지
	 * @return Source가 Target의 하위이면 true/아니면 false
	 */
	public static boolean isChild(SnowPage source, SnowPage target) {
		List<SnowPage> children = source.getChildren();
		for (SnowPage page : children) {
			if (page.equals(target))
				return true;
			if (isChild(page, target))
				return true;
		}

		return false;
	}

	public static String getUserDirPath() {
		return SnowNotePlugin.getWorkspacePath() + "/"
				+ SnowNotePlugin.getUserDomain() + "/";
	}

	public static String conciliateOpenId(String openId) {
		if (openId.indexOf("http://") == -1) {
			openId = "http://" + openId;
		}
		return openId;
	}
}