package apap.ti._5.tour_package_2306275203_be.security;

import apap.ti._5.tour_package_2306275203_be.dto.response.auth.ValidateTokenResponseDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class JwtValidationService {
    @Value("${sso.auth.base-url}")
    private String authBaseUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder().baseUrl(authBaseUrl).build();
    }

    public ValidateTokenResponseDTO validateToken(String token) {
        try {
            System.out.println("🔍 Validating token with Flight BE: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null"));
            
            // Nembak ke Auth BE buat nanya: "Token ini valid gak?"
            ValidateTokenResponseDTO response = webClient.post()
                    .uri("/api/auth/validate-token")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ValidateTokenResponseDTO.class)
                    .block();
            
            System.out.println("✅ Validation response: " + (response != null ? response.isValid() : "null"));
            return response;
        } catch (Exception e) {
            System.err.println("❌ Token validation failed: " + e.getMessage());
            // Kalau error koneksi atau token salah
            return new ValidateTokenResponseDTO(false, null, null);
        }
    }
}