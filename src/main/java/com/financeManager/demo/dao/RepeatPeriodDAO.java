package com.financeManager.demo.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.InvalidPeriodException;
import com.financeManager.demo.model.RepeatPeriod;
import com.financeManager.demo.repositories.IRepeatPeriodRepository;

@Component
public class RepeatPeriodDAO implements IRepeatPeriodDAO {

	private static final int INVALID = -1;
	private static final int YEARLY = 4;
	private static final int WEEKLY = 3;
	private static final int DAILY = 2;
	private static final int MONTHLY = 1;

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
	public Date calculateEndDateByPeriod(Long periodId) {
		switch (periodId.intValue()) {
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

	@Override
	public Timestamp calculateStartDateByPeriod(String period) throws InvalidPeriodException {

		int periodId = this.mapPeriodToId(period.toLowerCase());

		switch (periodId) {
		case MONTHLY:
			return Timestamp.valueOf(LocalDateTime.now().minusMonths(1));
		case DAILY:
			return Timestamp.valueOf(LocalDateTime.now().minusDays(1));
		case WEEKLY:
			return Timestamp.valueOf(LocalDateTime.now().minusWeeks(1));
		case YEARLY:
			return Timestamp.valueOf(LocalDateTime.now().minusYears(1));
		default:
			throw new InvalidPeriodException("Bad input period input! Input: month/day/week/year");
		}
	}

	private int mapPeriodToId(String period) {
		switch (period) {
		case "month":
			return MONTHLY;
		case "day":
			return DAILY;
		case "week":
			return WEEKLY;
		case "year":
			return YEARLY;
		default:
			return INVALID;
		}
	}

}
