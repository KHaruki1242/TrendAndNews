package com.example.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
    // 最近のトレンド（archivedがfalse）を取得
    List<Trend> findByArchivedFalse();
    
    // パワポ要件⑤「過去のトレンド」を取得
    List<Trend> findByArchivedTrue();
    
    // 追加：作成日時の新しい順に全件取得する
    List<Trend> findAllByOrderByDatetimeDesc();
}