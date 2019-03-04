package com.financeManager.demo.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.RepeatPeriod;
import com.financeManager.demo.repositories.IRepeatPeriodRepository;

@Component
public class RepeatPeriodDAO implements IRepeatPeriodDAO {

	private static final String YEARLY = "yearly";
	private static final String WEEKLY = "weekly";
	private static final String DAILY = "daily";
	private static final String MONTHLY = "monthly";
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
	public Date calculateEndDateByPeriod(RepeatPeriod repeatPeriod) {
		switch (repeatPeriod.getPeriod()) {
		case MONTHLY:
			return Date.valueOf(LocalDate.now().plusMonths(1));
		case DAILY:
			return Date.valueOf(LocalDate.now().plusDays(1));
		case WEEKLY:
			return Date.valueOf(LocalDate.now().plusWeeks(1));
		case YEARLY:
			return Date.valueOf(LocalDate.now().plusYears(1));
		default:
			return Date.valueOf(LocalDate.now().plusMonths(1));
		}
	}

}