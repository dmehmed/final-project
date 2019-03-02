package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Settings;

@Repository
public interface ISettingsRepository extends JpaRepository<Settings, Long> {
	
}
