package com.belvinard.inventory_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateAccountExpiryStatusRequest {

    @Schema(description = "ID de l'utilisateur", example = "123")
    private Long userId;

    @Schema(description = "True pour marquer le compte comme expiré, False sinon", example = "true")
    private boolean expire;
}
