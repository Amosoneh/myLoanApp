//package com.myloanapp.controllers;
//
//import com.myloanapp.data.dtos.requests.LoanApplicationRequest;
//import com.myloanapp.exceptions.LoanNotFoundException;
//import com.myloanapp.exceptions.UserNotFoundException;
//import com.myloanapp.services.CustomerService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/v1/customer")
//public class CustomerController {
//    private final CustomerService customerService;
//    @PostMapping("/applyForLoan")
//    public ResponseEntity<?> applyForLoan(@RequestBody LoanApplicationRequest loanApplicationRequest, String customerId){
//        try {
//            return ResponseEntity.ok(customerService.applyForLoan(loanApplicationRequest, customerId));
//        }catch (UserNotFoundException e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//    @GetMapping("/applicationStatus")
//    public ResponseEntity<?> viewLoanApplicationStatus (@RequestBody String loanApplicationId){
//        try {
//            return ResponseEntity.ok(customerService.viewLoanApplicationStatus(loanApplicationId));
//        }catch (LoanNotFoundException e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/getAllCustomerLoanApplications")
//    public ResponseEntity<?> viewCustomersApplication(@RequestBody String customerId){
//        return ResponseEntity.ok(customerService.viewAllCustomerLoanApplication(customerId));
//    }
//
//}
