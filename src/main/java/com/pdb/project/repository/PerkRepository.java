package com.pdb.project.repository;

import com.pdb.project.model.EPerk;
import com.pdb.project.model.Perk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerkRepository extends JpaRepository<Perk, Long> {
    Optional<Perk> findByTitle(EPerk title);
}
