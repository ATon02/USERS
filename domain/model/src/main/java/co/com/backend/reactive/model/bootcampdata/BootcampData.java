package co.com.backend.reactive.model.bootcampdata;
import lombok.Builder;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BootcampData {
    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInDays;
}
