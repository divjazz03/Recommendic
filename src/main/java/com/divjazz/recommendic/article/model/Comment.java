package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.global.Auditable;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment extends Auditable {
    @Column(name = "comment_id", updatable = false)
    private final String commentId = "CMT-" + UlidCreator.getMonotonicUlid();
    @Column(name = "user_id")
    private String userThatCommented;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    private Article article;
    @OneToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment comments;
}
