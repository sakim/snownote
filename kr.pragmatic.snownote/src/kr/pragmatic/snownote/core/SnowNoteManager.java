package kr.pragmatic.snownote.core;

import kr.pragmatic.snownote.SnowNotePlugin;
import kr.pragmatic.snownote.utils.HibernateUtil;

public class SnowNoteManager {

	/**
	 * Login 설정 값에 저장된 사용자 도메인 명과 일치하는 프로젝트를 열어서 SnowNote를 생성한다.
	 * 
	 * @return 일치하는 프로젝트가 있다면 SnowNote생성해서 반환. 일치하는 프로젝트 없는 경우 생성해서 반환.
	 * @throws Exception
	 */
	public static SnowNote createSnowNote() throws Exception {
		HibernateUtil.init(SnowNotePlugin.getWorkspacePath(),
				SnowNotePlugin.getUserDomain());

		return new SnowNote();
	}
}
