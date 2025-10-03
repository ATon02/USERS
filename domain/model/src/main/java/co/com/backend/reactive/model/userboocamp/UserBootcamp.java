package co.com.backend.reactive.model.userboocamp;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserBootcamp {
    private Long id;
    private Long userId;
    private Long bootcampId;
}
