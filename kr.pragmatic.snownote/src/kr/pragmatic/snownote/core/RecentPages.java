package kr.pragmatic.snownote.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * "최근 페이지" 화면에서 사용하기 위해서 페이지를 시간에 따른 카테고리 별로 분류한 모델.
 * 
 * @author sakim
 * 
 */
public class RecentPages {
	private SnowNote fNote;

	private String[] categories = { "오늘", "어제", "지난 1주", "지난 1개월" };

	public RecentPages(SnowNote note) {
		fNote = note;
	}

	public SnowNote getSnowNote() {
		return fNote;
	}

	public String[] getCategories() {
		List<String> categoryList = new ArrayList<String>();

		// empty category는 제외
		for (String category : categories) {
			if (getPages(category).length > 0) {
				categoryList.add(category);
			}
		}

		return categoryList.toArray(new String[categoryList.size()]);
	}

	public SnowPage[] getPages(String category) {
		// 요청이 잦지 않고 시간이 계속 변경되므로 이 위치에서 초기화 중이지만 적절한 곳 옮길 필요있음.
		Calendar current = Calendar.getInstance();

		Calendar today = getBaseCalendar();
		Calendar yesterday = getBaseCalendar();
		yesterday.set(Calendar.DAY_OF_MONTH, 
				yesterday.get(Calendar.DAY_OF_MONTH) - 1);

		Calendar week = getBaseCalendar();
		week.set(Calendar.DAY_OF_MONTH, week.get(Calendar.DAY_OF_MONTH) - 7);

		Calendar month = getBaseCalendar();
		month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);

		List<SnowPage> pages = null;

		if (category.equals(categories[0])) {
			pages = fNote.getPageByDate(current.getTime(), today.getTime());
		}
		
		if (category.equals(categories[1])) {
			pages = fNote.getPageByDate(today.getTime(), yesterday.getTime());
		}

		if (category.equals(categories[2])) {
			pages = fNote.getPageByDate(yesterday.getTime(), week.getTime());
		}

		if (category.equals(categories[3])) {
			pages = fNote.getPageByDate(week.getTime(), month.getTime());
		}

		return pages != null ? pages.toArray(new SnowPage[pages.size()])
				: new SnowPage[0];
	}

	/**
	 * 어제 자정의 Calendar를 반환한다. 기본적으로 최근 페이지의 시간 비교는 자정부터.
	 * 
	 * @return
	 */
	private Calendar getBaseCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return cal;
	}
}
