package patmal.course.enigma.service;

import org.springframework.stereotype.Service;
import patmal.course.enigma.ProcessRequestDTO.ProcessResponse;
import patmal.course.enigma.entities.ProcessingEntity;
import patmal.course.enigma.repositories.MachineRepository;
import patmal.course.enigma.repositories.ProcessingRepository;

import java.util.List;

@Service
public class ProcessService {

    private final SessionService sessionService;
    private final MachineRepository machineRepository;
    private final ProcessingRepository processingRepository;

    public ProcessService(SessionService sessionService,
                          MachineRepository machineRepository,
                          ProcessingRepository processingRepository) {
        this.sessionService = sessionService;
        this.machineRepository = machineRepository;
        this.processingRepository = processingRepository;
    }

    public ProcessResponse processMessage(String input, String sessionId) {
        ActiveSessionState sessionState = sessionService.getSession(sessionId);

        if (sessionState == null) {
            throw new RuntimeException("Session not found or expired.");
        }

        long startTime = System.nanoTime();

        List<String> outputMessageList = sessionState.getEngine().process(input);
        if (outputMessageList.isEmpty()) {
            throw new RuntimeException("Machine configuration or code is not initialized.");
        }
        String outputMessage = outputMessageList.getFirst();
        String code = sessionState.getEngine().getCurrentCode();
        String currentRotorsPositionCompact= sessionState.getEngine().getCurrentRotorsPositionCompact('-');

        long endTime = System.nanoTime();
        long durationNano = endTime - startTime;

        String machineName = sessionState.getMachineName();
        ProcessingEntity processingEntity = new ProcessingEntity(
                machineRepository.findByName(machineName),
                sessionId,
                code,
                input,
                outputMessage,
                durationNano
        );

        processingRepository.save(processingEntity);

        return new ProcessResponse(outputMessage, currentRotorsPositionCompact);
    }
}