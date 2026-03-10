package com.example.news; // ここは自分のパッケージ名に合わせてください

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class NewsController {

    private final TrendRepository trendRepository;
    private final WeatherRepository weatherRepository;
    
    @Autowired
    private TrendService trendService;

    public NewsController(TrendRepository trendRepository, WeatherRepository weatherRepository) {
        this.trendRepository = trendRepository;
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/")
    public String index(@PageableDefault(size = 20, sort = "datetime", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        // 1. ページ指定されたニュースを取得
        Page<Trend> trendPage = trendRepository.findAll(pageable);
        
        // 2. トレンド解析用（これは全データから上位を取る）
        List<Trend> allTrends = trendRepository.findAll();
        List<String> allWords = new ArrayList<>();
        for (Trend t : allTrends) {
            allWords.addAll(trendService.extractKeywords(t.getKeyword()));
        }
        
        // 3. HTMLへ渡す
        model.addAttribute("recentNews", trendPage.getContent()); // 20件分
        model.addAttribute("currentPage", trendPage.getNumber()); // 今何ページ目か
        model.addAttribute("totalPages", trendPage.getTotalPages()); // 全部で何ページか
        model.addAttribute("topKeywords", trendService.getTopKeywords(allWords));
        model.addAttribute("weathers", weatherRepository.findAll());
        
        return "index";
    }
    
 // 確実に GET と POST の両方を、このパスで受け取れるように指定
    @GetMapping("/api/trend")
    @ResponseBody
    public String addTrend(@RequestParam String keyword, @RequestParam String link) {
        // 1. まずは保存（既存の処理）
        Trend trend = new Trend();
        trend.setKeyword(keyword);
        trend.setLink(link);
        trend.setDatetime(LocalDateTime.now());
        trendRepository.save(trend);

        // 2. 最新のニュース100件くらいからトレンドを分析する
        List<Trend> recentTrends = trendRepository.findTop100ByOrderByDatetimeDesc();
        List<String> allWords = new ArrayList<>();
        for (Trend t : recentTrends) {
            allWords.addAll(trendService.extractKeywords(t.getKeyword()));
        }

        // 3. 集計してログに出してみる
        Map<String, Integer> top5 = trendService.getTopKeywords(allWords);
        System.out.println("🔥 今のトレンドTOP5: " + top5);

        return "SUCCESS";
    }

    @PostMapping("/api/weather")
    public String updateWeather(@RequestBody Weather weather) {
        // 同じ日付のデータが既にあれば削除して、常に最新にする
        //weatherRepository.deleteByDate(weather.getDate()); 
        weatherRepository.save(weather);
        return "SUCCESS";
    }
    
 // ニュース削除用のAPI
    @PostMapping("/api/trend/delete/{id}")
    @ResponseBody
    public String deleteTrend(@PathVariable("id") Long id) {
        try {
            trendRepository.deleteById(id);
            System.out.println("★削除成功 ID: " + id);
            return "SUCCESS";
        } catch (Exception e) {
            System.err.println("★削除失敗: " + e.getMessage());
            return "ERROR";
        }
    }
}