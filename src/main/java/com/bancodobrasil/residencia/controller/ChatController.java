package com.bancodobrasil.residencia.controller;

import com.bancodobrasil.residencia.dto.response.ChatResponseDTO;
import com.bancodobrasil.residencia.model.ChatRequest;
import com.bancodobrasil.residencia.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final AiService aiService;

    public ChatController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> enviarPrompt(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiService.estimate(request));
    }
}
