package com.example.news;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final TrendRepository trendRepository;
    private final WeatherRepository weatherRepository;

    public NewsController(TrendRepository trendRepository, WeatherRepository weatherRepository) {
        this.trendRepository = trendRepository;
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        // 1ページ20件で取得
        Pageable pageable = PageRequest.of(page, 20);
        Page<Trend> trendPage = trendRepository.findAllByOrderByDatetimeDesc(pageable);

        model.addAttribute("recentNews", trendPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", trendPage.getTotalPages());
        
        // 天気リスト（weathers）を取得
        model.addAttribute("weathers", weatherRepository.findAll());
        
        return "index";
    }

 // 確実に GET と POST の両方を、このパスで受け取れるように指定
    @RequestMapping(value = "/api/trend", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String receiveTrend(
            @RequestParam(name = "keyword") String keyword, 
            @RequestParam(name = "link") String link) { 
        
        System.out.println("★データが届きました！: " + keyword);

        Trend trend = new Trend();
        trend.setKeyword(keyword);
        trend.setLink(link);
        trend.setDatetime(LocalDateTime.now());
        
        trendRepository.save(trend);
        
        return "SUCCESS"; // これを返さないとGAS側で「拒否」扱いになる
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