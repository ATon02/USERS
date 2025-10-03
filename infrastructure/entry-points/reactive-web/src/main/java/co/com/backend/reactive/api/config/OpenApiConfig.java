package co.com.backend.reactive.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.request.RegisterBootcampRequest;
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
                    .addSchemas("RegisterBootcampRequest", new Schema<RegisterBootcampRequest>()
                            .addProperty("bootcampIds", new ArraySchema()
                                    .items(new Schema<>().type("integer").format("int64"))
                                    .minItems(1)
                                    .maxItems(5)))
                    .addSchemas("SuccessResponse", new Schema<>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time")))
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

            PathItem registerBootcampPath = new PathItem()
                    .post(new Operation()
                            .operationId("registerUserBootcamp")
                            .tags(List.of("User"))
                            .summary("Register user to bootcamps")
                            .description("Registers a user to multiple bootcamps with validations: max 5 bootcamps, user exists, no duplicate registrations")
                            .addParametersItem(new Parameter()
                                    .name("userId")
                                    .in("path")
                                    .required(true)
                                    .description("ID of the user to register")
                                    .schema(new Schema<>().type("integer").format("int64")))
                            .requestBody(new RequestBody()
                                    .description("Bootcamp registration request with list of bootcamp IDs (1-5 elements)")
                                    .required(true)
                                    .content(new Content().addMediaType("application/json",
                                            new io.swagger.v3.oas.models.media.MediaType()
                                                    .schema(new Schema<>().$ref("#/components/schemas/RegisterBootcampRequest")))))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("User registered to bootcamps successfully")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/SuccessResponse")))))
                                    .addApiResponse("400", new ApiResponse()
                                            .description("Validation error - Invalid data, user not found, duplicate registrations, or exceeds maximum bootcamps")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                                    .addApiResponse("404", new ApiResponse()
                                            .description("User not found")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                            )
                    );

            openApi.path("/api/v1/user", saveUserPath);
            openApi.path("/api/v1/user/{userId}/register-bootcamp", registerBootcampPath);
        };
    }
}