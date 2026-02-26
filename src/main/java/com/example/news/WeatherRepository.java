package com.example.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    // パワポ要件③「都道府県すべての天気取得」に対応するため、場所で検索できるようにします
    List<Weather> findByPrefectureAndCityOrderByDateAsc(String prefecture, String city);
}