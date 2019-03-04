package com.financeManager.demo.dao;

import java.sql.Date;
import java.util.List;

import com.financeManager.demo.model.RepeatPeriod;

public interface IRepeatPeriodDAO {
	
	public List<RepeatPeriod> getAll();
	public RepeatPeriod getById(Long id);
	public Date calculateEndDateByPeriod(RepeatPeriod repeatPeriod);
	
}
