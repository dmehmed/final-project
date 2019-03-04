package com.financeManager.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.DeletedUser;

@Repository 
public interface IDeletedUsersRepository extends JpaRepository<DeletedUser, Long> {
	Optional<DeletedUser> findByEmail(String email);
	
}
