package jpabook.jpashop.Entity.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.jpashop.dto.AddItemReqDTO;
import jpabook.jpashop.dto.EditItemReqDTO;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends Item {

	private String author;

	private String isbn;

	// 생성
	public static Book createOf(AddItemReqDTO dto) {
		Book book = new Book();
		book.setName(dto.getName());
		book.setPrice(dto.getPrice());
		book.setStockQuantity(dto.getStockQuantity());
		book.setAuthor(dto.getAuthor());
		book.setIsbn(dto.getIsbn());
		return book;
	}


	// 수정
	public static Book editOf(EditItemReqDTO dto) {
		Book book = new Book();
		book.setId(dto.getId());
		book.setName(dto.getName());
		book.setPrice(dto.getPrice());
		book.setStockQuantity(dto.getStockQuantity());
		book.setAuthor(dto.getAuthor());
		book.setIsbn(dto.getIsbn());
		return book;
	}
}
