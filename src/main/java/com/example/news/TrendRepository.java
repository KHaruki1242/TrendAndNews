package com.example.news;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
    
	
    // 1. 日付の新しい順に全件取得する（これがダッシュボードのメインになります）
    List<Trend> findAllByOrderByDatetimeDesc();
    
    // ページ指定（20件ずつなど）で日付順に取得する
    Page<Trend> findAllByOrderByDatetimeDesc(Pageable pageable);
    
    List<Trend> findTop100ByOrderByDatetimeDesc();
    
    List<Trend> findByDatetimeAfter(LocalDateTime dateTime);
    
    
}


 