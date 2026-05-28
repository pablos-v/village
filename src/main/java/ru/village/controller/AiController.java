package ru.village.controller;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.village.controller.dto.request.ChatRequest;
import ru.village.service.ai.AiChatService;

/** REST-эндпоинт чата с AI ассистентом. Доступен USER+. */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public Map<String, String> chat(@Valid @RequestBody ChatRequest req) {
        return Map.of("response", aiChatService.chat(req.message()));
    }
}
