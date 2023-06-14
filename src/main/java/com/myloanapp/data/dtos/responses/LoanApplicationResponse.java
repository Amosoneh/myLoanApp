package com.myloanapp.data.dtos.responses;

import com.myloanapp.data.models.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanApplicationResponse {
    private String id;
    private int loanAmount;
    private String customerId;
    private String message;
    private Status loanStatus = Status.PENDING;

}
