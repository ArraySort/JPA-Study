package jpabook.jpashop.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditItemReqDTO {

	private Long id;

	private String name;

	private int price;

	private int stockQuantity;

	private String author;

	private String isbn;

}
