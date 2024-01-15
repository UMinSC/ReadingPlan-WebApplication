package ca.sheridancollege.liu198.Beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor

public class Book {
	private long id;
	@NonNull
	private String title;
	@NonNull
	private String author;
	@NonNull
	private Long isbn;
	private double price;
	private String description;
	private int totalPage;
	private String imagePath;
	private final String PrePATH = "images/bookCover/";
	
	public void setId(long id) {
		this.id = id;
		this.imagePath = PrePATH + id + ".png";
	}
	

}
