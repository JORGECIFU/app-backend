package com.sena.app_backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
public class CoinbaseClient {
  private final WebClient wc = WebClient.create("https://api.coinbase.com");

  /**
   * Devuelve el spot price USD para la crypto dada (“BTC” o “ETH”).
   */
  public BigDecimal getSpotPrice(String crypto) {
    // ejemplo URL: /v2/prices/BTC-USD/spot
    String path = String.format("/v2/prices/%s-USD/spot", crypto);
    return wc.get()
        .uri(path)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(json -> new BigDecimal(json.at("/data/amount").asText()))
        .block();
  }
}
