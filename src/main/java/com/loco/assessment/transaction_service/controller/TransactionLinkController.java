
package com.loco.assessment.transaction_service.controller;

import com.loco.assessment.transaction_service.service.TransactionLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        path = {"/transactionLinkService"}
)
public class TransactionLinkController {
    private final TransactionLinkService transactionLinkService;

    @GetMapping({"/links/bulk"})
    public ResponseEntity<?> getAllTransactionLinks(@RequestParam(name = "pageSize",required = false) Integer pageSize, @RequestParam(name = "pageNumber",required = false) Integer pageNumber) {
        return ResponseEntity.ok(this.transactionLinkService.getAllLinks(pageSize, pageNumber));
    }

    public TransactionLinkController(final TransactionLinkService transactionLinkService) {
        this.transactionLinkService = transactionLinkService;
    }
}
