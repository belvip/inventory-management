package com.belvinard.inventory_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateAccountEnabledStatusRequest {

    @Schema(description = "ID de l'utilisateur", example = "123")
    private Long userId;

    @Schema(description = "True pour activer le compte, False pour le désactiver", example = "false")
    private boolean enabled;
}
