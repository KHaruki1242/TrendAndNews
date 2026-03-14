package com.example.news;


import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;


@Service
public class TrendService {
    private final Tokenizer tokenizer = new Tokenizer();

    // 1. 単語と「重み（ポイント）」をセットで抽出するように変更
    public Map<String, Integer> extractKeywordsWithWeight(String text) {
        List<Token> tokens = tokenizer.tokenize(text);
        Map<String, Integer> localCounts = new HashMap<>();

        for (Token token : tokens) {
            String word = token.getSurface();
            String pos1 = token.getPartOfSpeechLevel1(); // 名詞など
            String pos2 = token.getPartOfSpeechLevel2(); // 固有名詞、一般など

            // 名詞かつ2文字以上のみ対象
            if (pos1.equals("名詞") && word.length() > 1) {
                // X風ロジック：固有名詞・人名・地域は「3点」、それ以外は「1点」
                int weight = (pos2.equals("固有名詞") || pos2.equals("人名") || pos2.equals("地域")) ? 3 : 1;
                localCounts.put(word, localCounts.getOrDefault(word, 0) + weight);
            }
        }
        return localCounts;
    }

    // 2. 集計側も「ポイントの合計」を計算するように修正
    public Map<String, Integer> getTopKeywordsFromMaps(List<Map<String, Integer>> keywordMaps) {
        Map<String, Integer> totalCounts = new HashMap<>();
        
        // 画面を見ながら、ノイズになっている単語をここに追加！
        List<String> stopWords = List.of(
        		"仙台", "Yahoo", "ニュース", "NEWS", "jp", "com", 
        	    "河北", "新聞", "新報", "オンライン", "記事", 
        	    "宮城", "東日本", "放送", "配信", "一覧",
        	    "DIG", "TBS", "khb", "秋田", "山形", "福島", "岩手", // 局名や隣県を追加
        	    "JNN", "ANN", "NNN", "FNN", "提供","東日本放送","東京","東北","東北放送"
        );

        for (Map<String, Integer> map : keywordMaps) {
            map.forEach((word, weight) -> {
                // 大文字小文字を区別せずにチェック
                if (!stopWords.stream().anyMatch(s -> s.equalsIgnoreCase(word))) {
                    totalCounts.put(word, totalCounts.getOrDefault(word, 0) + weight);
                }
            });
        }

        return totalCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}