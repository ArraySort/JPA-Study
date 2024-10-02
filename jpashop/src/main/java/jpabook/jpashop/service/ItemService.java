package jpabook.jpashop.service;

import jpabook.jpashop.Entity.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;

	// 상품 등록
	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}

	// 상품 리스트 조회(전체)
	@Transactional(readOnly = true)
	public List<Item> findItems() {
		return itemRepository.findAll();
	}

	// 상품 조회(Id)
	@Transactional(readOnly = true)
	public Item findOne(Long itemId) {
		return itemRepository.findOne(itemId);
	}
}
