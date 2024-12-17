package com.divjazz.recommendic.article.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "article")
public class Article extends Auditable {

    public Article(String title, String content, Consultant consultant) {
        this.title = title;
        this.content = content;
        this.consultant = consultant;
    }

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    protected Article() {}


}
