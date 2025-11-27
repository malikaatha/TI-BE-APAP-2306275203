package apap.ti._5.tour_package_2306275203_be.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponseDTO {
    private boolean valid;
    private String username;
    private String role;
}