package com.example.news;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;      // トレンドワード 
    private LocalDateTime datetime; // 取得日時 
    
    // ニュースと同様、過去分かどうかのフラグ [cite: 36]
    private boolean archived = false; 
}