package patmal.course.enigma.service;

import patmal.course.enigma.ProcessRequestDTO.LoadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import patmal.course.enigma.engine.MachineManager.MachineManager;
import patmal.course.enigma.entities.MachineEntity;
import patmal.course.enigma.entities.MachineReflectorEntity;
import patmal.course.enigma.entities.MachineRotorEntity;
import patmal.course.enigma.entities.ReflectorId;
import patmal.course.enigma.repositories.MachineRepository;
import patmal.course.enigma.engine.component.EnigmaRotor;
import patmal.course.enigma.engine.component.EnigmaReflector;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoaderService {

    private final MachineManager globalMachineManager;
    private final MachineRepository machineRepository;

    public LoaderService(MachineManager globalMachineManager, MachineRepository machineRepository) {
        this.globalMachineManager = globalMachineManager;
        this.machineRepository = machineRepository;
    }

    public LoadResponse loadMachine(MultipartFile file) {
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
            return new LoadResponse(false, null, "File must be an XML.");
        }
        try (InputStream is = file.getInputStream()) {

            List<String> message = globalMachineManager.loadXml(is);

            if (message.getFirst().startsWith("Error:")) {
                return new LoadResponse(false, null, String.join(", ", message));
            }

            String machineName = message.getFirst();

            if (!machineRepository.existsByName(machineName)) {
                MachineEntity newMachineEntity = new MachineEntity();
                newMachineEntity.setName(machineName);

                List<Character> abcChars = globalMachineManager.getMachineABC(machineName);
                StringBuilder abcString = new StringBuilder();
                for (Character ch : abcChars) {
                    abcString.append(ch);
                }
                newMachineEntity.setAbc(abcString.toString());
                List<MachineRotorEntity> rotorEntities = new ArrayList<>();
                List<MachineReflectorEntity> reflectorEntities = new ArrayList<>();





                for (EnigmaRotor rotor : globalMachineManager.getMachineRotors(machineName)) {
                    MachineRotorEntity rotorEntity = new MachineRotorEntity();
                    rotorEntity.setRotorId(rotor.getRotorId());
                    rotorEntity.setNotch(rotor.getNotch());
                    rotorEntity.setWiringLeft(rotor.getLeft());
                    rotorEntity.setWiringRight(rotor.getRight());
                    rotorEntity.setMachine(newMachineEntity);
                    rotorEntities.add(rotorEntity);
                }
                for (EnigmaReflector originalReflector : globalMachineManager.getMachineReflectors(machineName)) {

                    String originalRefId = originalReflector.getId();
                    String input = originalReflector.getInput();
                    String output = originalReflector.getOutput();
                    MachineReflectorEntity reflectorEntity = new MachineReflectorEntity(newMachineEntity, ReflectorId.valueOf(originalRefId), input, output);
                    reflectorEntities.add(reflectorEntity);
                }
                newMachineEntity.setRotorsCount(rotorEntities.size());
                newMachineEntity.setRotors(rotorEntities);
                newMachineEntity.setReflectors(reflectorEntities);

                machineRepository.save(newMachineEntity);
            }

            return new LoadResponse(true, machineName, null);

        } catch (Exception e) {
            return new LoadResponse(false, null, "Error: " + e.getMessage());
        }
    }
}