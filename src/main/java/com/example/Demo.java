package com.example;

import org.springframework.beans.factory.annotation.Autowired;

import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IWalletRepository;

public class Demo {
	@Autowired
	private IWalletRepository repo;
	public static void main(String[] args) throws Exception {
<<<<<<< HEAD

=======
		
			new Demo().repo.save(new Wallet());
>>>>>>> a899bbc4fa9dfa3e2c0ee1acc19b7ad873aaa7ee
			
			
			
		
	}

}
