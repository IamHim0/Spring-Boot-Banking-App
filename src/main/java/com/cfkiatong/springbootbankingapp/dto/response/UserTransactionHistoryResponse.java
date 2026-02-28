package com.cfkiatong.springbootbankingapp.dto.response;

import java.util.List;

public class UserTransactionHistoryResponse {

    List<TransactionResponse> transactions;

    public UserTransactionHistoryResponse(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
}
