package com.financeManager.demo.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.financeManager.demo.exceptions.InvalidPeriodException;
import com.financeManager.demo.model.RepeatPeriod;

public interface IRepeatPeriodDAO {
	
	public List<RepeatPeriod> getAll();
	public RepeatPeriod getById(Long id);
	public Date calculateEndDateByPeriod(Long id);
	public Timestamp calculateStartDateByPeriod(String period) throws InvalidPeriodException;
	
}
