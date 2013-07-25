package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowAttachment;
import kr.pragmatic.snownote.utils.FileUtil;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FilesLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private Image iconArchive = null;
	private Image iconData = null;
	private Image iconDoc = null;
	private Image iconExcel = null;
	private Image iconPdf = null;
	private Image iconPicture = null;
	private Image iconPpt = null;

	private static final String ICON_ARCHIVE = "/icons/file_archive.png";
	private static final String ICON_DATA = "/icons/file_data.png";
	private static final String ICON_DOC = "/icons/file_doc.png";
	private static final String ICON_EXCEL = "/icons/file_excel.png";
	private static final String ICON_PDF = "/icons/file_pdf.png";
	private static final String ICON_PICTURE = "/icons/file_picture.png";
	private static final String ICON_PPT = "/icons/file_ppt.png";

	public FilesLabelProvider() {
		iconArchive = SnowNotePlugin.getImageDescriptor(ICON_ARCHIVE)
				.createImage();
		iconData = SnowNotePlugin.getImageDescriptor(ICON_DATA).createImage();
		iconDoc = SnowNotePlugin.getImageDescriptor(ICON_DOC).createImage();
		iconExcel = SnowNotePlugin.getImageDescriptor(ICON_EXCEL)
				.createImage();
		iconPdf = SnowNotePlugin.getImageDescriptor(ICON_PDF).createImage();
		iconPicture = SnowNotePlugin.getImageDescriptor(ICON_PICTURE)
				.createImage();
		iconPpt = SnowNotePlugin.getImageDescriptor(ICON_PPT).createImage();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		SnowAttachment attach = (SnowAttachment) element;

		if (columnIndex == 0) {
			String extension = FileUtil.getExtension(attach.getName());

			int type = FileUtil.getType(extension);

			if (type == FileUtil.TYPE_ARCHIVE) {
				return iconArchive;
			}

			if (type == FileUtil.TYPE_DOC) {
				return iconDoc;
			}

			if (type == FileUtil.TYPE_PPT) {
				return iconPpt;
			}

			if (type == FileUtil.TYPE_EXCEL) {
				return iconExcel;
			}

			if (type == FileUtil.TYPE_PICTURE) {
				return iconPicture;
			}

			if (type == FileUtil.TYPE_PDF) {
				return iconPdf;
			}

			return iconData; // etc

		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		SnowAttachment attach = (SnowAttachment) element;

		if (columnIndex == 0) { // type
			// type은 이미지만 표시
			return null;
		}

		if (columnIndex == 1) { // name
			return attach.getName();
		}

		if (columnIndex == 2) { // size
			return Long.toString(FileUtil.bytesToKBytes(attach.getSize()))
					+ "KB";
		}

		if (columnIndex == 3) { // path
			return attach.getPath();
		}

		return null;
	}

	@Override
	public void dispose() {
		iconArchive.dispose();
		iconData.dispose();
		iconDoc.dispose();
		iconExcel.dispose();
		iconPdf.dispose();
		iconPicture.dispose();
		iconPpt.dispose();

		super.dispose();
	}
}
