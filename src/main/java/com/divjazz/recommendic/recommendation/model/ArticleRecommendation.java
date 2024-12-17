package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "article_recommendation")
public class ArticleRecommendation extends Auditable {

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    protected ArticleRecommendation() {
    }

    public ArticleRecommendation(Patient patient, Article article) {
        this.patient = patient;
        this.article = article;
    }
}
