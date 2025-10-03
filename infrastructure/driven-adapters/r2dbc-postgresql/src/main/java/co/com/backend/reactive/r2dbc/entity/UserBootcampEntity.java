package co.com.backend.reactive.r2dbc.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;  
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("user_bootcamp")
public class UserBootcampEntity {
    @Id
    private Long id;
    private Long userId;
    private Long bootcampId;

}
