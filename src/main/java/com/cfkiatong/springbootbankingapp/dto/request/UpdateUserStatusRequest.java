package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "NewUserStatus field cannot be empty.") UserStatus newUserStatus) {
}
