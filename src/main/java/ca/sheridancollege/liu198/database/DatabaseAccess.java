package ca.sheridancollege.liu198.database;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.liu198.Beans.User;
import ca.sheridancollege.liu198.Beans.Book;
import ca.sheridancollege.liu198.Beans.BookRecord;

@Repository
public class DatabaseAccess {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	// ---------------------------- User methods ----------------------------

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void addUser(String userName, String email, String password) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "INSERT INTO sec_user (userName, email, encryptedPassword, enabled) VALUES (:userName, :email, :encryptedPassword, 1)";
		namedParameters.addValue("userName", userName);
		namedParameters.addValue("email", email);
		namedParameters.addValue("encryptedPassword", passwordEncoder.encode(password));

		jdbc.update(query, namedParameters);
	}

	public void addRole(Long userId, Long roleId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO user_role (userId, roleId) VALUES (:userId, :roleId)";
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);
		jdbc.update(query, namedParameters);
	}

	// Method to find a user account by userName
	public User findUserAccount(String userName) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM sec_user where userName = :userName";
		namedParameters.addValue("userName", userName);

		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<>(User.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// Method to get User Roles for a specific User id
	public List<String> getRolesById(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "SELECT sec_role.roleName FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId AND userId = :userId";
		namedParameters.addValue("userId", userId);

		return jdbc.queryForList(query, namedParameters, String.class);

	}

	// ---------------------------- Library methods ----------------------------
	public List<Book> getLibrary() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM LIBRARY";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Book>(Book.class));
	}

	public List<Book> getLBookListById(Long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM LIBRARY WHERE id = :id";
		namedParameters.addValue("id", id);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Book>(Book.class));
	}

	public void updateLBook(Book updatedBook) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "UPDATE LIBRARY SET title = :title, author = :author, isbn = :isbn, price = :price, description = :description, totalPage = :totalPage WHERE id = :id";

		namedParameters.addValue("id", updatedBook.getId());
		namedParameters.addValue("title", updatedBook.getTitle());
		namedParameters.addValue("author", updatedBook.getAuthor());
		namedParameters.addValue("isbn", updatedBook.getIsbn());
		namedParameters.addValue("price", updatedBook.getPrice());
		namedParameters.addValue("description", updatedBook.getDescription());
		namedParameters.addValue("totalPage", updatedBook.getTotalPage());

		if (jdbc.update(query, namedParameters) > 0) {
			System.out.println("Updated Book with ID " + updatedBook.getId() + " in the database.");
		}

	}

	public void deletebLBookById(Long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM LIBRARY WHERE id = :id";
		namedParameters.addValue("id", id);

		if (jdbc.update(query, namedParameters) > 0) {
			System.out.println("Deleted book " + id + " from the database.");
		}

	}

	public void insertLBook(Book book) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		String query = "INSERT INTO LIBRARY (title, author, isbn, price, description,totalPage) VALUES(:title, :author, :isbn, :price, :description,:totalPage)";
		namedParameters.addValue("title", book.getTitle());
		namedParameters.addValue("author", book.getAuthor());
		namedParameters.addValue("isbn", book.getIsbn());
		namedParameters.addValue("price", book.getPrice());
		namedParameters.addValue("description", book.getDescription());
		namedParameters.addValue("totalPage", book.getTotalPage());

		// reset book imagePath by using bookId
		if (jdbc.update(query, namedParameters, generatedKeyHolder) > 0) {
			Long id = (Long) generatedKeyHolder.getKey();
			book.setId(id);
			namedParameters.addValue("id", id);
			namedParameters.addValue("imagePath", book.getImagePath());
			query = "UPDATE LIBRARY SET imagePath = :imagePath WHERE id = :id";
			jdbc.update(query, namedParameters);
			System.out.println("Insert book " + book.getTitle() + " to the database.");
		}
	}
	
	
	
	// ---------------------------- USER bookList methods ----------------------------

	public List<Book> getBookListByStatus(Long userId, int status) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = status == 0 ? 
				"SELECT * FROM LIBRARY WHERE LIBRARY.id NOT IN (SELECT bookId FROM bookList WHERE userId = :userId)" 
				: "SELECT * FROM LIBRARY WHERE LIBRARY.id IN (SELECT bookId FROM bookList WHERE userId = :userId AND statusId = :statusId)";
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("statusId", status);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Book>(Book.class));
	}

	public Long getBookStatus(User user, Long bookId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT statusId FROM bookList WHERE userId = :userId AND bookId = :bookId";
		namedParameters.addValue("bookId", bookId);
		namedParameters.addValue("userId", user.getUserId());
		List<Long> statusIdList = jdbc.query(query, namedParameters, new SingleColumnRowMapper<>(Long.class));

		return statusIdList.isEmpty() ? 0 :statusIdList.get(0);
	    
	}
	
	public void setBookStatus(Long bookStatus, Long userId, long bookId, int toList) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("bookId", bookId);
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("statusId", toList);
		String query = toList == 0 ? "DELETE FROM bookList WHERE userId = :userId AND bookId = :bookId" : (bookStatus == 0 ? 
				"INSERT INTO bookList (userId, bookId, statusId) VALUES (:userId, :bookId, :statusId)" 
				:  "UPDATE bookList SET statusId = :statusId WHERE userId = :userId AND bookId = :bookId");
		jdbc.update(query, namedParameters);
		
		
	}
	
	// ---------------------------- USER Record methods ----------------------------
