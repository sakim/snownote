package kr.pragmatic.snownote.actions;

import kr.pragmatic.snownote.core.SnowPage;

import org.eclipse.ui.IActionFilter;

/**
 * Page의 Context menu의 활성화/비활성화 상태를 결정한다.
 * 
 * @author sakim
 * 
 */
public class SnowPageActionFilter implements IActionFilter {
	private static final String IS_DELETED = "isDeleted";
	private static final String CONTAINS_DELETED = "containsDeleted";
	private static final String ALL_DELETED = "allDeleted";

	public boolean testAttribute(Object target, String name, String value) {
		SnowPage page = ((SnowPage) target);

		Boolean expected = Boolean.valueOf(value);

		// 현재 Popup 대상이 deleted 상태인지 여부 검사
		// 삭제하기, 삭제 취소하기 액션의 상태 결정
		if (name.equals(IS_DELETED)) {
			if (expected == page.isDeleted() && !page.isRoot()) {
				return true;
			}
		}

		// 현재 Popup 대상이 deleted 상태를 포함하는 하위 페이지를 가지고 있는지 검사
		// 모두 삭제 취소하기 액션의 상태 결정
		if (name.equals(CONTAINS_DELETED)) {
			// 하위 페이지 없는 경우 기본 삭제/삭제 취소 메뉴와 중복됨으로 불필요
			if (page.getChildren().size() == 0) {
				return false;
			}

			if (expected == containsDeleted(page) && !page.isRoot()) {
				return true;
			}
		}

		// 모든 Popup 대상과 모든 하위 페이지가 deleted 상태인지를 검사
		// 모두 deleted 상태인 경우 모두 삭제 액션을 보여주지 않음
		if (name.equals(ALL_DELETED)) {
			if (page.getChildren().size() == 0) {
				return false;
			}

			if (expected == allDeleted(page) && !page.isRoot()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 현재 페이지와 하위 페이지 중에서 삭제 대상인 페이지를 포함하는지 여부를 검사
	 * 
	 * @param page
	 * @return
	 */
	private boolean containsDeleted(SnowPage page) {
		boolean value = false;

		for (SnowPage child : page.getChildren()) {
			if (child.isDeleted())
				return true;
			value = containsDeleted(child);

			if (value)
				break;
		}

		return value;
	}

	private boolean allDeleted(SnowPage page) {
		boolean value = true;

		for (SnowPage child : page.getChildren()) {
			if (!child.isDeleted())
				return false;
			value = allDeleted(child);

			if (!value)
				break;
		}

		return value;
	}
}
