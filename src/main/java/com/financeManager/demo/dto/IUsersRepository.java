package com.financeManager.demo.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.User;

@Repository
public interface IUsersRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
