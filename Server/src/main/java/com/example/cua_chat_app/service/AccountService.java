package com.example.cua_chat_app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


import com.example.cua_chat_app.dto.request.account.AccountCreationRequest;
import com.example.cua_chat_app.dto.request.account.AccountUpdateRequest;
import com.example.cua_chat_app.dto.response.ApiResponse;
import com.example.cua_chat_app.dto.response.account.AccountResponse;
import com.example.cua_chat_app.entity.PasswordResetToken;
import com.example.cua_chat_app.entity.Role;
import com.example.cua_chat_app.entity.Status;
import com.example.cua_chat_app.entity.User;
import com.example.cua_chat_app.exception.AppException;
import com.example.cua_chat_app.exception.ErrorCode;

import com.example.cua_chat_app.repository.PasswordResetTokenRepository;
import com.example.cua_chat_app.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    private static final Random RANDOM = new Random();

    public AccountResponse createAccount(AccountCreationRequest accountRequest) {
        Optional<User> account = userRepository.findByEmail(accountRequest.getEmail());
        if (account.isPresent()) {
            throw  new AppException(ErrorCode.USER_EXISTED);
        }

        User validAccount = User.builder()
                .role(Role.USER)
                .email(accountRequest.getEmail())
                .fullName(accountRequest.getFullName())
                .status(Status.OFFLINE)
                .build();
        validAccount.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        User savedAccount = userRepository.save(validAccount);

        return mapToAccountResponse(savedAccount);
    }

    public List<AccountResponse> getAllAccounts() {
        List<User> accounts = userRepository.findAll();
        return accounts.stream().map(this::mapToAccountResponse).toList();
    }

    public Optional<AccountResponse> getAccountById(String email) {
        Optional<User> account = userRepository.findByEmail(email);
        return account.map(this::mapToAccountResponse);
    }

    public AccountResponse updateAccount(String id, AccountUpdateRequest accountRequest) {
        Optional<User> accountOpt = userRepository.findById(id);
        if (accountOpt.isPresent()) {
            User account = accountOpt.get();
            if(passwordEncoder.matches(accountRequest.getOldPassword(), account.getPassword()) &&
                    accountRequest.getConfirmPassword().equals(accountRequest.getNewPassword())) {

                account.setPassword(passwordEncoder.encode(accountRequest.getNewPassword()));
            }
            else {throw new AppException(ErrorCode.WRONG_PASSWORD);}
            User updatedAccount = userRepository.save(account);
            return mapToAccountResponse(updatedAccount);
        }
        return null;
    }

    public boolean deleteAccount(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private AccountResponse mapToAccountResponse(User user) {
        return AccountResponse.builder()
                .id(user.getNickName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public String sendResetCode(@Email String email) {
        Optional<User> accountOpt = userRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return "Email is not exist!";
        }

        String otp = String.format("%06d", RANDOM.nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByEmail(email);

        if (existingTokenOpt.isPresent()) {
            PasswordResetToken existingToken = existingTokenOpt.get();
            existingToken.setToken(otp);
            existingToken.setExpiryDate(expiryTime);
            tokenRepository.save(existingToken);
        } else {
            PasswordResetToken token = new PasswordResetToken(email, otp, expiryTime);
            tokenRepository.save(token);
        }

        sendEmail(email, "Forgot password - OTP Code", "Your OTP Code: " + otp);

        return "OTP Code has been sent";
    }

    public void sendEmail(@Email String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public ApiResponse<String> confirmOTP(String otp, @Email String email){
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndToken(email, otp);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(400, "Invalid or expired OTP", null);
        }
        return new ApiResponse<>(200, "Valid OTP", null);
    }

    @Transactional
    public String resetPassword(@Email String email, String newPassword, String confirmPassword) {
        Optional<User> accountOpt = userRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return "Email is not exist!";
        }

        User account = accountOpt.get();
        if(newPassword.equals(confirmPassword)) {
            account.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(account);
            tokenRepository.deleteByEmail(email);
            return "Successful";
        }
        else {return "Password isn't match";}
    }
}
