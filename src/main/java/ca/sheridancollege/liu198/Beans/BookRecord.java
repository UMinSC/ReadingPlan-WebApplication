package ca.sheridancollege.liu198.Beans;

import java.time.LocalDate;

import lombok.Data;

@Data

public class BookRecord {
	private long bookId;
	private String title;
	private int recordPage;
	private int totalPage;
	private LocalDate updateDate;

}
