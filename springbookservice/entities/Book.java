package com.spring_web_book.springbookservice.entities;


import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity(name = "books")
@NamedEntityGraph(
        name = "with-userBookState",
        attributeNodes = {
                @NamedAttributeNode("userBookState"),
                @NamedAttributeNode("genres"),
                @NamedAttributeNode("publisher")
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "release_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp releaseDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "genres_id")
    @Column(nullable = false)
    private Set<Genre> genres = new HashSet<>();

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> genreIds = new ArrayList<>();

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long publisherId;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<UserBookState> userBookState = new ArrayList<>();

    private Long likeCount = 0L;

    public Book(String title, String description, Timestamp releaseDate) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public static final List<String> sortByList = new ArrayList<>();

    {
        sortByList.add("releaseDate");
        sortByList.add("price");
    }

    public Book() {
    }

    public Book(Long id, String title, String description, Double price, Timestamp releaseDate,
                Publisher publisher) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.releaseDate = releaseDate;
        this.publisher = publisher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void incrementLikeCount() {
        if (this.likeCount != null) {
            this.setLikeCount(++likeCount);
        } else {
            this.setLikeCount(1L);
        }
    }

    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount != 0) {
            this.setLikeCount(--likeCount);
        }
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public UserBookState getUserState() {
        return userBookState.isEmpty() ? null : userBookState.get(0);
    }

    public void setUserState(UserBookState userBookState) {
        if (this.userBookState.isEmpty()) {
            this.userBookState.add(userBookState);
        } else {
            this.userBookState.set(0, userBookState);
        }
    }

}
