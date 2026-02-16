package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.entity.Transaction;

import java.util.List;

public record TransactionHistoryResponse(List<TransactionDTO> transactionHistory) {

}
