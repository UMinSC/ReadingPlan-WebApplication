-- user table---------------------------
INSERT INTO sec_user (userName, email, encryptedPassword, enabled) VALUES 
('admin','admin@example.com', '$2a$10$1ltibqiyyBJMJQ4hqM7f0OusP6np/IHshkYc4TjedwHnwwNChQZCy', 1),
('booklover','booklover@example.com', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

 
INSERT INTO sec_role (roleName) VALUES 
('ROLE_ADMIN'),
('ROLE_CLIENT');
 

 
INSERT INTO user_role (userId, roleId) VALUES 
(1, 1),
(2, 2);

-- LIBRARY table ---------------------------
INSERT INTO LIBRARY(title, author, isbn, price, description,totalPage,imagePath) VALUES 
	('The Great Gatsby', 'F. Scott Fitzgerald', 9780743273565, 12.99, 'Classic American novel.',233,'images/bookCover/1.png'),
    ('To Kill a Mockingbird', 'Harper Lee', 9780061120084, 10.95, 'Pulitzer Prize-winning novel.', 186,'images/bookCover/2.png'),
    ('1984', 'George Orwell', 9780451524935, 9.99, 'Dystopian science fiction.', 352,'images/bookCover/3.png'),
    ('Brave New World', 'Aldous Huxley', 9780060850524, 8.99, 'Dystopian novel.', 288, 'images/bookCover/4.png'),
    ('The Catcher in the Rye', 'J.D. Salinger', 9780316769488, 11.49, 'Coming-of-age novel.', 288, 'images/bookCover/5.png'),
    ('Moby-Dick', 'Herman Melville', 9780142437247, 14.99, 'Novel by Herman Melville.', 704, 'images/bookCover/6.png'),
    ('Pride and Prejudice', 'Jane Austen', 9780141439518, 7.99, 'Romantic novel by Jane Austen.', 432, 'images/bookCover/7.png'),
    ('The Hobbit', 'J.R.R. Tolkien', 9780547928227, 11.99, 'Fantasy novel.', 320, 'images/bookCover/8.png'),
    ('Lord of the Flies', 'William Golding', 9780571056866, 10.99, 'Novel by William Golding.', 224, 'images/bookCover/9.png'),
    ('To the Lighthouse', 'Virginia Woolf', 9780156907392, 9.49, 'Modernist novel.', 209, 'images/bookCover/10.png'),
    ('The Picture of Dorian Gray', 'Oscar Wilde', 9780141439570, 8.49, 'Philosophical novel by Oscar Wilde.', 304, 'images/bookCover/11.png'),
    ('The Adventures of Tom Sawyer', 'Mark Twain', 9780486400778, 6.99, 'Novel by Mark Twain.', 272, 'images/bookCover/12.png');
    
    
-- bookList table  --------------------------- 
INSERT INTO BookStatus (statusName) VALUES 
('Plan to Read in the Future'),
('Want to Read This Month'),
('ing_ThisMonth'),
('Finish_ThisMonth'),
('Finished_Before'),
('GiveUp');


INSERT INTO  bookList (userId, bookId, statusId) VALUES 
(2,1,2),(2,2,1),(2,3,2),(2,4,3),(2,5,4),(2,6,5),(2,7,6);

-- RECORDS table  --------------------------- 
INSERT INTO  RECORDS (userId, bookId, recordPage, updateDate)  VALUES
(2,4,100,'2023-12-06'),(2,5,80,'2023-12-02'),(2,5,200,'2023-12-03'),(2,4,30,'2023-11-15'),(2,4,44,'2023-12-02'), (2,5,288,'2023-12-04'),(2,6,704,'2023-11-04'),(2,7,200,'2023-12-02');
