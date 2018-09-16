SELECT TOP 10 b.book_id, title, author, quantity, CASE WHEN rating is NULL THEN 0 ELSE rating END rating
FROM 
(SELECT * FROM Books
WHERE title LIKE '%title%' OR author LIKE '%author%') b LEFT JOIN
(SELECT r.book_id, AVG(rating) rating FROM Ratings r GROUP BY r.book_id) r
ON b.book_id = r.book_id
ORDER BY rating DESC;