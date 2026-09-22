package com.bancodobrasil.residencia.controller;

import com.bancodobrasil.residencia.model.ChatRequest;
import com.bancodobrasil.residencia.dto.ChatCustomResponseDTO;
import com.bancodobrasil.residencia.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AiService aiService;

    @PostMapping
    public ResponseEntity<ChatCustomResponseDTO> enviarPrompt(@RequestBody ChatRequest request) {
        ChatCustomResponseDTO resposta = aiService.callChatGpt(request);
        return ResponseEntity.ok(resposta);
    }
}
