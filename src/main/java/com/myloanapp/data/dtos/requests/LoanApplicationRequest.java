package com.myloanapp.data.dtos.requests;

import com.myloanapp.data.models.RepaymentPlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanApplicationRequest {
    private int loanAmount;
    private String purposeForLoan;
    private String repaymentPlan;
//    private String customerId;
//    private String loanStatus;
}
