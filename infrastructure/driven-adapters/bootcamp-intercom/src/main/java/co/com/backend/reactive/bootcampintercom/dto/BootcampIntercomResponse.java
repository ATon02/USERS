package co.com.backend.reactive.bootcampintercom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.com.backend.reactive.model.bootcampdata.BootcampData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampIntercomResponse {
    private int status;
    private String message;
    private String path;
    private String timestamp;
    private BootcampData data;
}