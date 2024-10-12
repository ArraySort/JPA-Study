package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDTO;
import study.querydsl.dto.QMemberDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@PersistenceUnit
	EntityManagerFactory emf;

	@BeforeEach
	void before() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}

	@Test
	void startJPQL() {
		// member1 찾기
		String query = "select m from Member m" +
				" where m.username = :username";

		Member findMember = em.createQuery(query, Member.class)
				.setParameter("username", "member1")
				.getSingleResult();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void startQuerydsl() {
		// JDBC prepared statement 파라미터 바인딩 적용
		Member findMember = queryFactory
				.select(member)
				.from(member)
				.where(member.username.eq("member1"))
				.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void search() {
		Member findMember = queryFactory
				.selectFrom(member)
				.where(member.username.eq("member1")
						.and(member.age.eq(10)))
				.fetchOne();

		Member findMember2 = queryFactory
				.selectFrom(member)
				.where(
						member.username.eq("member1"),
						member.age.eq(10)
				)
				.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
		assertThat(findMember2.getUsername()).isEqualTo("member1");
	}

	@Test
	void resultFetch() {
		// 리스트 조회
		List<Member> fetch = queryFactory
				.selectFrom(member)
				.fetch();

		// 단건 조회
		Member fetchOne = queryFactory
				.selectFrom(member)
				.fetchOne();

		// 단건 조회 limit(1)
		Member fetchFirst = queryFactory
				.selectFrom(member)
				.fetchFirst();

		// 페이징 쿼리 -> Deprecated 쿼리 2번 실행
		QueryResults<Member> results = queryFactory
				.selectFrom(member)
				.fetchResults();

		results.getTotal();
		results.getLimit();
		results.getOffset();

		// 페이징 쿼리 -> Deprecated
		long total = queryFactory
				.selectFrom(member)
				.fetchCount();

		System.out.println("fetch = " + fetch);
		System.out.println("fetchOne = " + fetchOne);
		System.out.println("fetchFirst = " + fetchFirst);
		System.out.println("total = " + total);
	}

	/**
	 * 회원 정렬 순서
	 * 1. 회원 나이 내림차순(desc)
	 * 2. 회원 이름 올림차순(asc)
	 * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
	 */
	@Test
	void sort() {
		em.persist(new Member(null, 100));
		em.persist(new Member("member5", 100));
		em.persist(new Member("member6", 100));

		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(100))
				.orderBy(member.age.desc(), member.username.asc().nullsLast())
				.fetch();

		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(2);

		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}

	@Test
	void paging() {
		List<Member> result = queryFactory
				.selectFrom(member)
				.orderBy(member.username.desc())
				.offset(1)
				.limit(2)
				.fetch();

		assertThat(result).hasSize(2);
	}

	@Test
	void aggregation() {
		List<Tuple> result = queryFactory
				.select(
						member.count(),
						member.age.sum(),
						member.age.avg(),
						member.age.max(),
						member.age.min()
				)
				.from(member)
				.fetch();

		// select 를 통해서 필드를 조회 할 때 tuple 을 가져온다.
		Tuple tuple = result.get(0);

		assertThat(tuple.get(member.count())).isEqualTo(4);
		assertThat(tuple.get(member.age.sum())).isEqualTo(100);
		assertThat(tuple.get(member.age.avg())).isEqualTo(25);
		assertThat(tuple.get(member.age.max())).isEqualTo(40);
		assertThat(tuple.get(member.age.min())).isEqualTo(10);
	}

	/**
	 * 팀의 이름과 각 팀의 평균 연령을 구해라.
	 */
	@Test
	void group() {
		List<Tuple> result = queryFactory
				// 팀의 이름, 회원의 나이 평균
				.select(team.name, member.age.avg())
				// member 로 부터 조회
				.from(member)
				// member 의 Team 엔티티 join
				.join(member.team, team)
				// 팀 이름으로 그룹핑
				.groupBy(team.name)
				.fetch();

		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);

		assertThat(teamA.get(team.name)).isEqualTo("teamA");
		assertThat(teamA.get(member.age.avg())).isEqualTo(15);    // (10 + 20) / 2

		assertThat(teamB.get(team.name)).isEqualTo("teamB");
		assertThat(teamB.get(member.age.avg())).isEqualTo(35);    // (30 + 40) / 35
	}

	/**
	 * 팀 A에 소속된 모든 회원
	 */
	@Test
	void join() {
		List<Member> result = queryFactory
				.selectFrom(member)
				.join(member.team, team)
				.where(team.name.eq("teamA"))
				.fetch();

		assertThat(result)
				.extracting("username")
				.containsExactly("member1", "member2");
	}

	/**
	 * 세타 조인
	 * 회원의 이름이 팀 이름과 같은 회원 조회
	 */
	@Test
	void theta_join() {
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));

		List<Member> result = queryFactory
				.select(member)
				.from(member, team)
				.where(member.username.eq(team.name))
				.fetch();

		assertThat(result)
				.extracting("username")
				.containsExactly("teamA", "teamB");
	}

	/**
	 * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA 인 팀만 조인, 회원은 모두 조회
	 * JPQL : select m, t from Member m left join m.team t on t.name = 'teamA'
	 */
	@Test
	void join_on_filtering() {
		List<Tuple> result = queryFactory
				.select(member, team)
				.from(member)
				.leftJoin(member.team, team)
				.on(team.name.eq("teamA"))
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}

	/**
	 * 연관관계 없는 엔티티 외부 조인
	 * 회원의 이름이 팀 이름과 같은 대상 외부 조인
	 */
	@Test
	void join_on_no_relation() {
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));

		List<Tuple> result = queryFactory
				.select(member, team)
				.from(member)
				.leftJoin(team).on(member.username.eq(team.name))
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}

	@Test
	void fetchJoinNo() {
		em.flush();
		em.clear();

		Member findMember = queryFactory
				.selectFrom(member)
				.where(member.username.eq("member1"))
				.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).as("패치 조인 미적용").isFalse();
	}

	@Test
	void fetchJoinUse() {
		em.flush();
		em.clear();

		Member findMember = queryFactory
				.selectFrom(member)
				.join(member.team, team).fetchJoin()
				.where(member.username.eq("member1"))
				.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).as("패치 조인 적용").isTrue();
	}

	@Test
	void subQuery() {
		// alias 지정
		QMember memberSub = new QMember("memberSub");

		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(
						// 서브쿼리
						JPAExpressions
								.select(memberSub.age.max())
								.from(memberSub)
				))
				.fetch();

		assertThat(result).extracting("age")
				.containsExactly(40);
	}

	@Test
	void subQueryIn() {
		// alias 지정
		QMember memberSub = new QMember("memberSub");

		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.in(
						// 서브쿼리
						JPAExpressions
								.select(memberSub.age)
								.from(memberSub)
								.where(memberSub.age.gt(10))
				))
				.fetch();

		assertThat(result).extracting("age")
				.containsExactly(20, 30, 40);
	}

	@Test
	void basicCase() {
		List<String> fetch = queryFactory
				.select(member.age
						.when(10).then("열살")
						.when(20).then("스무살")
						.otherwise("기타"))
				.from(member)
				.fetch();

		for (String s : fetch) {
			System.out.println("s = " + s);
		}
	}

	@Test
	void complexCase() {
		List<String> result = queryFactory
				.select(new CaseBuilder()
						.when(member.age.between(0, 20)).then("0~20살")
						.when(member.age.between((21), 30)).then("21살~30살")
						.otherwise("기타")
				)
				.from(member)
				.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	void constant() {
		List<Tuple> result = queryFactory
				.select(member.username, Expressions.constant("A"))
				.from(member)
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}

	@Test
	void concat() {
		List<String> result = queryFactory
				.select(member.username.concat("_").concat(member.age.stringValue()))
				.from(member)
				.where(member.username.eq("member1"))
				.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	void tupleProjection() {
		List<String> result = queryFactory
				.select(member.username)
				.from(member)
				.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}

		List<Tuple> result2 = queryFactory
				.select(member.username, member.age)
				.from(member)
				.fetch();

		for (Tuple tuple : result2) {
			String username = tuple.get(member.username);
			Integer age = tuple.get(member.age);

			System.out.println("username = " + username);
			System.out.println("age = " + age);
		}
	}

	@Test
	void findDTOByJPQL() {
		List<MemberDTO> result = em.createQuery("select new study.querydsl.dto.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
				.getResultList();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}
	}

	// Setter 를 이용한 방식 : DTO 에 Setter 가 필요하다.
	@Test
	void findDTOBySetter() {
		List<MemberDTO> result = queryFactory
				.select(Projections.bean(
						MemberDTO.class,
						member.username,
						member.age
				))
				.from(member)
				.fetch();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}
	}

	// 필드에 바로 넣는 방식 : DTO 에 Setter 필요 없음
	@Test
	void findDTOByField() {
		List<MemberDTO> result = queryFactory
				.select(Projections.fields(
						MemberDTO.class,
						member.username,
						member.age
				))
				.from(member)
				.fetch();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}
	}

	// 런타임 오류가 발생할 수 있음
	@Test
	void findDTOByConstructor() {
		List<MemberDTO> result = queryFactory
				.select(Projections.constructor(
						MemberDTO.class,
						member.username,
						member.age
				))
				.from(member)
				.fetch();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}
	}

	// 런타임 오류를 해결할 수 있음 / DTO 자체가 querydsl 에 의존성을 가지게 된다.
	@Test
	void findDTOByQueryProjection() {
		List<MemberDTO> result = queryFactory
				.select(new QMemberDTO(member.username, member.age))
				.from(member)
				.fetch();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}
	}

	/**
	 * 검색 조건 동적쿼리
	 * BooleanBuilder 사용
	 */
	@Test
	void 동적쿼리_BooleanBuilder() {
		String usernameParam = "member1";
		Integer ageParam = null;

		List<Member> result = searchMember1(usernameParam, ageParam);
		assertThat(result).hasSize(1);
	}

	private List<Member> searchMember1(String usernameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder();

		// 이름 조건이 null 이 아닐 때
		if (usernameCond != null) {
			builder.and(member.username.eq(usernameCond));
		}

		// 나이 조건이 null 이 아닐 때
		if (ageCond != null) {
			builder.and(member.age.eq(ageCond));
		}

		// 둘 다 조건이 null 일때? -> builder 가 실행되지 않으므로 조건이 안들어감
		return queryFactory
				.selectFrom(member)
				.where(builder)
				.fetch();
	}

	/**
	 * 검색 조건 동적쿼리
	 * 다중 where 파라미터 사용
	 * 재사용 가능
	 */
	@Test
	void 동적쿼리_WhereParam() {
		String usernameParam = "member1";
		Integer ageParam = 10;

		List<Member> result = searchMember2(usernameParam, ageParam);
		assertThat(result).hasSize(1);
	}

	private List<Member> searchMember2(String usernameCond, Integer ageCond) {
		return queryFactory
				.selectFrom(member)
				// where 안에 null 이 들어가면 조건이 무시된다.
				// .where(usernameEq(usernameCond), ageEq(ageCond))
				.where(allEq(usernameCond, ageCond))
				.fetch();
	}

	private BooleanExpression usernameEq(String usernameCond) {
		return usernameCond != null ? member.username.eq(usernameCond) : null;
	}

	private BooleanExpression ageEq(Integer ageCond) {
		return ageCond != null ? member.age.eq(ageCond) : null;
	}

	private BooleanExpression allEq(String usernameCond, Integer ageCond) {
		return usernameEq(usernameCond).and(ageEq(ageCond));
	}

	@Test
	void bulkUpdate() {
		// member1 = 10 -> 비회원
		// member2 = 20 -> 비회원
		long count = queryFactory
				.update(member)
				.set(member.username, "비회원")
				.where(member.age.lt(28))
				.execute();

		// 영속성 컨텍스트 초기화
		em.flush();
		em.clear();

		// 영속성 컨텍스트와 값이 달라진다. -> 벌크연산을 하면 DB 에 바로 반영된다.
		List<Member> result = queryFactory
				.selectFrom(member)
				.fetch();

		// 변경 전 데이터가 유지된다.
		for (Member member : result) {
			System.out.println("member = " + member);
		}

		assertThat(count).isEqualTo(2);
	}

	@Test
	void bulkAdd() {
		long count = queryFactory
				.update(member)
				.set(member.age, member.age.add(2))
				.execute();

		em.flush();
		em.clear();

		List<Member> result = queryFactory
				.selectFrom(member)
				.fetch();

		// 변경 전 데이터가 유지된다.
		for (Member member : result) {
			System.out.println("member = " + member);
		}

		assertThat(count).isEqualTo(4);
	}
}