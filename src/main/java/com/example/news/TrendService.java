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
    // 解析器の準備
    private final Tokenizer tokenizer = new Tokenizer();

    public List<String> extractKeywords(String text) {
        // 文章をトークン（単語単位）に分解
        List<Token> tokens = tokenizer.tokenize(text);
        
        return tokens.stream()
            // 「名詞」だけを抽出
            .filter(token -> token.getPartOfSpeechLevel1().equals("名詞"))
            // 2文字以上の単語に絞る（「の」「は」や1文字のノイズを除去）
            .filter(token -> token.getSurface().length() > 1)
            // 単語の文字列だけを取り出す
            .map(Token::getSurface)
            .collect(Collectors.toList());
    }
    
 // クラスの中に追加
    public Map<String, Integer> getTopKeywords(List<String> allWords) {
        Map<String, Integer> counts = new HashMap<>();
        
        // 単語を数える
        for (String word : allWords) {
            // 「仙台」「ニュース」「Yahoo」などは多すぎるので除外（ノイズカット）
            if (word.equals("仙台") || word.equals("Yahoo") || word.equals("ニュース")) continue;
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }

        // 数が多い順に並び替えて、上位5つだけを返す
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}