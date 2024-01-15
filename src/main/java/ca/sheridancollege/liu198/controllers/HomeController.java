package ca.sheridancollege.liu198.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.liu198.Beans.Book;
import ca.sheridancollege.liu198.Beans.MonthlyCalendar;
import ca.sheridancollege.liu198.Beans.User;
import ca.sheridancollege.liu198.database.DatabaseAccess;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	@Lazy
	private DatabaseAccess da;
	private boolean recordFlag = false;

	// --------------public pages--------------------
	// A page that everyone could see a list of books as a Home page of the book
	// store
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("setDate", LocalDate.now());
		model.addAttribute("calendarSundays", new MonthlyCalendar().getCalendarSundays());
		model.addAttribute("library", da.getLibrary());
		return "index";
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();
			return da.findUserAccount(username);
		} else
			return null;
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}

	@GetMapping("/register")
	public String getRegister() {
		return "register";
	}

	@PostMapping("/register")
	public String postRegister(Model model, @RequestParam String username, @RequestParam String email,
			@RequestParam String password) {
		da.addUser(username, email, password);
		User user = da.findUserAccount(username);
		Long userId = user.getUserId();
		da.addRole(userId, Long.valueOf(2));// this system only allow new user register as “ROLE_USER”
		model.addAttribute("user", user);

		return "login";

	}

	// A page for each book in library that displays its details.
	@GetMapping("/showDetailById/{bookId}")
	public String showDetailById(Model model, @PathVariable Long bookId) {
		Book book = da.getLBookListById(bookId).get(0);
		
		model.addAttribute("book", book);
		User user = getCurrentUser();
		model.addAttribute("bookStatus", user == null ? 0 : da.getBookStatus(user, bookId));
		recordFlag = false;
		model.addAttribute("recordFlag",recordFlag);
		model.addAttribute("currentPage",user == null ? 0 : da.getRecentBookRecord(user.getUserId(), bookId));
		return "secure/bookDetail";

	}

	// -------------- admin pages--------------------

	// Edit/delete book data
	@GetMapping("/admin/editDetailById/{id}")
	public String editBookById(Model model, @PathVariable Long id) {
		Book book = da.getLBookListById(id).get(0);
		model.addAttribute("book", book);
		model.addAttribute("library", da.getLibrary());
		return "secure/admin/bookEdit";

	}

	@GetMapping("/admin/deleteBookById/{id}")
	public String delete(Model model, @PathVariable Long id) {
		Book book = da.getLBookListById(id).get(0);
		da.deletebLBookById(id);
		model.addAttribute("book", book);
		model.addAttribute("library", da.getLibrary());
		model.addAttribute("successMessage", "The book [" + book.getTitle() + "] is DELETE successfully!");
		return "index";
	}

	// admin could go to Add new book data page
	@GetMapping("/admin/addBook")
	public String addBook(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute("library", da.getLibrary());
		return "secure/admin/bookEdit";
	}

	// submit a new book OR update book
	@PostMapping("/admin/insertBook")
	public String insertBook(Model model, @ModelAttribute Book book) {
		List<Book> existingBooks = da.getLBookListById(book.getId());
		String successMessage;
		if (existingBooks.isEmpty()) {
			// If the Book doesn't exist (based on ID), insert a new Book
			da.insertLBook(book);
			successMessage = "The new book [" + book.getTitle() + "] is added successfully!";

		} else {
			// If the Book exists, update the existing Book
			da.updateLBook(book);
			successMessage = "Book details of [" + book.getTitle() + "] updated successfully!";
		}

		model.addAttribute("successMessage", successMessage);
		model.addAttribute("book", book);
		model.addAttribute("library", da.getLibrary());
		return "secure/admin/bookEdit";
	}

	// -------------- client pages--------------------
	@GetMapping("/client/addToList/{toList}-{bookId}-{bookStatus}")
	public String addBooktoList(Model model, @PathVariable int toList, @PathVariable Long bookId, @PathVariable Long bookStatus,
			@ModelAttribute Book book) {

		Long userId = getCurrentUser().getUserId();
		da.setBookStatus(bookStatus, userId, bookId, toList);
		if (toList ==3) {
			model.addAttribute("book", da.getLBookListById(bookId).get(0));
			model.addAttribute("bookStatus", 3);
			recordFlag = true;
			model.addAttribute("recordFlag",recordFlag);
			model.addAttribute("currentPage",da.getRecentBookRecord(userId, bookId));
			return "secure/bookDetail";
		}
		else {	
			model.addAttribute("setDate", LocalDate.now());
			model.addAttribute("calendarSundays", new MonthlyCalendar().getCalendarSundays());
			model.addAttribute("library", da.getLibrary());
			return "index";
		}
	
		
	}
	
	@PostMapping("/client/recordReading")
	public String recordReading(Model model, @RequestParam Long bookId, @RequestParam int currentPage) {
		LocalDate setDate = LocalDate.now();
		User user = getCurrentUser();
		Long userId = user.getUserId();
		Book book = da.getLBookListById(bookId).get(0);
		da.setBookRecord(userId, bookId, currentPage,setDate);
		da.setBookStatus(da.getBookStatus(user, bookId), bookId, userId, currentPage == book.getTotalPage() ? 4 : 3);
		
		model.addAttribute("bookStatus", da.getBookStatus(user, bookId));
		model.addAttribute("book", book);
		recordFlag = true;
		model.addAttribute("recordFlag",recordFlag);
		model.addAttribute("currentPage",da.getRecentBookRecord(userId, bookId));
		model.addAttribute("successMessage", "Your reading record is updated!");
		return "secure/bookDetail";
	}
	
	
	

		@GetMapping("/client/booklist")
		public String getBookList(Model model) {
			Long userId = getCurrentUser().getUserId();
			
		    List<Integer> statusIds = Arrays.asList(0, 1, 2, 3, 4, 5, 6); 
		    
		    List<String> statusNames = Arrays.asList(
		    		"More Books In Libray",
		    		"Plan to Read in the Future",
		            "Want to Read This Month",
		            "Currently Reading This Month",
		            "Finished This Month",
		            "Finished Before",
		            "Give Up"
		        );

		        
		    List<List<Book>> bookListByStatus = new ArrayList<List<Book>>();   
		    for (Integer statusId : statusIds) {
		        List<Book> bookList =da.getBookListByStatus(userId, statusId);
		        bookListByStatus.add(bookList);
		    }

		    model.addAttribute("statusIds", statusIds);
		    model.addAttribute("statusNames", statusNames);
		    model.addAttribute("bookListByStatus", bookListByStatus);

			return "secure/client/bookList";
		}
		

	@GetMapping("/client/{year}-{month}-{day}")
	public String dateRecord(Model model, @PathVariable int year, @PathVariable  int month,  @PathVariable int day) {
		Long userId = getCurrentUser().getUserId();
		LocalDate setDate = LocalDate.of(year, month, day);
		model.addAttribute("setDate", setDate);
		model.addAttribute("bookListByDate", da.getRecordListByDate(userId, setDate));
		return "secure/client/dateRecord";
	}

	@PostMapping("/client/updateCart")
	public String updateCart(@RequestParam("quantities") List<Integer> quantities, HttpSession session, Model model) {
		@SuppressWarnings("unchecked")
		Map<Book, Integer> cart = (Map<Book, Integer>) session.getAttribute("cart");
		if (cart != null) {
			int i = 0;
			Iterator<Map.Entry<Book, Integer>> iterator = cart.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Book, Integer> entry = iterator.next();
				Book book = entry.getKey();
				Integer quantity = quantities.get(i);

				if (book != null) {
					if (quantity <= 0) {
						iterator.remove();
					} else {
						cart.put(book, quantity);
					}
				}
				i++;
			}
			session.setAttribute("cart", cart);

		}
		model.addAttribute("successMessage", "Your shoppingcart is updated!");

		return "secure/client/shoppingcart";
	}

}
