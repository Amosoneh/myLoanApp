package com.myloanapp.services;

import com.myloanapp.data.dtos.requests.LoanApplicationRequest;
import com.myloanapp.data.dtos.requests.LoginRequest;
import com.myloanapp.data.dtos.requests.RegisterRequest;
import com.myloanapp.data.dtos.responses.LoanApplicationResponse;
import com.myloanapp.data.dtos.responses.LoginResponse;
import com.myloanapp.data.dtos.responses.RegisterResponse;
import com.myloanapp.data.models.*;
import com.myloanapp.data.repositories.LoanApplicationRepository;
import com.myloanapp.data.repositories.UserRepository;
import com.myloanapp.exceptions.LoanNotFoundException;
import com.myloanapp.exceptions.UserNotFoundException;
import com.myloanapp.exceptions.UserAlreadyExistException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    @Override
    public RegisterResponse register(RegisterRequest request) throws UserAlreadyExistException{
        boolean anyMatch = userRepository.findAll().stream()
                .anyMatch(user1 -> user1.getEmail().equals(request.getEmail()));
        if(anyMatch) throw new UserAlreadyExistException("user with email already exist");
        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .build();
        userRepository.save(user);

        return buildRegResponse(user);
    }

    private RegisterResponse buildRegResponse(AppUser user) {
        return RegisterResponse.builder()
                .userId(user.getId())
                .message("Registration Successful")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) throws UserNotFoundException {
        var foundUser = userRepository.findUserByEmail(request.getEmail());
        if (foundUser != null){
            if (foundUser.getEmail().equalsIgnoreCase(request.getEmail()) && foundUser.getPassword().equals(request.getPassword())){
                return buildLoginResponse(foundUser);
            }
            else {
                return LoginResponse.builder().code(400).message("Login failed, bad credentials").build();
            }
        }
        throw new UserNotFoundException("User with the email not found");


    }

    private LoginResponse buildLoginResponse(AppUser user) {
        return LoginResponse.builder()
                .code(200)
                .message("User login successfully")
                .user(user)
                .build();
    }

    @Override
    public LoanApplicationResponse applyForLoan(LoanApplicationRequest request, String userId) throws UserNotFoundException {
        var foundUser = userRepository.findUserById(userId);
        if (foundUser == null) throw  new UserNotFoundException("User not found");
        AppUser user = new AppUser();
        LoanApplication loanApplication = new LoanApplication();
        if (foundUser.getRole().equals(Role.CUSTOMER)){
            loanApplication = LoanApplication.builder()
                    .purposeForLoan(request.getPurposeForLoan())
                    .customerId(foundUser.getId())
                    .loanAgreement("")
                    .loanAmount(request.getLoanAmount())
                    .loanStatus(Status.PENDING)
                    .repaymentPlan(RepaymentPlan.valueOf(request.getRepaymentPlan().toUpperCase()))
                    .build();
        }
        LoanApplication savedLoanApplication = loanApplicationRepository.save(loanApplication);
        userRepository.save(user);
        return LoanApplicationResponse.builder()
                .customerId(savedLoanApplication.getCustomerId())
                .id(savedLoanApplication.getId())
                .loanAmount(savedLoanApplication.getLoanAmount())
                .message(String.format("Your loan application of N" +
                        "%d has been submitted and pending. We will get back to you", savedLoanApplication.getLoanAmount()))
                .loanStatus(savedLoanApplication.getLoanStatus())
                .build();
    }

    @Override
    public LoanApplicationResponse viewLoanApplicationStatus(String loanApplicationId) throws LoanNotFoundException {
        LoanApplication loanApplication = loanApplicationRepository.findLoanApplicationById(loanApplicationId);
        log.info("{}", loanApplication);
        if(loanApplication == null) throw  new LoanNotFoundException("No loan application with that ID found");
        return LoanApplicationResponse.builder()
                .loanAmount(loanApplication.getLoanAmount())
                .id(loanApplication.getId())
                .loanStatus(loanApplication.getLoanStatus())
                .customerId(loanApplication.getCustomerId())
                .message(String.format("LOAN STATUS => %s", loanApplication.getLoanStatus()))
                .build();
    }


    @Override
    public List<LoanApplication> viewAllLoanApplication(String customerId) {
        List<LoanApplication> myLoans = new ArrayList<>();
        AppUser user = userRepository.findUserById(customerId);
        List<LoanApplication> loans = loanApplicationRepository.findAll();
        if (user.getRole().equals(Role.LOAN_OFFICER)){
            return loans;
        }
        else {
            for (LoanApplication loanApplication : loans) {
                if(loanApplication.getCustomerId() != null){
                    if (loanApplication.getCustomerId().equals(user.getId())) {
                        myLoans.add(loanApplication);
                    }
                }
            }
            return myLoans;
        }
    }

    @Override
    public String approveLoanApplication(String loanApplicationId, String userId) throws LoanNotFoundException {
        AppUser user = userRepository.findUserById(userId);
        LoanApplication loan = loanApplicationRepository.findLoanApplicationById(loanApplicationId);
        if(loan == null) throw new LoanNotFoundException("Loan application not found");
        if(user != null && user.getRole().equals(Role.LOAN_OFFICER)){
            loan.setLoanStatus(Status.APPROVED);
            loanApplicationRepository.save(loan);
            userRepository.save(user);
            return String.format("Congratulation, your loan has been %s", loan.getLoanStatus());
        }
        else return "Not allowed";
    }

    @Override
    public String rejectLoanApplication(String loanApplicationId, String userId) throws LoanNotFoundException {
        AppUser user = userRepository.findUserById(userId);
        LoanApplication loan = loanApplicationRepository.findLoanApplicationById(loanApplicationId);
        if(loan == null) throw new LoanNotFoundException("Loan application not found");
        if(user != null && user.getRole().equals(Role.LOAN_OFFICER)){
            loan.setLoanStatus(Status.REJECTED);
            loanApplicationRepository.save(loan);
            userRepository.save(user);
            return String.format("Sorry your loan application has been %s due to the reason stated for the loan", loan.getLoanStatus());
        }
        return "Not allowed";    }

    @Override
    public void generateLoanAgreement(String loanApplicationId, String userId) throws LoanNotFoundException {
        AppUser user = userRepository.findUserById(userId);
        LoanApplication loan = loanApplicationRepository.findLoanApplicationById(loanApplicationId);
        if(loan == null) throw new LoanNotFoundException("Loan application not found");
        double monthlyRepay = (loan.getLoanAmount() * 0.12 + loan.getLoanAmount()) / 12;
        if (user != null && user.getRole().equals(Role.LOAN_OFFICER)){
            if(loan.getLoanStatus().equals(Status.APPROVED)){
                String message = ("LOAN AGREEMENT FORM" +
                        "\nLoan Status = " + loan.getLoanStatus()+
                        "\nApproved amount = " +loan.getLoanAmount()+
                        "\nInterest rate = 12%" +
                        "\nLoan duration = 12 months" +
                        "\nRepayment plan = " + loan.getRepaymentPlan()+
                        "\nAmount per month = " + monthlyRepay+
                        "\nYour repayment starts next month\n");
                loan.setLoanAgreement(message);
                loanApplicationRepository.save(loan);
                userRepository.save(user);
            }else loan.setLoanAgreement("");
        }
    }

    @Override
    public String viewLoanAgreement(String loanApplicationId) {
        var loan = loanApplicationRepository.findLoanApplicationById(loanApplicationId);
        if (loan == null) return "Loan not found";
        return loan.getLoanAgreement();
    }

}
