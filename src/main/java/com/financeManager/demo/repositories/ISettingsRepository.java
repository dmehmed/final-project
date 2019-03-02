package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financeManager.demo.model.Settings;

public interface ISettingsRepository extends JpaRepository<Settings, Long> {

}
