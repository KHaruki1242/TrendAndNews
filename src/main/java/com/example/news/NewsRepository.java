package com.example.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    // 最近のニュース（archivedがfalseのもの）を取得 
    List<News> findByArchivedFalse();
    
    // 過去のニュース（archivedがtrueのもの）を取得 
    List<News> findByArchivedTrue();
}