package com.yap.young.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yap.young.dto.MessagesWrapperDTO;
import com.yap.young.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class CommonUtils {

    private static final String MOBILE_REGEX = "(\\d{3})(\\d{5})(\\d{4})";

    private static final String MOBILE_REPLACEMENT = "$1*****$3";

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final SecureRandom random = new SecureRandom();

    public static Integer generateRandomNumber(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        int number;
        number = min + random.nextInt((max - min) + 1);

        return number;
    }

    public static String getUsername(String fullName) {
        return fullName.replaceAll("\\s+", "_").trim();
    }

    public static String getFirstNameFromString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        int firstSpaceIndex = input.indexOf(' ');

        if (firstSpaceIndex == -1) {
            return input; // No space found, return the entire string
        }

        return input.substring(0, firstSpaceIndex);
    }

    public static boolean isCurrentTimeGreaterThanCreatedPlusSeconds(LocalDateTime createdTime, LocalDateTime now, int seconds) {
        LocalDateTime createdPlus15Minutes = createdTime.plusSeconds(seconds);
        return now.isAfter(createdPlus15Minutes);
    }

    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = authentication.getName() != null ? authentication.getName() : null;
        if (userId != null && userId.isBlank()) {
            return userId;
        }
        throw new ResourceNotFoundException(AppConstants.ERROR_USER_NOT_FOUND + userId);
    }

    public static String getJSONFromPayload(MessagesWrapperDTO payload) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(payload);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid email format");
        }

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 4) {
            // Show only the first and last character
            localPart = localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1);
        } else {
            // Show the first two and last two characters
            localPart = localPart.substring(0, 2) + "***" + localPart.substring(localPart.length() - 2);
        }

        return localPart + "@" + domain;
    }

    public static String maskMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            return mobileNumber;
        }
        return AppConstants.PLUS + mobileNumber.replaceAll(MOBILE_REGEX, MOBILE_REPLACEMENT);
    }

    public static String getLastNameFromString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        int firstSpaceIndex = input.indexOf(' ');

        if (firstSpaceIndex == -1) {
            return "";
        }

        return input.substring(firstSpaceIndex + 1).trim();
    }

    public static String getLastNameWithUnderScoreFromString(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] nameParts = fullName.split(" ");
        StringBuilder lastNameBuilder = new StringBuilder();

        for (int i = 1; i < nameParts.length; i++) {
            if (i > 1) {
                lastNameBuilder.append("_");
            }
            lastNameBuilder.append(nameParts[i]);
        }

        return lastNameBuilder.toString();
    }

    public static String getMonthFromDob(String dob) {
        if (dob == null || dob.isEmpty()) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = LocalDate.parse(dob, formatter);

        return String.format("%02d", date.getMonthValue());
    }

    public static String transformSuggestion(String suggestion) {
        if (suggestion == null || suggestion.isEmpty()) {
            return suggestion;
        }
        return AppConstants.AT_SYMBOL + suggestion.substring(0, 1).toLowerCase() + suggestion.substring(1).toLowerCase();
    }

    public static String getGender(char gender) {
        if (gender != '\u0000')
            return gender == 'M' ? "MALE" : "FEMALE";
        return null;
    }

    public static int getAgeFromDob(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public static boolean isAgeInWarningRange(LocalDate dob) {
        Period period = Period.between(dob, LocalDate.now());
        double ageInYears = period.getYears() + period.getMonths() / 12.0;
        return ageInYears >= 17.5 && ageInYears < 18;
    }

    public static String getLastNCharacters(String input, int n) {
        if (input == null) {
            return "";
        }
        int length = input.length();
        return length <= n ? input : input.substring(length - n);
    }
}
