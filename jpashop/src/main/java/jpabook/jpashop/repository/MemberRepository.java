package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository Bean 으로 등록 -> Component 내장, 컴포넌트 스캔 대상 지정
@Repository
@RequiredArgsConstructor
public class MemberRepository {

	// EntityManager 주입
	private final EntityManager em;

	// 회원 저장(Member)
	public void save(Member member) {
		em.persist(member);
	}

	// 회원 조회(ID)
	public Member findOne(Long id) {
		return em.find(Member.class, id);
	}

	// 회원 리스트 조회(전체, JPQL 사용)
	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}

	// 회원 조회(name, JPQL 사용, 파라미터)
	public List<Member> findByName(String name) {
		return em.createQuery("select  m from Member m where m.name = :name", Member.class)
				.setParameter("name", name)
				.getResultList();
	}
}
