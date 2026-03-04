package com.example.news;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NewsController {

    private final NewsRepository newsRepository;
    private final TrendRepository trendRepository;
    private final WeatherRepository weatherRepository;

    public NewsController(NewsRepository newsRepository, TrendRepository trendRepository, WeatherRepository weatherRepository) {
        this.newsRepository = newsRepository;
        this.trendRepository = trendRepository;
        this.weatherRepository = weatherRepository;
    }
    
    @GetMapping("/")
    public String index(Model model) {
        // ① 最近・過去のニュース
        model.addAttribute("recentNews", newsRepository.findByArchivedFalse());
        model.addAttribute("oldNews", newsRepository.findByArchivedTrue());
        
        // ★修正点：トレンドを「日付が新しい順」に全件取得して渡す
        // HTML側で index < 5 を使って最新5件を切り分けるため、全件渡します
        model.addAttribute("trends", trendRepository.findAllByOrderByDatetimeDesc());
        
        // 既存の取得方法も残しておきたい場合はこちら（必要に応じて使い分けてください）
        model.addAttribute("recentTrends", trendRepository.findByArchivedFalse());
        model.addAttribute("oldTrends", trendRepository.findByArchivedTrue());
        
        // ③ 天気予報
        model.addAttribute("weathers", weatherRepository.findAll());
        
        return "index";
    }
    
    @RequestMapping(value = "/api/trend", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String receiveTrend(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "link", required = false) String link) { 
        
        // 1. 重複チェック（ここを追加！）
        if (keyword != null && trendRepository.existsByKeyword(keyword)) {
            System.out.println("⚠️ 既に保存済みのためスキップ: " + keyword);
            return "SKIP: Already exists";
        }
        
        // 2. 新規保存（重複がない場合のみ実行される）
        System.out.println("★新規受信成功！ キーワード: " + keyword);
        Trend trend = new Trend();
        trend.setKeyword(keyword != null ? keyword : "タイトルなし");
        trend.setLink(link); 
        trend.setDatetime(java.time.LocalDateTime.now());
        
        trendRepository.save(trend);
        return "SUCCESS";
    }
    
    @PostMapping("/api/trend/delete/{id}")
    @ResponseBody
    public String deleteTrend(@PathVariable Long id) {
        trendRepository.deleteById(id);
        return "DELETED";
    }
    
    // ...以下、weather API等はそのまま
    // 天気データ受け取り用 API
    @PostMapping("/api/weather")
    @ResponseBody
    public String receiveWeather(@RequestBody Weather weather) {
        // パワポ要件③：都道府県すべての天気取得（1日後、3日後、1週間後） [cite: 35]
        weatherRepository.save(weather);
        return "Success: Saved Weather for " + weather.getCity();
    }
}