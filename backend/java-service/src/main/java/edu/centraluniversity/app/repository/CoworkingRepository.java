package edu.centraluniversity.app.repository;

import edu.centraluniversity.app.model.Role;
import edu.centraluniversity.app.model.database.Coworking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoworkingRepository extends JpaRepository<Coworking, Long> {

    List<Coworking> findAllByFloor(int floor);

    List<Coworking> findAllByRoleRequired(Role roleRequired);

    Optional<Coworking> findByLabelContaining(String label);
}
