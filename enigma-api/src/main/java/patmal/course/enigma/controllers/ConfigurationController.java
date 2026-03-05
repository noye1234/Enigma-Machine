package patmal.course.enigma.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patmal.course.enigma.ProcessRequestDTO.ConfigStatusResponse;
import patmal.course.enigma.service.MachineDataService;
import patmal.course.enigma.service.CodeService;
import patmal.course.enigma.ProcessRequestDTO.ManualCodeRequest;

import java.util.Map;

@RestController
@RequestMapping("/enigma/config")
public class ConfigurationController {

    private final CodeService codeService;
    private final MachineDataService machineDataService;

    public ConfigurationController(CodeService codeService, MachineDataService machineDataService) {
        this.codeService = codeService;
        this.machineDataService = machineDataService;
    }


    @GetMapping
    public ResponseEntity<?> getMachineStatus(
            @RequestParam("sessionID") String sessionID,
            @RequestParam(value = "verbose", defaultValue = "false") boolean verbose) {
        try {
            ConfigStatusResponse response = machineDataService.getMachineConfigStatus(sessionID, verbose);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetCode(@RequestParam("sessionID") String sessionID) {
        try {
            String msg=codeService.restartCode(sessionID);

            return ResponseEntity.ok(msg);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @PutMapping("/automatic")
    public ResponseEntity<String> automaticConfig(@RequestParam("sessionID") String sessionID) {
        try {
            codeService.codeAutomatic(sessionID);

            return ResponseEntity.ok("Automatic code setup completed successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/manual")
    public ResponseEntity<String> manualConfig(@RequestBody ManualCodeRequest request) {
        try {
            codeService.codeManually(request);

            return ResponseEntity.ok("Manual code set successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
