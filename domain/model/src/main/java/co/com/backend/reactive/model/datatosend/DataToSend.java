package co.com.backend.reactive.model.datatosend;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DataToSend {
    private Long userId;
    private String name;
    private String email;
    private List<Long> bootcampIds;
}
