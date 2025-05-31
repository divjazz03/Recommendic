package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "comment")
public class Comment extends Auditable {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userThatCommented;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    private Article article;
    @OneToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment comments;

    public Comment() {
    }

    public Comment(User userThatCommented, Article article, Comment comments) {
        this.userThatCommented = userThatCommented;
        this.article = article;
        this.comments = comments;
    }

    public User getUserThatCommented() {
        return userThatCommented;
    }

    public void setUserThatCommented(User userThatCommented) {
        this.userThatCommented = userThatCommented;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Comment getComments() {
        return comments;
    }

    public void setComments(Comment comments) {
        this.comments = comments;
    }
}
