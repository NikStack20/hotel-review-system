package com.User.Service.UserRepos;

import com.User.Service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    // using String BEcz of performance
}
