package co.com.backend.reactive.usecase.user.enums;

public enum UserError {
    USER_NULL("User cannot be null"),
    USER_ID_REQUIRED("User ID is required for updates"),
    USER_NOT_FOUND("User not found"),
    USER_NAME_REQUIRED("User name is required"),
    USER_NAME_TOO_SHORT("User name must be at least 2 characters long"),
    USER_NAME_TOO_LONG("User name cannot exceed 100 characters"),
    USER_EMAIL_REQUIRED("User email is required"),
    USER_EMAIL_INVALID_FORMAT("Invalid email format"),
    USER_EMAIL_TOO_LONG("Email cannot exceed 255 characters"),
    USER_EMAIL_ALREADY_EXISTS("Email already exists"),
    USER_NOT_CREATED("User could not be created"),
    BOOTCAMP_LIST_EMPTY("Bootcamp list cannot be empty"),
    BOOTCAMP_LIST_TOO_LARGE("Cannot register more than 5 bootcamps at once"),
    USER_ALREADY_HAS_BOOTCAMPS("User already has some of these bootcamps registered"),
    BOOTCAMP_NOT_FOUND("Bootcamp not found");
    
   
    
    private final String message;
    
    UserError(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
