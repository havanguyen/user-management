package com.hanguyen.registercourses.repository;
import com.hanguyen.registercourses.entity.RefreshToken;
import com.hanguyen.registercourses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    @Modifying
    int deleteByUser(User user);
}