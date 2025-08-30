package edu.centraluniversity.app.model.database;

import edu.centraluniversity.app.model.CoworkingDto;
import edu.centraluniversity.app.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coworkings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coworking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleRequired;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Integer occupancy;

    public Coworking(CoworkingDto coworkingDto) {
        this.floor = coworkingDto.getFloor();
        this.label = coworkingDto.getLabel();
        this.occupancy = coworkingDto.getOccupancy();
        this.roleRequired = coworkingDto.getRoleRequired();
    }
}
