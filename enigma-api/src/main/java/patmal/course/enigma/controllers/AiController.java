package patmal.course.enigma.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patmal.course.enigma.ProcessRequestDTO.ai.AiRequestDTO;
import patmal.course.enigma.ProcessRequestDTO.ai.AiResponseDTO;
import patmal.course.enigma.service.AiService;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enigma")
@CrossOrigin(origins = "*") // חובה כדי לאפשר לדפדפן לגשת לשרת
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ai")
    public ResponseEntity<AiResponseDTO> processAiQuery(@RequestBody AiRequestDTO request) {
        System.out.println("User asked: " + request.getQuery());

         String generatedSql = aiService.generateSqlFromQuery(request.getQuery());
         String rawDbResults = aiService.executeDynamicSql(generatedSql);
         String finalAnswer = aiService.generateFinalAnswer(request.getQuery(), rawDbResults);

        AiResponseDTO response = new AiResponseDTO(finalAnswer, generatedSql);
        return ResponseEntity.ok(response);
    }
}