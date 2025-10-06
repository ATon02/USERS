package co.com.backend.reactive.api.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBootcampRequest {
    @NotEmpty(message = "BootcampIds cannot be empty")
    @Size(min = 1, max = 5, message = "BootcampIds size must be between 1 and 5 elements")
    private List<Long> bootcampIds;
}
