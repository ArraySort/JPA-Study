package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


@Repository    // 컴포넌트 스캔의 대상이 된다. -> @Component 를 포함하고 있음(자동 빈 등록)
public class MemberRepository {

	@PersistenceContext    // 스프링 컨테이너에 있을 때 Entity Manager 를 주입해준다.
	private EntityManager em;

	// 회원 저장
	public Long save(Member member) {
		em.persist(member);
		return member.getId(); // 커멘드와 쿼리의 분리 : 사이드 이펙트를 일으키는 커멘드성이기 때문에 Id 반환
	}

	// 회원 조회
	public Member find(Long id) {
		return em.find(Member.class, id);
	}
}
