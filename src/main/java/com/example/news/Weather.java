package com.example.news;

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

    private String prefecture;
    private String city;
    private String forecast;
    
    // ★ 型を String に変更することで、GASからの送信エラーを防ぎます
    private String maxTemp; 
    private String minTemp;
    private String date; 
    
    private String weatherCode; 
}