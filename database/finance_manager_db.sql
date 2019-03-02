-- MySQL Script generated by MySQL Workbench
-- Sat Mar  2 01:52:09 2019
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema finance_manager_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema finance_manager_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `finance_manager_db` DEFAULT CHARACTER SET utf8 ;
USE `finance_manager_db` ;

-- -----------------------------------------------------
-- Table `finance_manager_db`.`repeat_periods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`repeat_periods` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `period` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`transaction_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`transaction_types` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`categories` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `transaction_type_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_transaction_categories_transaction_types1_idx` (`transaction_type_id` ASC) VISIBLE,
  CONSTRAINT `fk_transaction_categories_transaction_types1`
    FOREIGN KEY (`transaction_type_id`)
    REFERENCES `finance_manager_db`.`transaction_types` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `isDeleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`budgets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`budgets` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `amount` DOUBLE NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `user_id` INT(11) NOT NULL,
  `category_id` INT(11) NOT NULL,
  `repeat_period_id` INT(11) NULL DEFAULT '1',
  `isDeleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `fk_budget_users1_idx` (`user_id` ASC) VISIBLE,
  INDEX `fk_budget_repeat_period1_idx` (`repeat_period_id` ASC) VISIBLE,
  INDEX `fk_budget_transaction_categories1_idx` (`category_id` ASC) VISIBLE,
  CONSTRAINT `fk_budget_repeat_period1`
    FOREIGN KEY (`repeat_period_id`)
    REFERENCES `finance_manager_db`.`repeat_periods` (`id`),
  CONSTRAINT `fk_budget_transaction_categories1`
    FOREIGN KEY (`category_id`)
    REFERENCES `finance_manager_db`.`categories` (`id`),
  CONSTRAINT `fk_budget_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `finance_manager_db`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`countries`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`countries` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`currencies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`currencies` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(3) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`genders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`genders` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(7) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`settings` (
  `user_id` INT(11) NOT NULL,
  `currency_id` INT(11) NULL DEFAULT '1',
  `birthdate` DATE NULL DEFAULT NULL,
  `gender_id` INT(11) NULL DEFAULT NULL,
  `country_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  INDEX `fk_settings_gender1_idx` (`gender_id` ASC) VISIBLE,
  INDEX `fk_settings_countries1_idx` (`country_id` ASC) VISIBLE,
  INDEX `fk_settings_currencies1_idx` (`currency_id` ASC) VISIBLE,
  CONSTRAINT `fk_settings_countries1`
    FOREIGN KEY (`country_id`)
    REFERENCES `finance_manager_db`.`countries` (`id`),
  CONSTRAINT `fk_settings_currencies1`
    FOREIGN KEY (`currency_id`)
    REFERENCES `finance_manager_db`.`currencies` (`id`),
  CONSTRAINT `fk_settings_gender1`
    FOREIGN KEY (`gender_id`)
    REFERENCES `finance_manager_db`.`genders` (`id`),
  CONSTRAINT `fk_settings_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `finance_manager_db`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`wallets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`wallets` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `balance` DOUBLE NOT NULL DEFAULT '0',
  `limit` DOUBLE UNSIGNED NOT NULL DEFAULT '0',
  `user_id` INT(11) NOT NULL,
  `isDeleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `fk_wallets_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_wallets_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `finance_manager_db`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `finance_manager_db`.`transactions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `finance_manager_db`.`transactions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `amount` DOUBLE NOT NULL,
  `date` DATETIME NOT NULL,
  `description` VARCHAR(100) NULL DEFAULT NULL,
  `wallet_id` INT(11) NOT NULL,
  `category_id` INT(11) NOT NULL,
  `isDeleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `fk_transactions_wallets1_idx` (`wallet_id` ASC) VISIBLE,
  INDEX `fk_transactions_transaction_categories1_idx` (`category_id` ASC) VISIBLE,
  INDEX `date_index` (`date` ASC) VISIBLE,
  CONSTRAINT `fk_transactions_transaction_categories1`
    FOREIGN KEY (`category_id`)
    REFERENCES `finance_manager_db`.`categories` (`id`),
  CONSTRAINT `fk_transactions_wallets1`
    FOREIGN KEY (`wallet_id`)
    REFERENCES `finance_manager_db`.`wallets` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


/* *************************************************************** 
***************************INSERTING DATA*************************
**************************************************************** */
INSERT INTO transaction_types
VALUES (
	NULL,
	'income'
	);

INSERT INTO transaction_types
VALUES (
	NULL,
	'expense'
	);

INSERT INTO categories
VALUES (
	NULL,
	'salary',
  1
	);

INSERT INTO categories
VALUES (
	NULL,
	'grants',
  1
	);

INSERT INTO categories
VALUES (
	NULL,
	'loans',
  1
	);

INSERT INTO categories
VALUES (
	NULL,
	'others',
  1
	);

INSERT INTO categories
VALUES (
	NULL,
	'bills',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'health and personal care',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'education',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'charity',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'home and utilities',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'food and drinks',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'sports',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'transport',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'clothing and footwear',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'loans',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'gifts',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'leisure',
  2
	);

INSERT INTO categories
VALUES (
	NULL,
	'others',
  2
	);

INSERT INTO genders
VALUES (
	NULL,
	'male'
	); 

INSERT INTO genders
VALUES (
	NULL,
	'female'
	); 

INSERT INTO genders
VALUES (
	NULL,
	'others'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Albania'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Argentina'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Australia'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Austria'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Belarus'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Belgium'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Brazil'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Bulgaria'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Canada'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Croatia'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Czech Republic'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Denmark'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Dominican Republic'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Ecuador'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Estonia'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Finland'
	);

INSERT INTO countries
VALUES (
	NULL,
	'France'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Germany'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Greece'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Hungary'
	);

INSERT INTO countries
VALUES (
	NULL,
	'India'
	);
INSERT INTO countries
VALUES (
	NULL,
	'Italy'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Kenya'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Latvia'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Luxembourg'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Maldives'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Malta'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Mexico'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Monaco'
	);

INSERT INTO countries
VALUES (
	NULL,
	'Netherlands'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Norway'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Panama'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Peru'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Poland'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Portugal'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Romania'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Russia'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'San Marino'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Serbia'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Slovakia'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Slovenia'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Spain'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Switzerland'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Thailand'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Turkey'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Ukraine'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'United Kingdom'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'United States'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Venezuela'
	);
  
INSERT INTO countries
VALUES (
	NULL,
	'Vietnam'
	);

    
INSERT INTO currencies
VALUES (
	NULL,
	'EUR'
	);
  

INSERT INTO currencies
VALUES (
	NULL,
	'ALL'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'ARS'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'AUD'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'BMD'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'BGN'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'CAD'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'CNY'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'CZK'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'DKK'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'DOP'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'EGP'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'GIP'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'HUF'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'INR'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'IDR'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'ILS'
	);
  
  
INSERT INTO currencies
VALUES (
	NULL,
	'LRD'
	);


INSERT INTO currencies
VALUES (
	NULL,
	'MKD'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'MXN'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'NZD'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'RON'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'RUB'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'SGD'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'CHF'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'THB'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'TTD'
	);
  
INSERT INTO currencies
VALUES (
	NULL,
	'TRY'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'USD'
	);
    
INSERT INTO currencies
VALUES (
	NULL,
	'VEF'
	);
 
INSERT INTO repeat_periods
VALUES (
	NULL,
	'never'
	);

INSERT INTO repeat_periods
VALUES (
	NULL,
	'daily'
	);

INSERT INTO repeat_periods
VALUES (
	NULL,
	'weekly'
	);

INSERT INTO repeat_periods
VALUES (
	NULL,
	'monthly'
	);

INSERT INTO repeat_periods
VALUES (
	NULL,
	'yearly'
	);
 
 COMMIT;