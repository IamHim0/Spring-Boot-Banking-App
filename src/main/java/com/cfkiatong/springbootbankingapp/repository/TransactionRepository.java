package com.cfkiatong.springbootbankingapp.repository;

import com.cfkiatong.springbootbankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
