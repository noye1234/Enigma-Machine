package patmal.course.enigma.entities;

import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity
@Table(name = "machines_reflectors")
public class MachineReflectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "reflector_id", columnDefinition = "reflector_id_enum")
    private ReflectorId reflectorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;


    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    public MachineReflectorEntity() {}

    public MachineReflectorEntity(MachineEntity machine, ReflectorId reflectorId, String input, String output) {
        this.machine = machine;
        this.reflectorId = reflectorId;
        this.input = input;
        this.output = output;
    }
    public ReflectorId getReflectorId() {
        return reflectorId;
    }

    public void setReflectorId(ReflectorId reflectorId) {
        this.reflectorId = reflectorId;
    }

}