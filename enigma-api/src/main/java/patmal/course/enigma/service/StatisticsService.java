package patmal.course.enigma.service;

import org.springframework.stereotype.Service;
import patmal.course.enigma.ProcessRequestDTO.HistoryEntry;
import patmal.course.enigma.entities.MachineEntity;
import patmal.course.enigma.entities.ProcessingEntity;
import patmal.course.enigma.repositories.ProcessingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final ProcessingRepository processingRepository;

    public StatisticsService(ProcessingRepository processingRepository) {
        this.processingRepository = processingRepository;
    }

    public Map<String, List<HistoryEntry>> getHistoryBySession(String sessionId) {
        List<ProcessingEntity> entities = processingRepository.findBySessionId(sessionId);
        return groupHistoryByCode(entities);
    }

    public Map<String, List<HistoryEntry>> getHistoryByMachine(String machineName) {
        List<ProcessingEntity> entities = processingRepository.findByMachine_Name(machineName); // ודא שזה תואם לשם הפונקציה בריפוזיטורי שלך
        return groupHistoryByCode(entities);
    }

    private Map<String, List<HistoryEntry>> groupHistoryByCode(List<ProcessingEntity> entities) {
        Map<String, List<HistoryEntry>> historyMap = new HashMap<>();


        for (ProcessingEntity entity : entities) {
            MachineEntity machine = entity.getMachine();
            String codeKey = entity.getCode();

            HistoryEntry entry = new HistoryEntry(
                    entity.getInput(),
                    entity.getOutput(),
                    entity.getDuration()
            );

            historyMap.computeIfAbsent(codeKey, k -> new ArrayList<>()).add(entry);
        }

        return historyMap;
    }
}