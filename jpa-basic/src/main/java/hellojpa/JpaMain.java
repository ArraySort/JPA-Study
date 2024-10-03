package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain {

	public static void main(String[] args) {

		// Persistence Unit Name 설정
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		// 트랜잭션 시작
		tx.begin();

		try {
			Member findMember = em.find(Member.class, 1L);
			findMember.setName("수정된 JPA");
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
