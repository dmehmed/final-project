package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Budget;

@Repository
public interface IBudgetRepository extends JpaRepository<Budget, Long> {
	List<Budget> findAllByUserId(Long id);
	@Query("Select b from Budget b where b.isDeleted = 0")
	List<Budget> findAllActiveBudgets();
}
