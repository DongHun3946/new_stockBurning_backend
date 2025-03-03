package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.util.ProviderType;
import org.apache.catalina.User;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.OptionalInt;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
    Optional<Users> findByUserId(String userId);
    Optional<Users> findByUserIdAndUserPw(String userId, String userPw);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByNickName(String nickname);
    Optional<Users> findByUserIdAndEmail(String userid, String email);

    Optional<Users> findByProviderTypeAndProviderId(ProviderType providerType, Long providerId);

    boolean existsByUserId(String userid);
    boolean existsByEmail(String email);
    boolean existsByNickName(String nickname);

}
