package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
