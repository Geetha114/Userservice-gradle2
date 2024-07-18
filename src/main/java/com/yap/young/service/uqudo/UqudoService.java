package com.yap.young.service.uqudo;

import com.yap.young.dto.UqudoAuthResponse;
import com.yap.young.dto.UqudoIdFrontResponseDTO;
import com.yap.young.dto.UqudoIdResponseDTO;
import com.yap.young.exception.UnsupportedMediaTypeException;
import com.yap.young.exception.s3bucket.*;
import com.yap.young.exception.uqudo.UqudoApiException;
import com.yap.young.exception.uqudo.UqudoAuthException;
import com.yap.young.util.AppConstants;
import com.yap.young.util.CommonUtils;
import jakarta.ws.rs.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class UqudoService {

    @Value("${uqudo.authUrl}")
    private String authUrl;

    @Value("${uqudo.apiUrl}")
    private String apiUrl;

    @Value("${uqudo.clientId}")
    private String clientId;

    @Value("${uqudo.clientSecret}")
    private String clientSecret;

    RestTemplate restTemplate = new RestTemplate();

    private static final Logger LOGGER = LoggerFactory.getLogger(UqudoService.class);

    public String getAccessToken() {
        try {
            HttpEntity<MultiValueMap<String, String>> formEntity = getHttpEntityForUqudoAuth();
            ResponseEntity<UqudoAuthResponse> response = restTemplate.postForEntity(authUrl, formEntity, UqudoAuthResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getAccess_token() != null) {
                return response.getBody().getAccess_token();
            }
            throw new UqudoAuthException("Error obtaining access token: Uqudo service");
        } catch (UqudoAuthException e) {
            throw new UqudoAuthException("Error obtaining access token: Uqudo service");
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntityForUqudoAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(AppConstants.GRANT_TYPE, AppConstants.CLIENT_CREDENTIALS);
        requestBody.add(AppConstants.CLIENT_ID, clientId);
        requestBody.add(AppConstants.CLIENT_SECRET, clientSecret);
        return new HttpEntity<>(requestBody, headers);
    }

    public UqudoIdResponseDTO scanDocument(String documentType, MultipartFile frontImage, MultipartFile backImage) {
        try {
            HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = getHttpEntityForUqudoScanAPI(documentType, frontImage, backImage);

            ResponseEntity<UqudoIdResponseDTO> response = restTemplate.postForEntity(apiUrl, requestEntity, UqudoIdResponseDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getFront() != null) {
                LOGGER.info("Scan document for the user identity number: {}", CommonUtils.getLastNCharacters(response.getBody().getFront().getIdentityNumber(), 6));
                return response.getBody();
            }
            throw new UqudoApiException("Error scanning document: " + response.getBody());
        } catch (HttpClientErrorException.BadRequest e) {
            throw new ClientErrorException("Bad Request: " + e.getMessage());
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new InvalidAccessKeyException("Unauthorized: " + e.getMessage());
        } catch (HttpClientErrorException.Forbidden e) {
            throw new InvalidAccessKeyException("Invalid AWS access key ID: " + e.getMessage());
        } catch (HttpClientErrorException.NotAcceptable e) {
            throw new FileTooLargeException("File size is too large: " + e.getMessage());
        } catch (HttpClientErrorException.UnsupportedMediaType e) {
            throw new UnsupportedMediaTypeException("Unsupported Media Type: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new InternalServerErrorException("Internal Server Error: " + e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Unexpected error: " + e.getMessage());
        }
    }

    private HttpEntity<MultiValueMap<String, HttpEntity<?>>> getHttpEntityForUqudoScanAPI(String documentType, MultipartFile frontImage, MultipartFile backImage) throws IOException {
        String accessToken = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(accessToken);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part(AppConstants.DOCUMENT_TYPE, documentType);
        builder.part(AppConstants.FRONT_IMAGE, convertToResource(frontImage), getMediaType(frontImage));
        builder.part(AppConstants.BACK_IMAGE, convertToResource(backImage), getMediaType(backImage));

        MultiValueMap<String, HttpEntity<?>> body = builder.build();
        return new HttpEntity<>(body, headers);
    }

    private Resource convertToResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    private MediaType getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
    }
}

