package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID accountId, String accountOwner, BigDecimal balance) {

}