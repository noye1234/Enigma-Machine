package patmal.course.enigma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patmal.course.enigma.service.SessionService;

import java.util.Map;

@RestController
@RequestMapping("/enigma")
@CrossOrigin(origins = "*")
public class SessionsController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> request) {
        String machineName = request.get("machine");

        if (machineName == null || machineName.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Machine name is required"));
        }

        try {
            String sessionID = sessionService.createNewSession(machineName);

            return ResponseEntity.ok(Map.of("sessionID", sessionID));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Unknown machine name: " + machineName));
        }
    }

    @DeleteMapping("/session")
    public ResponseEntity<?> deleteSession(@RequestParam("sessionID") String sessionID) {
        boolean isDeleted = sessionService.deleteSession(sessionID);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Unknown sessionID: " + sessionID));
        }
    }
}