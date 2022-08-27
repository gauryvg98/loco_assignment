package com.loco.assessment.transaction_service.controller;

import com.loco.assessment.transaction_service.pojo.TransactionEntry;
import com.loco.assessment.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        path = {"/transactionService"}
)
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {
    final TransactionService transactionService;

    @PutMapping({"/transaction/{transactionId}"})
    public ResponseEntity saveTransaction(@PathVariable(name = "transactionId") Long transactionId, @RequestBody TransactionEntry transactionEntry) throws Exception {
        this.transactionService.saveTransaction(transactionEntry, transactionId);
        return ResponseEntity.ok(null);
    }

    @GetMapping({"/transaction/{transactionId}"})
    public ResponseEntity<?> getTransaction(@PathVariable(name = "transactionId") Long transactionId) throws Exception {
        return ResponseEntity.ok(this.transactionService.getTransactionById(transactionId));
    }

    @GetMapping({"/types/{type}"})
    public ResponseEntity<?> filterTransactionsOnType(@PathVariable(name = "type") String type) {
        return ResponseEntity.ok(this.transactionService.filterByType(type));
    }

    @GetMapping({"/sum/{transactionId}"})
    public ResponseEntity<?> findSumOfAllConnectedTransactions(@PathVariable(name = "transactionId") Long transactionId) throws Exception {
        return ResponseEntity.ok(this.transactionService.getSumOfConnectedTransactions(transactionId));
    }

    @GetMapping({"/transaction/bulk"})
    public ResponseEntity<?> getAllTransactions(@RequestParam(name = "pageSize",required = false) Integer pageSize, @RequestParam(name = "pageNumber",required = false) Integer pageNumber) {
        return ResponseEntity.ok(this.transactionService.getAllTransactions(pageNumber, pageSize));
    }
}
