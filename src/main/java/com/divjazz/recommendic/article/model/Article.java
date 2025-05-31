package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.article.ArticleStatus;
import com.divjazz.recommendic.user.model.Consultant;
import io.hypersistence.utils.hibernate.type.array.LongArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Entity
@Table(name = "article")
public class Article extends Auditable {

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(name = "subtitle", nullable = false)
    @NotBlank
    private String subtitle;
    @Column(nullable = false)
    @NotBlank
    private String content;

    @Type(LongArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "like_ids")
    private long[] likeUserIds;
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "article",
            orphanRemoval = true)
    private Set<Comment> comments;
    @Type(StringArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags")
    private String[] tags;
    @ManyToOne(fetch = FetchType.LAZY)
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

    public String getSubtitle() {
        return subtitle;
    }

    public LocalDateTime getPublished_at() {
        return published_at;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public long getNumberOfReads() {
        return numberOfReads;
    }

    public void setNumberOfReads(long numberOfReads) {
        this.numberOfReads = numberOfReads;
    }

    public long incrementReads() {
        return new AtomicLong(numberOfReads).incrementAndGet();
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long[] getLikeUserIds() {
        return likeUserIds;
    }

    public void setLikeUserIds(long[] likeUserIds) {
        this.likeUserIds = likeUserIds;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }


}
