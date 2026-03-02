package com.example.news;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        // ① 最近のニュース [cite: 35]
        model.addAttribute("recentNews", newsRepository.findByArchivedFalse());
        // ④ 過去のニュース [cite: 36]
        model.addAttribute("oldNews", newsRepository.findByArchivedTrue());
        
        // 最近のトレンド [cite: 4]
        model.addAttribute("recentTrends", trendRepository.findByArchivedFalse());
        // ⑤ 過去のトレンド [cite: 36]
        model.addAttribute("oldTrends", trendRepository.findByArchivedTrue());
        
        // ③ 天気予報 [cite: 35]
        model.addAttribute("weathers", weatherRepository.findAll());
        
        return "index";
    }
    
    @RequestMapping(value = "/api/trend", method = {RequestMethod.GET, RequestMethod.POST}) // GETもPOSTも許可
    @ResponseBody
    public String receiveTrend(@RequestParam(value = "keyword", required = false) String keyword) {
        // 1. 届いたデータを表示
        System.out.println("★受信成功！キーワード: " + keyword);
        
        Trend trend = new Trend();
        trend.setKeyword(keyword != null ? keyword : "テスト受信");
        trend.setDatetime(java.time.LocalDateTime.now());
        
        // 2. 保存
        trendRepository.save(trend);
        
        return "SUCCESS: Java received -> " + keyword;
    }
    
    // 天気データ受け取り用 API
    @PostMapping("/api/weather")
    @ResponseBody
    public String receiveWeather(@RequestBody Weather weather) {
        // パワポ要件③：都道府県すべての天気取得（1日後、3日後、1週間後） [cite: 35]
        weatherRepository.save(weather);
        return "Success: Saved Weather for " + weather.getCity();
    }
}