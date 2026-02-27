package com.cfkiatong.springbootbankingapp.dto.response;

import java.util.List;

public class UserTransactionHistoryResponse {

    List<TransactionDTO> transactions;

    public UserTransactionHistoryResponse(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }
}
