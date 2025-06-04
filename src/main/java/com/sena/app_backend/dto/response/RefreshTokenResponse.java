package com.sena.app_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
  private String accessToken;
  private String refreshToken;
}

