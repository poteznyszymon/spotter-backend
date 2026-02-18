package com.example.spotter.repository;

import com.example.spotter.model.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = {"avatar", "office"})
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAllByOffice_Id(Long id);

    boolean existsUserEntityByEmail(String email);
    boolean existsUserEntityByUsername(String username);
}
