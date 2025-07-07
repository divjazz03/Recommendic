package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.article.enums.ArticleStatus;
import com.divjazz.recommendic.user.model.Consultant;
import io.hypersistence.utils.hibernate.type.array.LongArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Entity
@Table(name = "article")
@AllArgsConstructor
@Builder
public class Article extends Auditable {

    @Column(nullable = false)
    private String title;

    @Column(name = "subtitle", nullable = false)
    private String subtitle;
    @Column(nullable = false)
    private String content;

    @Type(LongArrayType.class)
    @Column(name = "like_ids")
    private long[] likeUserIds;
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "article",
            orphanRemoval = true)
    private Set<Comment> comments;
    @Type(StringArrayType.class)
    @Column(name = "tags")
    private String[] tags;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id")
    private Consultant consultant;

    @Column(name = "no_of_reads")
    private long numberOfReads;

    @Enumerated(EnumType.STRING)
    @Column(name = "article_status")
    private ArticleStatus status;
    @Column(name = "published_at")
    private LocalDateTime published_at;

    protected Article() {
    }

    public Article(String title, String subtitle, String content, Consultant consultant, String[] tags) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.consultant = consultant;
        this.likeUserIds = new long[]{};
        this.comments = new HashSet<>();
        this.tags = tags;
        this.numberOfReads = 0L;
        this.status = ArticleStatus.DRAFT;
    }

    public long[] getLikeUserIds() {
        if (likeUserIds == null) {
            return new long[0];
        }
        return likeUserIds;
    }


    public long incrementReads() {
        return new AtomicLong(numberOfReads).incrementAndGet();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }


}
