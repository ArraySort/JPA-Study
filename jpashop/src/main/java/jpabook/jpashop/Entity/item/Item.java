package jpabook.jpashop.Entity.item;

import jakarta.persistence.*;
import jpabook.jpashop.Entity.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
// 상속 테이블 전략 설정 : SINGLE_TABLE -> 한 테이블에 모든 정보
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;

	private int price;

	private int stockQuantity;

	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();

	// 재고 증가
	public void addStock(int stockQuantity) {
		this.stockQuantity += stockQuantity;
	}

	// 재고 감소
	public void removeStock(int stockQuantity) {
		int restStock = this.stockQuantity - stockQuantity;
		if (restStock < 0) {
			throw new NotEnoughStockException("Need more Stock");
		}
		this.stockQuantity = restStock;
	}
}
