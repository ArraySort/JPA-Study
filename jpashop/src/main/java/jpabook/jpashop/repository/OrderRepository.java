package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.Entity.Order;
import jpabook.jpashop.dto.request.OrderSimpleQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	private final EntityManager em;

	// 주문 생성
	public void save(Order order) {
		em.persist(order);
	}

	// 주문 조회(Id)
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}

	// 주문 검색(Order Status, Name)
	public List<Order> findAll(OrderSearch orderSearch) {
		return em.createQuery("select  o from Order o join o.member m" +
						" where o.status = :status " +
						" and m.name like :name", Order.class)
				.setParameter("status", orderSearch.getOrderStatus())
				.setParameter("name", orderSearch.getMemberName())
				.setMaxResults(1000)    // 최대 1000 건 limit
				.getResultList();
	}

	public List<Order> findAllByString(OrderSearch orderSearch) {

		String jpql = "select o from Order o join o.member m";
		boolean isFirstCondition = true;

		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " o.status = :status";
		}

		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " m.name like :name";
		}

		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
				.setMaxResults(1000);

		if (orderSearch.getOrderStatus() != null) {
			query = query.setParameter("status", orderSearch.getOrderStatus());
		}
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}

		return query.getResultList();
	}

	public List<Order> findAllByCriteria(OrderSearch orderSearch) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		Root<Order> o = cq.from(Order.class);
		Join<Object, Object> m = o.join("member", JoinType.INNER);

		List<Predicate> criteria = new ArrayList<>();

		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
			criteria.add(status);
		}
		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			Predicate name =
					cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
			criteria.add(name);
		}

		cq.where(cb.and(criteria.toArray(new Predicate[0])));
		TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
		return query.getResultList();
	}

	// 주문 조회 : 페치 조인을 통한 member, delivery 함께 조회
	public List<Order> findAllWithMemberDelivery() {
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d", Order.class
		).getResultList();
	}

	// 주문 조회 : 페치조인을 통한 다대일 관계 조회, 일대다 컬렉션 조회, 페이징 처리
	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
						"select o from Order o" +
								" join fetch o.member m" +
								" join fetch o.delivery d", Order.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}

	// 주문 조회 : DTO 로 바로 매핑
	public List<OrderSimpleQueryDTO> findOrderDTOs() {
		return em.createQuery(
						"select new jpabook.jpashop.dto.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address)" +
								" from Order o" +
								" join o.member m" +
								" join o.delivery d", OrderSimpleQueryDTO.class)
				.getResultList();
	}

	// 주문 조회 : 패치 조인을 통한 member, delivery, orderItem.item 함께 조회
	// Hibernate 6 버전 이상부터는 자동으로 distinct 가 적용된다.
	// 페이징 불가능 -> 모든 데이터를 메모리에 올려버리고 페이징을 하게 된다. -> 오류남
	public List<Order> findAllWithItem() {
		return em.createQuery(
						"select distinct o from Order o" +
								" join fetch o.member m" +
								" join fetch o.delivery d" +
								" join fetch o.orderItems oi" +
								" join fetch oi.item i", Order.class)
				.setFirstResult(1)
				.setMaxResults(100)
				.getResultList();
	}
}
