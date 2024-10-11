package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.HelloEntity;
import study.querydsl.entity.QHelloEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	@Transactional
	void contextLoads() {

		HelloEntity hello = new HelloEntity();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHelloEntity qHello = new QHelloEntity("Hello");

		HelloEntity result = query
				.selectFrom(qHello)
				.fetchOne();

		assertThat(result).isEqualTo(hello);
		assertThat(result.getId()).isEqualTo(hello.getId());
	}
}
