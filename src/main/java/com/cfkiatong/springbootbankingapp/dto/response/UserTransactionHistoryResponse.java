package com.cfkiatong.springbootbankingapp.dto.response;

import java.util.List;

public record UserTransactionHistoryResponse(List<TransactionResponse> transactions) {

}