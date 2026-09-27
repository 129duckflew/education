package cn.duckflew.education.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("""
            select u from User u
            where (:keyword is null or lower(u.username) like lower(concat('%', :keyword, '%'))
                   or lower(u.nickname) like lower(concat('%', :keyword, '%'))
                   or lower(u.realName) like lower(concat('%', :keyword, '%')))
              and (:role is null or u.role = :role)
            """)
    Page<User> search(@Param("keyword") String keyword, @Param("role") UserRole role, Pageable pageable);
}
