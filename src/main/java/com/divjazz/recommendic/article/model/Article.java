package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "article")
public class Article extends Auditable {

    @Column(nullable = false)
    @NotBlank
    private String title;
    @Column(nullable = false)
    @NotBlank
    private String content;
    @ManyToOne
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;
    public Article(String title, String content, Consultant consultant) {
        this.title = title;
        this.content = content;
        this.consultant = consultant;
    }

    protected Article() {
    }


}
