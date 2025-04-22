package com.ndson03.quanlykhoahoc.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndson03.quanlykhoahoc.service.QnAService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/gemini")
public class GeminiController {

    @GetMapping("/qna")
    public String qnaPage() {
        return "gemini/qna";
    }


    private final QnAService qnAService;

    public GeminiController(QnAService qnAService) {
        this.qnAService = qnAService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> payload) {
        // Lấy câu hỏi từ payload
        String question = payload.get("question");

        // Giả sử bạn đã nhận được câu trả lời từ API (dạng JSON string)
        String answer = qnAService.getAnswer(question);

        // Parse JSON để lấy phần text
        JsonObject jsonObject = JsonParser.parseString(answer).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");

        // Lấy phần text từ câu trả lời
        String text = candidates.get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        return ResponseEntity.ok(text); // Trả về phần text
    }
}
