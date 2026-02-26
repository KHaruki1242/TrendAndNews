package com.example.news;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data // LombokでGetter/Setterを自動生成
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // ニュースの表題 [cite: 35]
    private String url;            // URL [cite: 35]
    private LocalDate publishedAt; // 取得・公開日 [cite: 35]
    
    // 最近(false)か過去(true)かを判定するフラグ 
    private boolean archived = false; 
}