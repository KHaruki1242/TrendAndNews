package com.example.news;

import java.time.LocalDate;
import java.time.LocalDateTime; // ログ用に追加
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    // 5秒ごとに実行するように変更
    @Scheduled(fixedRate = 5000) 
    @Transactional
    public void archiveOldNews() {
        // 現在時刻をコンソールに出して、動いているか確認
        System.out.println("★スケジューラー動作中: " + LocalDateTime.now());

        // テスト用：今日より前（昨日以前）のデータがあればアーカイブ
        LocalDate threshold = LocalDate.now(); 
        
        List<News> activeNews = repository.findByArchivedFalse();
        
        for (News news : activeNews) {
            // publishedAt が「今日より前」ならアーカイブ
            if (news.getPublishedAt().isBefore(threshold)) {
                news.setArchived(true);
                repository.save(news);
                System.out.println("【成功】アーカイブしました: " + news.getTitle());
            }
        }
    }
}