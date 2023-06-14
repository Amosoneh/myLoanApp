package com.myloanapp.data.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document("Loan Applications")
public class LoanApplication {
    @Id
    private String id;
    private String customerId;
    private int loanAmount;
    private String purposeForLoan;
    private LocalDateTime dateTime = LocalDateTime.now();
    private RepaymentPlan repaymentPlan;
    private Status loanStatus;
    private String loanAgreement;

}
