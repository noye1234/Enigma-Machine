package patmal.course.enigma.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "machines")
public class MachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "rotors_count")
    private Integer rotorsCount;

    @Column(name = "abc")
    private String abc;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MachineRotorEntity> rotors;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MachineReflectorEntity> reflectors;


    public MachineEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRotorsCount() { return rotorsCount; }
    public void setRotorsCount(Integer rotorsCount) { this.rotorsCount = rotorsCount; }
    public String getAbc() { return abc; }
    public void setAbc(String abc) { this.abc = abc; }
    public List<MachineRotorEntity> getRotors() { return rotors; }
    public void setRotors(List<MachineRotorEntity> rotors) { this.rotors = rotors; }
    public List<MachineReflectorEntity> getReflectors() { return reflectors; }
    public void setReflectors(List<MachineReflectorEntity> reflectors) { this.reflectors = reflectors; }
}