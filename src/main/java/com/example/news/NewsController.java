package com.example.news; // ここは自分のパッケージ名に合わせてください

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        // 全件取得してページ分割（本来はPageableを使いますが、まずは表示優先で！）
        List<Trend> allTrends = trendRepository.findAll(Sort.by(Sort.Direction.DESC, "datetime"));
        
        // トレンド集計用（最新100件）
        List<Trend> forAnalysis = allTrends.stream().limit(100).toList();
        List<String> allWords = new ArrayList<>();
        for (Trend t : forAnalysis) {
            allWords.addAll(trendService.extractKeywords(t.getKeyword()));
        }
        
        // HTMLへ渡す
        model.addAttribute("topKeywords", trendService.getTopKeywords(allWords));
        model.addAttribute("recentNews", allTrends); // HTMLの${recentNews}に合わせる
        model.addAttribute("weathers", weatherRepository.findAll());
        
        // ページネーション用の値（とりあえずエラー回避用）
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        
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