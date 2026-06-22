package EComm_App.repository;

import EComm_App.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    @Query("SELECT p FROM products p WHERE p.active = true AND p.stockQuantity > 0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
}


/*
 ⏺️ findByActiveTrue -- its not a prebuild method like save(),findAll()

How JPA "Reads" the Method Name:
Spring Data JPA parses your method name like a sentence, breaking it into keywords, and builds SQL from those keywords.
It's not magic guessing — it's strict parsing rules. Spring matches words in your method name against your entity's field names

        findByActiveTrue()
Breaks down like this:
    find          -> SELECT
    By            -> WHERE (start of condition)
    Active        -> column name "active" (matches your field: private Boolean active)
    True          -> condition: = true

Generated SQL:
    SELECT * FROM products WHERE active = true;


⏺️ @Query — Custom Query
This is for when the derived method name approach becomes too complex or impossible.

    SELECT p FROM products p
    WHERE p.active = true
        AND p.stockQuantity > 0
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
>>>>>>>>
SELECT p FROM products p
-- "p" is an alias for Product entity (NOT the SQL table directly — this is JPQL, not raw SQL)

WHERE p.active = true
-- only active products

AND p.stockQuantity > 0
-- only products in stock

AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
-- case-insensitive partial match on product name
-- LOWER(...) makes both sides lowercase so "Phone" matches "phone" search
-- CONCAT('%', :keyword, '%') builds the pattern: %keyword%


JPQL -> Jakarta Persistence Query Language
This queries the Entity (Product class and its fields), NOT the raw database table directly.
    JPQL uses:           Entity class name & field names → "Product", "p.stockQuantity"
    Raw SQL would use:   actual table/column names       → "products", "stock_quantity"

⏺️ :keyword is a named parameter placeholder in the query. @Param("keyword") binds your Java method parameter to that placeholder.
 */