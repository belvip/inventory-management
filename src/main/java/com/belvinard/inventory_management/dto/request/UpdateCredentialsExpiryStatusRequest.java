package com.belvinard.inventory_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateCredentialsExpiryStatusRequest {

    @Schema(description = "ID de l'utilisateur", example = "123")
    private Long userId;

    @Schema(description = "True pour marquer les identifiants comme expirés, False sinon", example = "true")
    private boolean expire;
}
