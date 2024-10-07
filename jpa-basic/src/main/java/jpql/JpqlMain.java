package jpql;

import jakarta.persistence.*;

import java.util.List;

public class JpqlMain {

	public static void main(String[] args) {

		// Persistence Unit Name 설정
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		// 트랜잭션 시작
		tx.begin();

		try {
			JMember JMember = new JMember();
			JMember.setUsername("member1");
			JMember.setAge(10);
			em.persist(JMember);

			// 반환 타입이 명확할 때 : TypedQuery
			TypedQuery<JMember> selectMember = em.createQuery("select m from JMember m where m.username = :username", JMember.class);
			TypedQuery<String> selectUsername = em.createQuery("select m.username from JMember m", String.class);

			// 반환 타입이 명확하지 않을 때 : Query
			Query selectUsernameAndAge = em.createQuery("select m.username, m.age from JMember m");

			// 파라미터 바인딩 결과 확인
			selectMember.setParameter("username", "member1");
			JMember singleResult = selectMember.getSingleResult();
			System.out.println("singleResult = " + singleResult);

			List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from JMember m", MemberDTO.class)
					.getResultList();

			MemberDTO memberDTO = result.get(0);
			System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
			System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

			//language=JPAQL
			String concatQuery = "select concat('a', 'b') from JMember m";
			//language=JPAQL
			String subStringQuery = "select substring(m.username, 2, 3 ) from JMember m";
			//language=JPAQL
			String loacteQuery = "select locate('de', 'abcde') from JMember m";
			//language=JPAQL
			String sizeQuery = "select size(t.JMembers) from JTeam t";

			List<String> queryResult = em.createQuery(concatQuery, String.class)
					.getResultList();

			for (String s : queryResult) {
				System.out.println("s = " + s);
			}

			JTeam teamA = new JTeam();
			teamA.setName("팀A");
			em.persist(teamA);

			JTeam teamB = new JTeam();
			teamB.setName("팀B");
			em.persist(teamB);

			JMember jMember1 = new JMember();
			jMember1.setUsername("회원1");
			jMember1.setJTeam(teamA);
			em.persist(jMember1);

			JMember jMember2 = new JMember();
			jMember2.setUsername("회원2");
			jMember2.setJTeam(teamA);
			em.persist(jMember2);

			JMember jMember3 = new JMember();
			jMember3.setUsername("회원3");
			jMember3.setJTeam(teamB);
			em.persist(jMember3);

			em.flush();
			em.clear();

			//language=JPAQL
			String fetchJoinQuery = "select m from JMember m join fetch m.JTeam";

			List<JMember> fetchJoinQueryResult = em.createQuery(fetchJoinQuery, JMember.class)
					.getResultList();

			for (JMember jMember : fetchJoinQueryResult) {
				System.out.println("JMember = " + jMember.getUsername() + ", " + jMember.getJTeam().getName());
				// 회원1, 팀A(SQL, DB 조회)
				// 회원2, 팀A(영속성 컨텍스트, 1차캐시)
				// 회원3, 팀B(SQL, DB 조회)

				// 회원 100명 -> N + 1 문제 발생(최악의 경우 팀이 다 다를 때 100번 조회)
			}

			List<JMember> resultList = em.createNamedQuery("JMember.findByUsername", JMember.class)
					.setParameter("username", "회원1")
					.getResultList();

			for (JMember jMember : resultList) {
				System.out.println("jMember = " + jMember);
			}

			// 트랜잭션 커밋
			tx.commit();
		} catch (Exception e) {
			// 트랜잭션 롤백
			tx.rollback();
		} finally {
			em.close();
		}

		emf.close();
	}
}
