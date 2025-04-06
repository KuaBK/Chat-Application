package com.example.cua_chat_app.controller;

import java.util.List;
import java.util.Optional;

import com.example.cua_chat_app.dto.request.account.AccountCreationRequest;
import com.example.cua_chat_app.dto.request.account.AccountUpdateRequest;
import com.example.cua_chat_app.dto.response.ApiResponse;
import com.example.cua_chat_app.dto.response.account.AccountResponse;
import com.example.cua_chat_app.repository.UserRepository;
import com.example.cua_chat_app.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final UserRepository userRepository;

    private static final String ACCOUNT_NOT_FOUND = "Account not found";

    // Create Account
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreationRequest accountRequest) {
        AccountResponse createdAccount = accountService.createAccount(accountRequest);
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .message("Account created successfully")
                .result(createdAccount)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all Accounts
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        ApiResponse<List<AccountResponse>> response = ApiResponse.<List<AccountResponse>>builder()
                .message("Fetched all accounts successfully")
                .result(accounts)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get Account by ID
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable String email) {
        Optional<AccountResponse> account = accountService.getAccountById(email);
        if (account.isPresent()) {
            ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                    .message("Account fetched successfully")
                    .result(account.get())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .message(ACCOUNT_NOT_FOUND)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Update Account
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable String id, @RequestBody AccountUpdateRequest accountRequest) {
        AccountResponse updatedAccount = accountService.updateAccount(id, accountRequest);
        if (updatedAccount != null) {
            ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                    .message("Account updated successfully")
                    .result(updatedAccount)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .message(ACCOUNT_NOT_FOUND)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Delete Account
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable String id) {
        boolean isDeleted = accountService.deleteAccount(id);
        ApiResponse<Void> response;
        if (isDeleted) {
            response = ApiResponse.<Void>builder()
                    .message("Account deleted successfully")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response = ApiResponse.<Void>builder()
                    .message(ACCOUNT_NOT_FOUND)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> sendResetCode(@RequestParam String email) {
        if(userRepository.findByEmail(email).isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String message = accountService.sendResetCode(email);
        return ResponseEntity.ok(new ApiResponse<>(200, message, null));
    }

    @PostMapping("/confirm-otp")
    public ResponseEntity<ApiResponse<String>> confirmOtp(@RequestParam String otp, @RequestParam String email) {
        return ResponseEntity.ok(accountService.confirmOTP(otp, email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        return ResponseEntity.ok(accountService.resetPassword(email, newPassword, confirmPassword));
    }
}
