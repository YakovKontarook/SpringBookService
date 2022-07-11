package com.spring_web_book.springbookservice.repositories;

import com.spring_web_book.springbookservice.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.spring_web_book.springbookservice.entities.Book_.genres;

interface CriteriaBookRepository {
    List<Book> findAllFiltered(Double maxPrice, String searchQuery, Long publisherId,
                               List<Long> genreIds, Long userId, Long bookId, Pageable pageable);

    long countPages(Double maxPrice, String searchQuery, Long publisherId,
                    List<Long> genreIds, Long userId, Long bookId, Pageable pageable);

}

public interface BookRepository extends JpaRepository<Book, Long>, CriteriaBookRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE books b SET b.likeCount = b.likeCount + :count WHERE b.id = :id")
    void addLikeCount(
            @Param("id") Long id,
            @Param("count") Long count);

    @Query("SELECT b FROM books b LEFT JOIN b.userBookState ubs ON ubs.user.id = :userId WHERE b.id = :id")
    @EntityGraph("with-userBookState")
    Optional<Book> findWithUserState(
            @Param("id") Long id,
            @Param("userId") Long userId);

    @Query("SELECT b.likeCount FROM books b WHERE b.id = :id")
    Optional<Long> getLikeCount(@Param("id") Long id);
}

class CriteriaBookRepositoryImpl implements CriteriaBookRepository {

    @Autowired
    private EntityManager em;

    public long countPages(Double maxPrice, String searchQuery, Long publisherId,
                           List<Long> genreIds, Long userId, Long bookId, Pageable pageable
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Book> book = q.from(Book.class);
        Predicate[] predicates = getPredicates(maxPrice, searchQuery, publisherId, genreIds, userId, bookId, cb, book);
        q.select(cb.count(book)).where(predicates);
        long bookCount = em.createQuery(q).getSingleResult();
        return Math.round((double) bookCount / pageable.getPageSize());
    }

    @Override
    public List<Book> findAllFiltered(Double maxPrice, String searchQuery, Long publisherId,
                                      List<Long> genreIds, Long userId, Long bookId, Pageable pageable
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> q = cb.createQuery(Book.class);
        Root<Book> book = q.from(Book.class);

        Join<Book, UserBookState> bookState = book.join(Book_.userBookState, JoinType.LEFT);
        bookState.on(cb.equal(bookState.get(UserBookState_.user), userId));

        Predicate[] predicates = getPredicates(maxPrice, searchQuery, publisherId, genreIds, userId, bookId, cb, book);
        q.select(book).where(predicates);

        if (pageable != null) {
            q.orderBy(QueryUtils.toOrders(pageable.getSort(), book, cb));
        }

        return em.createQuery(q).
                setFirstResult(pageable != null ? (int) pageable.getOffset() : 0).
                setMaxResults(pageable != null ? pageable.getPageSize() : 1).
                setHint("javax.persistence.loadgraph", em.getEntityGraph("with-userBookState")).
                getResultList();
    }

    public static Predicate[] getPredicates(Double maxPrice, String searchQuery, Long publisherId,
                                            List<Long> genreIds, Long userId, Long bookId,
                                            CriteriaBuilder cb, Root<Book> book
    ) {
        List<Predicate> predicates = new ArrayList<>();
        if (maxPrice != null) {
            predicates.add(cb.lt(book.get(Book_.price), maxPrice));
        }
        if (publisherId != null) {
            predicates.add(cb.equal(book.get(Book_.publisher), publisherId));
        }
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String searchQueryLike = "%" + searchQuery + "%";
            predicates.add(cb.or(
                    cb.like(book.get(Book_.title), searchQueryLike),
                    cb.like(book.get(Book_.description), searchQueryLike)
            ));
        }
        if (genreIds != null && !genreIds.isEmpty()) {
            CriteriaQuery<Book> criteriaQuery =
                    cb.createQuery(Book.class);
            Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
            Root<Book> subBook = subquery.from(Book.class);
            SetJoin<Book, Genre> bookGenres = subBook.join(genres);
            subquery.select(subBook.get(Book_.id))
                    .distinct(true)
                    .where(bookGenres.get(Genre_.id).in(genreIds));

            predicates.add(
                    cb.in(book.get(Book_.id)).value(subquery));
        }

        if (bookId != null) {
            predicates.add(cb.equal(book.get(Book_.id), bookId));
        }

        return predicates.toArray(new Predicate[0]);
    }
}

