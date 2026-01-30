package com.finance.api.infra.gateways;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.api.application.gateways.QuoteGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BrasilApiAdapter implements QuoteGateway {

    private final String API_URL = "https://brasilapi.com.br/api/cambio/v1/cotacao";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public BigDecimal getQuote(String coinType, LocalDate date) {
        if (coinType == null || coinType.equalsIgnoreCase("BRL")) {
            return BigDecimal.ONE;
        }

        //String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

        return getQuoteForSpecificDate(coinType, date);
    }

    private BigDecimal getQuoteForSpecificDate(String coinType, LocalDate date) {
        try {
            String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String url = String.format("%s/%s/%s", API_URL, coinType, dateString);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                BrasilApiResponseDTO dto = objectMapper.readValue(response.body(), BrasilApiResponseDTO.class);

                if (dto.values() != null && !dto.values().isEmpty()) {
                    var lastQuote = dto.values().get(dto.values().size() - 1);
                    return lastQuote.saleQuote();
                } else {
                    throw new RuntimeException("Quotation found, but blank");
                }
            } else if (response.statusCode() == 404 || response.statusCode() == 400) {
                throw new IllegalArgumentException(
                        String.format("Unavailable quotation found for coinType %s ", coinType)
                );
            } else {
                throw new RuntimeException("Erro external API: " + response.statusCode());
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error " + e.getMessage());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record BrasilApiResponseDTO(
            @JsonAlias({"cotacao", "cotacoes"})
            List<QuoteItemDTO> values
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record QuoteItemDTO(
            @JsonAlias("cotacao_venda")
            BigDecimal saleQuote,

            @JsonAlias("data_hora_cotacao")
            String dateTime
    ) {}
}