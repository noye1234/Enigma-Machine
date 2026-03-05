package patmal.course.enigma.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "processing")
public class ProcessingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "code")
    private String code;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "time")
    private Long time; // bigint ns

    public ProcessingEntity() {}
    public ProcessingEntity(MachineEntity machine, String sessionId, String code, String input, String output, Long time) {
        this.machine = machine;
        this.sessionId = sessionId;
        this.code = code;
        this.input = input;
        this.output = output;
        this.time = time;
    }
    public MachineEntity getMachine() {
        return machine;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public long getDuration() {
        return time;
    }

    public String getCode() {
        return code;
    }


}