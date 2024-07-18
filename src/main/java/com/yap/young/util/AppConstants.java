package com.yap.young.util;

public class AppConstants {

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SECRET_CODE_GENERATION_SUCCESS_MSG = "Successfully secret code generated!";

    public static final String ERROR_REALM_NOT_FOUND = "Realm not exist ";

    public static final String ERROR_USER_NOT_FOUND = "User not exist with id: ";

    public static final String USER_CREATION_SUCCESS_MSG = "Successfully user created!";

    public static final String USER_CREATION_FAILED_MSG = "User creation failed!";

    public static final String MESSAGE = "message";

    public static final String STATUS_CODE = "statusCode";

    public static final String DATA = "data";

    public static final String TOTAL_COUNT = "totalCount";

    public static final String ROLE_BENEFICIARY = "beneficiary";

    public static final String ROLE_SPONSOR = "sponsor";

    public static final String ERROR_SECRET_CODE_NOT_FOUND = "Given code not exist: ";

    public static final String ERROR_SECRET_CODE_ALREADY_USED = "Given code already used";

    public static final String ERROR_SECRET_CODE_EXPIRED = "Given code is expired";

    public static final String SUCCESS_SECRET_CODE_VERIFIED = "Given code successfully verified!";

    public static final String ERROR_COUNTRY_NOT_FOUND = "Given country not exist. ";

    public static final String ERROR_USER_INVALID_CREDENTIALS = "Invalid credentials or account setup incomplete.";

    public static final String FULL_NAME = "fullName";

    public static final String GENDER = "gender";

    public static final String DOB = "dob";

    public static final String COUNTRY = "country";

    public static final String PARENT_ID = "parentId";

    public static final String MOBILE = "mobile";

    public static final String PASSWORD_UPDATE_SUCCESS = "Password updated successfully!";

    public static final String VERIFICATION_CODE_EMAIL_TEMPLATE = "VerificationCodeEmailTemplate";

    public static final String VERIFICATION_CODE = "Verification Code";

    public static final String EMAIL_SUCCESS_MSG = "Email notification send successfully.";

    public static final String USER_SUCCESS_LOG = "User created successfully. Username: {}, User ID: {}";

    public static final String USER_PASSWORD_UPDATE_SUCCESS_LOG = "User password updated successfully. User ID: {}";

    public static final String SECRET_CODE_GENERATION_LOG = "Secret code generated for the user. User ID: {}";

    public static final String SECRET_CODE_VALIDATION_LOG = "Secret code validated for the user. User ID: {}";

    public static final String APPLICATION_JSON = "application/json";

    public static final String AUTHORIZATION = "Authorization";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String ACCEPT = "accept";

    public static final String MOBILE_SUCCESS_MSG = "Mobile notification send successfully.";

    public static final String MOBILE_NUMBER_NOT_FOUND = "Given mobile number is not found.";

    public static final String EMAIL_NOT_FOUND = "Given email id is not found.";

    public static final String ERROR_NOTIFICATION_TYPE_NOT_FOUND = "Notification type id not found";

    public static final String UPDATED_AT = "updatedAt";

    public static final String PLUS = "+";

    public static final String PREFERRED_USERNAME = "preferred_username";

    public static final String ERROR_TAG_NAME_EXIST = "Given tag name already exist";

    public static final String TAG_NAME_VERIFIED = "Given tag name successfully verified";

    public static final String AT_SYMBOL = "@";

    public static final String USER_PROFILE_UPDATED_SUCCESS = "Given user profile details updated successfully";

    public static final String SUB = "sub";

    public static final String EMAIL = "email";

    public static final String CARD_COLOR = "cardColor";

    public static final String TAG_NAME = "tagName";

    public static final String CARD_NAME = "cardName";

    public static final String GRANT_TYPE = "grant_type";

    public static final String CLIENT_ID = "client_id";

    public static final String CLIENT_SECRET = "client_secret";

    public static final String CLIENT_CREDENTIALS = "client_credentials";

    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String DOCUMENT_TYPE = "documentType";

    public static final String FRONT_IMAGE = "frontImage";

    public static final String BACK_IMAGE = "backImage";

    public static final String USER_PROFILE_RETRIEVED_SUCCESS = "Given user profile details retrieved successfully";

    public static final String ERROR_AGE_LESS_THAN_EIGHT = "Age must be at least 8 years old.";

    public static final String ERROR_AGE_GREATER_THAN_SEVENTEEN = "Age must be 17 years old or younger.";

    public static final String AGE_BETWEEN_WARNING_RANGE = "Age is about to turn 18.";

    public static final String ERROR_SPONSOR_NAME_MISMATCH = "Sponsor name mismatch with the document provided.";

    public static final String ERROR_NOT_VALID_EID = "Sponsor name not found in given emirate card. It seems to be adult emirate card. Please upload a valid child emirate card!";

    public static final String ERROR_EMAIL_ALREADY_VERIFIED = "Given user's mail id is already verified!";

    public static final String ERROR_TAG_NAME_IS_BLANK = "Given tag name cannot be empty!";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String ERROR_TOKEN_IS_NOT_VALID = "Token is not active";

    public static final String ERROR_DEVICE_ID_ALREADY_BOUNDED = "Given device is already bound to the user";

    public static final String USER_DEVICE_CREATED_SUCCESS = "Given device is registered successfully against the user!";

    public static final String DEVICE_ID = "deviceId";

    public static final String USER_DEVICE_VERIFIED_SUCCESS = "Given user and device details are verified successfully!";

    public static final String ERROR_USER_DEVICE_NOT_BOUNDED = "Given device is not bounded with the user!";

    public static final String NAME = "name";

    public static final String VERIFICATION_CODE_OBJ = "verificationCode";

    public static final String OS_VERSION = "osVersion";

    public static final String DEVICE_SIGN_IN_CONFIRMATION_EMAIL_TEMPLATE = "DeviceSignInConfirmationEmailTemplate";

    public static final String SECURITY_ALERT = "Security alert";

    public static final String ERROR_TAG_NAME_RANGE = "YAP tag must be between 3 and 20 characters";

    public static final String ERROR_DEVICE_NOT_BOUNDED = "Given device is not bounded with the user!";

    public static final String USER_DEVICE_UNBOUNDED_SUCCESS = "Device is unbounded from a user successfully";

    public static final String USER_LOGGED_OUT_SUCCESS = "Given user logged out from the device successfully!";

    public static final String USER_IMAGE_UPDATED_SUCCESS = "Given user profile image updated successfully!";

    public static  final String USER_PROFILE_SUCCESS = "User Profile retrieved successfully";

    public static final String USER_PROFILE_NOT_FOUND = "User profile not found";

    public static final String ERROR_USER_ALREADY_EXISTS = "User profile already exists";

    public static final String PASSWORD_VERIFIED_SUCCESS = "Given password is verified successfully!";

    public static final String ERROR_PASSWORD_NOT_CORRECT = "Given password is incorrect!";

    public static final CharSequence ERROR_401_UNAUTHORIZED = "401 Unauthorized";
}
