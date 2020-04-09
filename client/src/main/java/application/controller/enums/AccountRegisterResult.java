package application.controller.enums;

public enum AccountRegisterResult {
    ACCOUNT_REGISTER_SUCCESS,
    FAILED_BY_CREDENTIALS,
    FAILED_BY_USERNAME_TAKEN,
    FAILED_BY_EMAIL_TAKEN,
    FAILED_BY_NETWORK,
    FAILED_BY_UNEXPECTED_ERROR;
}
