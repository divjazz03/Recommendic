package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "comment")
public class Comment extends Auditable {

    @Column(name = "user_id")
    private String userThatCommented;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    private Article article;
    @OneToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment comments;

    public Comment() {
    }

    public Comment(String userIdOfCommenter, Article article, Comment comments) {
        this.userThatCommented = userIdOfCommenter;
        this.article = article;
        this.comments = comments;
    }

    public void setUserThatCommented(String userIdOfCommenter) {
        this.userThatCommented = userThatCommented;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public void setComments(Comment comments) {
        this.comments = comments;
    }
}
