package com.example.news;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableScheduling
public class NewsTrendDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsTrendDashboardApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(NewsRepository newsRepo, TrendRepository trendRepo, WeatherRepository weatherRepo) {
        return (args) -> {
            // トレンドのサンプル [cite: 4]
            Trend t = new Trend();
            t.setKeyword("Java 21");
            t.setDatetime(java.time.LocalDateTime.now());
            trendRepo.save(t);

            // 天気のサンプル（1日後など） [cite: 35]
            Weather w = new Weather();
            w.setPrefecture("東京都");
            w.setCity("港区");
            w.setForecast("晴れ");
            w.setMaxTemp(18);
            w.setMinTemp(8);
            w.setDate(java.time.LocalDate.now().plusDays(1)); // 1日後 [cite: 35]
            weatherRepo.save(w);
            
            System.out.println("全データのサンプル投入が完了しました。");
        };
    }
}