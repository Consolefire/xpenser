package com.consolefire.xpenser.cloud.user.data.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserUpdateRequestDto {

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    @NotNull(message = "Account non-expired status is required")
    private Boolean accountNonExpired;

    @NotNull(message = "Credentials non-expired status is required")
    private Boolean credentialsNonExpired;

    @NotNull(message = "Account non-locked status is required")
    private Boolean accountNonLocked;

    private Set<String> roles;

}