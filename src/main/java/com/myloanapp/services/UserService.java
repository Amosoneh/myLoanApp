package com.myloanapp.services;

import com.myloanapp.data.dtos.requests.LoanApplicationRequest;
import com.myloanapp.data.dtos.requests.LoginRequest;
import com.myloanapp.data.dtos.requests.RegisterRequest;
import com.myloanapp.data.dtos.responses.LoanApplicationResponse;
import com.myloanapp.data.dtos.responses.LoginResponse;
import com.myloanapp.data.dtos.responses.RegisterResponse;
import com.myloanapp.data.models.LoanApplication;
import com.myloanapp.exceptions.LoanNotFoundException;
import com.myloanapp.exceptions.UserNotFoundException;
import com.myloanapp.exceptions.UserAlreadyExistException;

import java.util.List;

public interface UserService {
    RegisterResponse register(RegisterRequest request) throws UserAlreadyExistException;

    LoginResponse login(LoginRequest request) throws UserNotFoundException;

    LoanApplicationResponse applyForLoan(LoanApplicationRequest request, String userId) throws UserNotFoundException;

    LoanApplicationResponse viewLoanApplicationStatus(String loanApplicationId) throws LoanNotFoundException;


    List<LoanApplication> viewAllLoanApplication(String customerId);

    String approveLoanApplication(String loanApplicationId, String userId) throws LoanNotFoundException, UserNotFoundException;
    String rejectLoanApplication(String loanApplicationId, String userId) throws LoanNotFoundException, UserNotFoundException;
    void generateLoanAgreement(String loanApplicationId, String userId) throws LoanNotFoundException, UserNotFoundException;

    String viewLoanAgreement(String loanApplicationId);

}
