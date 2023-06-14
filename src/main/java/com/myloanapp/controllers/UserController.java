package com.myloanapp.controllers;

import com.myloanapp.data.dtos.requests.LoanApplicationRequest;
import com.myloanapp.data.dtos.requests.LoginRequest;
import com.myloanapp.data.dtos.requests.RegisterRequest;
import com.myloanapp.exceptions.LoanNotFoundException;
import com.myloanapp.exceptions.UserAlreadyExistException;
import com.myloanapp.exceptions.UserNotFoundException;
import com.myloanapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?>register(@RequestBody RegisterRequest registerRequest) throws UserAlreadyExistException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws UserNotFoundException {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/applyForLoan")
    public ResponseEntity<?> applyForLoan(@RequestBody LoanApplicationRequest request, String userId) throws UserNotFoundException {
        return ResponseEntity.ok(userService.applyForLoan(request, userId));
    }

    @GetMapping("/viewLoanStatus")
    public ResponseEntity<?> viewLoanStatus(@RequestParam String loanApplicationId) throws LoanNotFoundException {
        return ResponseEntity.ok(userService.viewLoanApplicationStatus(loanApplicationId));
    }
    @GetMapping("/getAllLoanApplications")
    public ResponseEntity<?> viewAllLoanApplication (@RequestParam String userId){
        return ResponseEntity.ok(userService.viewAllLoanApplication(userId));
    }

    @PostMapping("/approveLoan")
    public ResponseEntity<?> approveLoanApplication(@RequestParam String loanApplicationId, String userId) throws UserNotFoundException, LoanNotFoundException {
        return ResponseEntity.ok(userService.approveLoanApplication(loanApplicationId, userId));
    }

    @PostMapping("/rejectLoan")
    public ResponseEntity<?> rejectLoanApplication(@RequestParam String loanApplicationId, String userId) throws UserNotFoundException, LoanNotFoundException {
        return ResponseEntity.ok(userService.rejectLoanApplication(loanApplicationId, userId));
    }

    @PostMapping("/generateAgreement")
    public ResponseEntity<?> generateLoanAgreement(@RequestParam String loanApplicationId, String userId) throws LoanNotFoundException, UserNotFoundException{
        userService.generateLoanAgreement(loanApplicationId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/viewLoanAgreement")
    public ResponseEntity<?> viewLoanAgreement(@RequestParam String loanApplicationId){
        return ResponseEntity.ok(userService.viewLoanAgreement(loanApplicationId));
    }
}
