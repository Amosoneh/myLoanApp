package com.myloanapp.data.repositories;

import com.myloanapp.data.models.LoanApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanApplicationRepository extends MongoRepository<LoanApplication, Long> {
    LoanApplication findLoanApplicationById (String loanApplicationId);
}
