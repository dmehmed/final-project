package com.financeManager.demo.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.RepeatPeriod;
import com.financeManager.demo.repositories.IRepeatPeriodRepository;

@Component
public class RepeatPeriodDAO implements IRepeatPeriodDAO {

	@Autowired
	private IRepeatPeriodRepository periodRepo;
	private List<RepeatPeriod> periods;

	@Override
	public List<RepeatPeriod> getAll() {
		return this.periods;
	}

	@Override
	public RepeatPeriod getById(Long id) {
		return this.periods.stream().filter(period -> period.getId().equals(id)).findFirst().get();
	}

	@Autowired
	private void setPeriods() {
		this.periods = this.periodRepo.findAll();
	}

	@Override
	public Date calculateStartDateByPeriod(RepeatPeriod repeatPeriod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date calculateEndDateByPeriod(RepeatPeriod repeatPeriod) {
		// TODO Auto-generated method stub
		return null;
	}

}
