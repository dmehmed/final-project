package com.financeManager.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.User;

@Repository
public interface IUsersRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
