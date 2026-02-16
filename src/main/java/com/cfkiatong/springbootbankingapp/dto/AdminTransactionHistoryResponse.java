package com.cfkiatong.springbootbankingapp.dto;


import java.util.List;

public record AdminTransactionHistoryResponse(List<AdminTransactionDTO> allTransactions) {}