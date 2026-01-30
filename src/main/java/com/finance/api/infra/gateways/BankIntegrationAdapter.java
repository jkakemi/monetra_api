package com.finance.api.infra.gateways;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.api.application.gateways.BankIntegrationGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

@Component
public class BankIntegrationAdapter implements BankIntegrationGateway {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${bank.api.url}")
    private String API_URL;

    @Override
    public BigDecimal getBalanceFromExternalBank(String accountNumber) {
        try {

            String url = String.format("%s?accountNumber=%s", API_URL, accountNumber);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<MockAccountDTO> accounts = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MockAccountDTO.class)
                );

                if (!accounts.isEmpty()) {
                    BigDecimal saldo = accounts.get(0).balance();
                    return saldo;
                }
            }

            return BigDecimal.ZERO;

        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MockAccountDTO(
            @JsonAlias("accountNumber") String accountNumber,
            @JsonAlias("balance") BigDecimal balance
    ) {}
}
