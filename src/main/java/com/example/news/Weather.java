package com.example.news;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prefecture;   // 都道府県 
    private String city;         // 市区町村 [cite: 9]
    private String forecast;     // 天気（晴れ、雨など） [cite: 9]
    private Integer maxTemp;     // 最高気温 [cite: 9]
    private Integer minTemp;     // 最低気温 [cite: 9]
    private LocalDate date;      // 予報日（1日後、3日後、1週間後など） 
}