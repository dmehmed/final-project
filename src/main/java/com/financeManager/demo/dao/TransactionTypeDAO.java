package com.financeManager.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.TransactionType;
import com.financeManager.demo.repositories.ITransactionTypeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class TransactionTypeDAO implements ITransactionTypeDAO {

	@Autowired
	private ITransactionTypeRepository typeRepo;

	private List<TransactionType> types;

	@Override
	public List<TransactionType> getAll() {
		return this.types;
	}

	@Override
	public TransactionType getById(Long id) {
		return this.types.stream().filter(type -> type.getId().equals(id)).findFirst().get();
	}
	
	@Autowired
	private void setTypes() {
		this.types = this.typeRepo.findAll();
	}
	
}
