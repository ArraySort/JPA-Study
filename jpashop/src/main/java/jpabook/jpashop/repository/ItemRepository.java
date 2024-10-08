package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Entity.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

	private final EntityManager em;

	// 상품 등록
	public void save(Item item) {
		if (item.getId() == null) {
			em.persist(item);
		} else {
			em.merge(item); // merge 는 모든 객체를 병합하기 때문에 위험하다.
		}
	}

	// 상품 조회(Id)
	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}

	// 상품 리스트 조회(전체)
	public List<Item> findAll() {
		return em.createQuery("select i from Item i", Item.class)
				.getResultList();
	}
}
