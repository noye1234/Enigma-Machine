package patmal.course.enigma.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patmal.course.enigma.ProcessRequestDTO.HistoryEntry;
import patmal.course.enigma.service.StatisticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enigma")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final StatisticsService statisticsService;

    public HistoryController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestParam(value = "sessionID", required = false) String sessionID,
            @RequestParam(value = "machineName", required = false) String machineName) {

        boolean hasSession = (sessionID != null && !sessionID.isEmpty());
        boolean hasMachine = (machineName != null && !machineName.isEmpty());

        if ((hasSession && hasMachine) || (!hasSession && !hasMachine)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Exactly one of sessionID or machineName must be provided"));
        }

        try {
            Map<String, List<HistoryEntry>> historyData;

            if (hasSession) {
                historyData = statisticsService.getHistoryBySession(sessionID);
            } else {
                historyData = statisticsService.getHistoryByMachine(machineName);
            }

            return ResponseEntity.ok(historyData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching history."));
        }
    }
}