public int getRecentBookRecord(Long userId, Long bookId) {
	MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	namedParameters.addValue("bookId", bookId);
	namedParameters.addValue("userId", userId);
	String query = "SELECT recordPage FROM RECORDS WHERE userId = :userId AND bookId = :bookId ORDER BY UPDATEDATE DESC";
			
	
		List<Integer> recordPages = jdbc.query(query, namedParameters, new SingleColumnRowMapper<>(int.class));
		return recordPages.isEmpty() ? 0:recordPages.get(0);
	
	}

public int getAnyBookRecord(Long userId, Long bookId, LocalDate setDate) {
	MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	namedParameters.addValue("bookId", bookId);
	namedParameters.addValue("userId", userId);
	namedParameters.addValue("updateDate", setDate);
	String query = "SELECT recordPage FROM RECORDS WHERE userId = :userId AND bookId = :bookId AND updateDate = :updateDate";
			
	
		List<Integer> recordPages = jdbc.query(query, namedParameters, new SingleColumnRowMapper<>(int.class));
		return recordPages.isEmpty() ? 0:recordPages.get(0);
	
	}

public void setBookRecord(Long userId, Long bookId, int recordPage, LocalDate setDate) {
	MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	namedParameters.addValue("bookId", bookId);
	namedParameters.addValue("userId", userId);
	namedParameters.addValue("recordPage", recordPage);
	namedParameters.addValue("updateDate", setDate);
	String query = getAnyBookRecord(userId, bookId, setDate) == 0 ? "INSERT INTO RECORDS(recordPage, updateDate, userId, bookId) VALUES (:recordPage, :updateDate, :userId, :bookId)" : "UPDATE RECORDS SET recordPage =  :recordPage WHERE :userId AND bookId = :bookId AND updateDate = :updateDate";
	jdbc.update(query, namedParameters);
}


	

	public List<BookRecord>  getRecordListByDate(Long userId,LocalDate setDate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("updateDate", setDate);
		namedParameters.addValue("userId", userId);
		String query = "SELECT r.bookId, l.title, r.recordPage, l.totalPage, r.updateDate FROM RECORDS AS r INNER JOIN LIBRARY AS l ON r.bookId = l.Id WHERE r.userId = :userId AND r.updateDate = :updateDate";

		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<BookRecord>(BookRecord.class));
		
	}

	

	
	

}
