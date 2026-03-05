package patmal.course.enigma.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "machines_rotors")
public class MachineRotorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "rotor_id")
    private Integer rotorId;

    @Column(name = "notch") // nullable ב-DB
    private Integer notch;

    @Column(name = "wiring_right")
    private String wiringRight;

    @Column(name = "wiring_left")
    private String wiringLeft;

    public MachineRotorEntity() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MachineEntity getMachine() {
        return machine;
    }

    public void setMachine(MachineEntity machine) {
        this.machine = machine;
    }

    public Integer getRotorId() {
        return rotorId;
    }

    public void setRotorId(Integer rotorId) {
        this.rotorId = rotorId;
    }

    public Integer getNotch() {
        return notch;
    }

    public void setNotch(Integer notch) {
        this.notch = notch;
    }

    public void setWiringRight(String wiringRight) {
        this.wiringRight = wiringRight;
    }

    public void setWiringLeft(String wiringLeft) {
        this.wiringLeft = wiringLeft;
    }


}