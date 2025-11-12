package eu.dickovadev.pojisteniapp.repositories;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.userId = :userId")
    Optional<UserEntity> findByIdWithRoles(@Param("userId") long userId);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Page<UserEntity> findByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId")
    Page<UserEntity> findByUserId(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.firstName IS NULL OR u.lastName IS NULL")
    Page<UserEntity> findUsersWithNullFirstNameOrLastName(Pageable pageable);

    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r = :role")
    Page<UserEntity> findAdminUsers(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE NOT EXISTS (SELECT r FROM u.roles r WHERE r = 'ROLE_ADMIN')")
    List<UserEntity> findUsersExcludingAdmins();

    @Query("""
                SELECT u FROM UserEntity u 
                WHERE NOT EXISTS (
                    SELECT r FROM u.roles r WHERE r = 'ROLE_ADMIN'
                ) 
                AND u.firstName IS NOT NULL 
                AND u.lastName IS NOT NULL
            """)
    Page<UserEntity> findNonAdminUsersWithValidNames(Pageable pageable);


    //Statistics
    @Query("SELECT COUNT(u) FROM UserEntity u")
    Long countTotalUsers();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE :role MEMBER OF u.roles")
    Long countTotalByRole(@Param("role") Role role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.firstName IS NULL OR u.lastName IS NULL")
    Long countUsersWithNullNames();

    @Query("SELECT COUNT(DISTINCT u) FROM UserEntity u JOIN u.policies p")
    Long countActiveUsers();

}
