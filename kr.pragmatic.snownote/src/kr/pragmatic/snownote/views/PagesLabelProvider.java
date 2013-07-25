package kr.pragmatic.snownote.views;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PagesLabelProvider extends LabelProvider {
	private Image iconRoot = null;
	private Image iconPage = null;
	private Image iconPageCreated = null;
	private Image iconPageDeleted = null;
	private Image iconPageModified = null;
	private Image iconDate = null;

	private static final String ICON_ROOT = "/icons/treeHome.gif";
	private static final String ICON_PAGE = "/icons/page.gif";
	private static final String ICON_PAGE_CREATED = "/icons/page_added.gif";
	private static final String ICON_PAGE_DELETED = "/icons/page_deleted.gif";
	private static final String ICON_PAGE_MODIFIED = "/icons/page_modified.gif";
	private static final String ICON_DATE = "/icons/date.gif";

	public static final int ALL_PAGES_MODE = 0;
	public static final int RECENT_PAGES_MODE = 1;

	private int fMode;

	public PagesLabelProvider(int mode) {
		fMode = mode;

		iconPage = SnowNotePlugin.getImageDescriptor(ICON_PAGE).createImage();
		iconRoot = SnowNotePlugin.getImageDescriptor(ICON_ROOT).createImage();
		iconPageCreated = SnowNotePlugin
				.getImageDescriptor(ICON_PAGE_CREATED).createImage();
		iconPageDeleted = SnowNotePlugin
				.getImageDescriptor(ICON_PAGE_DELETED).createImage();
		iconPageModified = SnowNotePlugin.getImageDescriptor(
				ICON_PAGE_MODIFIED).createImage();
		iconDate = SnowNotePlugin.getImageDescriptor(ICON_DATE).createImage();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof SnowPage)
			return ((SnowPage) element).getTitle();

		if (element instanceof String)
			return (String) element;

		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof SnowPage) {
			SnowPage page = (SnowPage) element;

			// page icon priority
			// root -> deleted -> newly created -> modified -> synchronized
			if (fMode == ALL_PAGES_MODE) { // root는 모든 페이지 보기에서만 아이콘 변경
				if (page.isRoot())
					return iconRoot;
			}

			if (page.isDeleted())
				return iconPageDeleted;

			if (page.isCreated())
				return iconPageCreated;

			if (page.isModified())
				return iconPageModified;

			return iconPage;
		}

		if (element instanceof String)
			return iconDate;

		return null;
	}

	@Override
	public void dispose() {
		iconRoot.dispose();
		iconPage.dispose();
		iconPageCreated.dispose();
		iconPageDeleted.dispose();
		iconPageModified.dispose();
		iconDate.dispose();
		super.dispose();
	}
}
