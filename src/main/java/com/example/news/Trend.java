package com.example.news;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動で 1, 2, 3... と増える
    private Long id;

    private String keyword; // タイトル

    @Column(length = 1000) // 長いURLも入るように
    private String link;   // URL

    private LocalDateTime datetime;

    // 以下、Getter / Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }
}