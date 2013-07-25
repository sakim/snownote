package kr.pragmatic.snownote.utils;


public class FileUtil {
	public static final int TYPE_PICTURE = 0;
	public static final int TYPE_ARCHIVE = 1;
	public static final int TYPE_PDF = 2;
	public static final int TYPE_DOC = 3;
	public static final int TYPE_PPT = 4;
	public static final int TYPE_EXCEL = 5;
	public static final int TYPE_DATA = 6;
	
	public static int getTypeByName(String name) {
		String extension = getExtension(name);
		return getType(extension);
	}

	/**
	 * 파일의 확장자로 파일의 종류를 구별한다.
	 * 
	 * @param extension
	 *            확장자
	 * @return 확장자에 따른 타입 상수를 반환한다. 알려지지 않은 확장자는 모두 TYPE_DATA를 반환한다.
	 */
	public static int getType(String extension) {
		if (extension.equalsIgnoreCase("zip")
				|| extension.equalsIgnoreCase("rar")
				|| extension.equalsIgnoreCase("tar")
				|| extension.equalsIgnoreCase("gz")
				|| extension.equalsIgnoreCase("Z")
				|| extension.equalsIgnoreCase("alz")) {
			return TYPE_ARCHIVE;
		}

		if (extension.equalsIgnoreCase("doc")
				|| extension.equalsIgnoreCase("docx")) {
			return TYPE_DOC;
		}

		if (extension.equalsIgnoreCase("ppt")
				|| extension.equalsIgnoreCase("pptx")) {
			return TYPE_PPT;
		}

		if (extension.equalsIgnoreCase("xls")
				|| extension.equalsIgnoreCase("xlsx")
				|| extension.equalsIgnoreCase("csv")) {
			return TYPE_EXCEL;
		}

		if (extension.equalsIgnoreCase("jpg")
				|| extension.equalsIgnoreCase("png")
				|| extension.equalsIgnoreCase("gif")
				|| extension.equalsIgnoreCase("bmp")) {
			return TYPE_PICTURE;
		}

		if (extension.equalsIgnoreCase("pdf")) {
			return TYPE_PDF;
		}

		return TYPE_DATA;
	}

	/**
	 * 전달받은 파일명의 확장자를 반환한다. 서로간의 정렬을 지원하기 위해서 확장자가 없는 파일은 ""를 반환하고, .파일(hidden
	 * file)이면 ".파일명"을 반환한다.
	 * 
	 * @param name
	 *            확장자를 포함한 파일명 (경로는 제외)
	 * @return 파일의 확장자
	 */
	public static String getExtension(String name) {
		if (name.startsWith("."))
			return name;

		String[] tokens = name.split("\\."); // regex

		if (tokens.length == 1)
			return "";

		return tokens[tokens.length - 1];
	}

	/**
	 * Byte를 KByte로 환산해서 반환한다. 소수점 이하는 올림.
	 * 
	 * @param bytes
	 * @return 소수점 이하 올림 처리된 KBytes 사이즈
	 */
	public static long bytesToKBytes(long bytes) {

		long kBytes = bytes / 1024;

		long remainder = bytes % 1024;

		if (remainder > 0)
			kBytes += 1;

		return kBytes;
	}
}
