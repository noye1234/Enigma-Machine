package patmal.course.enigma.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patmal.course.enigma.ProcessRequestDTO.ProcessResponse;
import patmal.course.enigma.service.ProcessService;

@RestController
@RequestMapping("/enigma")
@CrossOrigin(origins = "*")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processMessage(@RequestParam("input") String input,
                                            @RequestParam("sessionId") String sessionId) {
        try {
            ProcessResponse response = processService.processMessage(input, sessionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}