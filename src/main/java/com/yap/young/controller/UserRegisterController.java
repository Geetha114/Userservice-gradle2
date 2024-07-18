package com.yap.young.controller;

import com.yap.young.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user_register")
@RequiredArgsConstructor
public class UserRegisterController {

    private final UserService userService;

    @Operation(summary = "Scan a document", description = "Scans the provided document images and returns the result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully scanned the document",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping(value = "/scan", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> getDataByScanDocument(@RequestParam("parentId") String parentId, @RequestParam("documentType") String documentType,
                                                        @Parameter(description = "Upload front side of Emirate Id") @RequestParam("frontImage") MultipartFile frontImage,
                                                        @Parameter(description = "Upload back side of Emirate Id") @RequestParam("backImage") MultipartFile backImage) {
        return userService.getDataByScanDocument(parentId, documentType, frontImage, backImage);
    }
}
