package jpabook.jpashop.dto.response;

import jpabook.jpashop.Entity.item.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditItemResDTO {

	private Long id;

	private String name;

	private int price;

	private int stockQuantity;

	private String author;

	private String isbn;

	// 수정
	public static EditItemResDTO of(Book book) {
		return EditItemResDTO.builder()
				.id(book.getId())
				.name(book.getName())
				.price(book.getPrice())
				.stockQuantity(book.getStockQuantity())
				.author(book.getAuthor())
				.isbn(book.getIsbn())
				.build();
	}
}
