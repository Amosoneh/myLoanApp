package com.myloanapp.services;

import com.myloanapp.data.dtos.requests.LoanApplicationRequest;
import com.myloanapp.data.dtos.requests.LoginRequest;
import com.myloanapp.data.dtos.requests.RegisterRequest;
import com.myloanapp.data.dtos.responses.LoanApplicationResponse;
import com.myloanapp.data.models.RepaymentPlan;
import com.myloanapp.data.models.Role;
import com.myloanapp.data.models.Status;
import com.myloanapp.data.repositories.LoanApplicationRepository;
import com.myloanapp.data.repositories.UserRepository;
import com.myloanapp.exceptions.LoanNotFoundException;
import com.myloanapp.exceptions.UserNotFoundException;
import com.myloanapp.exceptions.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;


    private RegisterRequest request;
    private RegisterRequest request2;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    private LoanApplicationRequest loanApplicationRequest;
    private LoanApplicationResponse loanApplicationResponse;

//    private RegisterResponse userResponse;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        request = RegisterRequest.builder()
                .firstName("Amos")
                .lastName("Oneh")
                .email("amoskhaled@gmail.com")
                .phoneNumber("08034612225")
                .password("diego5000")
                .role("customer")
                .build();
        request2 = RegisterRequest.builder()
                .firstName("Amos")
                .lastName("Oneh")
                .email("khaled@gmail.com")
                .phoneNumber("08034612225")
                .password("diego5000")
                .role("loan_officer")
                .build();

        loginRequest = LoginRequest.builder().email("amoskhaled@gmail.com")
                .password("diego5000").build();

        loanApplicationRequest = LoanApplicationRequest.builder()
                .purposeForLoan("Trade")
                .loanAmount(3000)
                .repaymentPlan("annually")
                .build();
        userRepository.deleteAll();
        loanApplicationRepository.deleteAll();

    }

    @Test
    void register() throws UserAlreadyExistException {
        var response = userService.register(request);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isNotNull();

    }


    @Test
    void login() throws UserAlreadyExistException, UserNotFoundException {
        var response = userService.register(request);
        var loginResponse = userService.login(loginRequest);
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getUser()).isNotNull();
        assertThat(loginResponse.getUser().getFirstName()).isEqualTo("Amos");
    }

    @Test
    void applyForLoan() throws UserNotFoundException, UserAlreadyExistException {
        var userResponse  = userService.register(request);
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        assertThat(userResponse.getUserId()).isNotNull();
        assertNotNull(loanApplicationResponse);
        assertThat(loanApplicationResponse.getId()).isNotNull();
        assertThat(loanApplicationResponse.getMessage()).isNotNull();
        assertThat(loanApplicationResponse.getLoanAmount()).isEqualTo(3000);
    }

    @Test
    void viewLoanStatus() throws LoanNotFoundException, UserAlreadyExistException, UserNotFoundException {
        var user = userService.register(request);
        var response = userService.applyForLoan(loanApplicationRequest, user.getUserId());
        var loanStatus = userService.viewLoanApplicationStatus(response.getId());
        assertThrows(LoanNotFoundException.class, ()-> userService.viewLoanApplicationStatus("232"));
//        log.info("{}", loanStatus);
        assertThat(loanStatus).isNotNull();
        assertThat(loanStatus.getLoanStatus()).isNotNull();
        assertThat(loanStatus.getLoanStatus()).isEqualTo(Status.valueOf("PENDING"));
    }

    @Test
    void viewLoanApplicationStatus() throws LoanNotFoundException, UserNotFoundException, UserAlreadyExistException {
        var userResponse = userService.register(request);
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var loans = userService.viewLoanApplicationStatus(loanApplicationResponse.getId());
        assertThat(loans).isNotNull();
        assertThat(loans.getLoanStatus()).isNotNull();
        assertThat(loans.getMessage()).isNotNull();
    }

    @Test
    void viewAllCustomerLoanApplication() throws UserNotFoundException, UserAlreadyExistException {
        var userResponse = userService.register(request);
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var loans = userService.viewAllLoanApplication(userResponse.getUserId());
        assertThat(loans).isNotNull();
        assertThat(loans.size()).isGreaterThan(1);
        assertThat(loans.size()).isEqualTo(3);
        assertThat(loans.get(0).getCustomerId()).isEqualTo(userResponse.getUserId());
    }


    @Test
    void approveLoanApplication() throws UserAlreadyExistException, UserNotFoundException, LoanNotFoundException {
        var userResponse = userService.register(request);
        var user = userRepository.findUserById(userResponse.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var response = userService.approveLoanApplication(loanApplicationResponse.getId(), userResponse.getUserId());
        log.info("{}", response);
        assertThat(userResponse).isNotNull();
        assertThat(user).isNotNull();
        assertThat(user.getRole().equals(Role.LOAN_OFFICER));
        assertThat(response.contains("APPROVED"));
        assertThat(loanApplicationResponse).isNotNull();
    }

    @Test
    void rejectLoanApplication() throws UserAlreadyExistException, UserNotFoundException, LoanNotFoundException {
        var userResponse = userService.register(request);
        var user = userRepository.findUserById(userResponse.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var response = userService.rejectLoanApplication(loanApplicationResponse.getId(), userResponse.getUserId());
        log.info("{}", response);
        assertThat(userResponse).isNotNull();
        assertThat(user).isNotNull();
        assertThat(user.getRole().equals(Role.LOAN_OFFICER));
        assertThat(response.contains("REJECTED"));
        assertThat(loanApplicationResponse).isNotNull();
    }

    @Test
    void generateLoanAgreement() throws UserAlreadyExistException, UserNotFoundException, LoanNotFoundException {
        var userResponse = userService.register(request);
        var userResponse2 = userService.register(request2);
        var user = userRepository.findUserById(userResponse2.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var response = userService.approveLoanApplication(loanApplicationResponse.getId(), userResponse.getUserId());
        userService.generateLoanAgreement(loanApplicationResponse.getId(), user.getId());
        var loan = loanApplicationRepository.findLoanApplicationById(loanApplicationResponse.getId());
        assertThat(loan).isNotNull();
        assertThat(loan.getLoanAgreement()).isNotNull();
        assertThat(user.getRole().equals(Role.LOAN_OFFICER));
        assertThat(response.contains("APPROVED"));
        assertThat(loanApplicationResponse).isNotNull();
    }


    @Test
    void viewLoanAgreement() throws UserAlreadyExistException, UserNotFoundException, LoanNotFoundException {
        var userResponse = userService.register(request);
        var userResponse2 = userService.register(request2);
        var user = userRepository.findUserById(userResponse2.getUserId());
        loanApplicationResponse = userService.applyForLoan(loanApplicationRequest, userResponse.getUserId());
        var loan = loanApplicationRepository.findLoanApplicationById(loanApplicationResponse.getId());
        var response = userService.approveLoanApplication(loan.getId(), userResponse.getUserId());
        userService.generateLoanAgreement(loan.getId(), user.getId());
        var loanAgreement = userService.viewLoanAgreement(loan.getId());
        log.info("{}", loan.getLoanAgreement());
        log.info("{}",loan);
        assertThat(loanAgreement).isNotNull();
        assertThat(loanAgreement.contains("APPROVED"));
        assertThat(user.getRole().equals(Role.LOAN_OFFICER));
        assertThat(response.contains("APPROVED"));
    }
}