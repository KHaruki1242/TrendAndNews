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
        
        // 2. トレンド解析（全データから重み付きで集計）
        List<Trend> allTrends = trendRepository.findAll();
        List<Map<String, Integer>> allKeywordMaps = new ArrayList<>();
        
        for (Trend t : allTrends) {
            // 文字列ではなく「単語 -> ポイント」のMapをリストに追加していく
            allKeywordMaps.add(trendService.extractKeywordsWithWeight(t.getKeyword()));
        }
        
        // 3. HTMLへ渡す
        model.addAttribute("recentNews", trendPage.getContent());
        model.addAttribute("currentPage", trendPage.getNumber());
        model.addAttribute("totalPages", trendPage.getTotalPages());
        
        // メソッド名を getTopKeywordsFromMaps に変更
        model.addAttribute("topKeywords", trendService.getTopKeywordsFromMaps(allKeywordMaps));
        model.addAttribute("weathers", weatherRepository.findAll());
        
        return "index";
    }
    
    @GetMapping("/api/trend")
    @ResponseBody
    public String addTrend(@RequestParam String keyword, @RequestParam String link) {
        Trend trend = new Trend();
        trend.setKeyword(keyword);
        trend.setLink(link);
        trend.setDatetime(LocalDateTime.now());
        trendRepository.save(trend);

        // トレンド分析も新しいロジックに合わせる
        List<Trend> recentTrends = trendRepository.findTop100ByOrderByDatetimeDesc();
        List<Map<String, Integer>> keywordMaps = new ArrayList<>();
        for (Trend t : recentTrends) {
            keywordMaps.add(trendService.extractKeywordsWithWeight(t.getKeyword()));
        }

        Map<String, Integer> top5 = trendService.getTopKeywordsFromMaps(keywordMaps);
        System.out.println("🔥 X風トレンドTOP5（重み付き）: " + top5);

        return "SUCCESS";
    }

    // --- 以下、weather と delete は変更なしでOK ---
    @PostMapping("/api/weather")
    public String updateWeather(@RequestBody Weather weather) {
        weatherRepository.save(weather);
        return "SUCCESS";
    }
    
    @PostMapping("/api/trend/delete/{id}")
    @ResponseBody
    public String deleteTrend(@PathVariable("id") Long id) {
        try {
            trendRepository.deleteById(id);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR";
        }
    }
}