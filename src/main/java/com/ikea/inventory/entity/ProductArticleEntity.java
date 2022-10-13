package com.ikea.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "product_articles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ArticleEntity article;
    private Integer amount;
}
