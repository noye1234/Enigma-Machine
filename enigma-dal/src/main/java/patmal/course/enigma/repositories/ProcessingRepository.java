package patmal.course.enigma.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import patmal.course.enigma.entities.ProcessingEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface ProcessingRepository extends JpaRepository<ProcessingEntity, UUID> {
    List<ProcessingEntity> findBySessionId(String sessionId);
    List<ProcessingEntity> findByMachine_Name(String machineName);
}