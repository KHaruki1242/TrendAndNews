package com.example.news;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewsController {

    private final NewsRepository repository;

    // コンストラクタ注入（推奨される書き方です）
    public NewsController(NewsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        // パワポの①にあたる「最近のニュース」を取得 
        model.addAttribute("recentNews", repository.findByArchivedFalse());
        
        // パワポの④⑤にあたる「過去のニュース」を取得 
        model.addAttribute("oldNews", repository.findByArchivedTrue());
        
        return "index"; // src/main/resources/templates/index.html を探す
    }
}