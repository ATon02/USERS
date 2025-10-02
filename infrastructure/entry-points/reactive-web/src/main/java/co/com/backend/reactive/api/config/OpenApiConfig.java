package co.com.backend.reactive.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.response.UserResponseDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import co.com.backend.reactive.api.dtos.response.ErrorResponse;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("User Management API").version("v1"));
    }

    @Bean
    public GroupedOpenApi publicApi(OpenApiCustomizer customizer) {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(customizer)
                .build();
    }

    @Bean
    @Primary
    public OpenApiCustomizer customizer() {
        return openApi -> {
            openApi.getComponents()
                    .addSchemas("UserRequest", new Schema<UserRequestDTO>()
                            .addProperty("name", new StringSchema().maxLength(100))
                            .addProperty("email", new StringSchema().maxLength(255)))
                    .addSchemas("UserResponse", new Schema<UserResponseDTO>()
                            .addProperty("id", new Schema<>().type("integer").format("int64"))
                            .addProperty("name", new StringSchema())
                            .addProperty("email", new StringSchema()))
                    .addSchemas("UserSuccessResponse", new Schema<BaseResponse<UserResponseDTO>>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time"))
                            .addProperty("data", new Schema<>().$ref("#/components/schemas/UserResponse")))
                    .addSchemas("ErrorResponse", new Schema<ErrorResponse>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time")));

            PathItem saveUserPath = new PathItem()
                    .post(new Operation()
                            .operationId("saveUser")
                            .tags(List.of("User"))
                            .summary("Create a new user")
                            .description("Creates a new user with the provided information and validates the data")
                            .requestBody(new RequestBody()
                                    .description("User payload")
                                    .required(true)
                                    .content(new Content().addMediaType("application/json",
                                            new io.swagger.v3.oas.models.media.MediaType()
                                                    .schema(new Schema<>().$ref("#/components/schemas/UserRequest")))))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("User created successfully")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/UserSuccessResponse")))))
                                    .addApiResponse("400", new ApiResponse()
                                            .description("Validation error - Invalid user data")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                            )
                    );

            openApi.path("/api/v1/user", saveUserPath);
        };
    }
}
