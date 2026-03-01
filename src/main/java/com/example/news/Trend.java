package com.example.news;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 追加

import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ★これ：知らない項目や変な形式が来ても無視する
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private LocalDateTime datetime; // ここでエラーが起きがち
    private boolean archived = false;
}
