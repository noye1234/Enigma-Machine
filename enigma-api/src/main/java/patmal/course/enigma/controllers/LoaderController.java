package patmal.course.enigma.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import patmal.course.enigma.service.LoaderService;
import patmal.course.enigma.ProcessRequestDTO.LoadResponse;

@RestController
@RequestMapping("/enigma")
public class LoaderController {

    private final LoaderService loaderService;

    public LoaderController(LoaderService loaderService) {
        this.loaderService = loaderService;
    }

    @PostMapping(value = "/load", consumes = "multipart/form-data")
    public ResponseEntity<LoadResponse> loadMachine(@RequestParam("file") MultipartFile file) {
        LoadResponse response = loaderService.loadMachine(file);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}