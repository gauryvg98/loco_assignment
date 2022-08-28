package com.loco.assessment.transaction_service.utils;

import com.loco.assessment.transaction_service.model.TransactionEntity;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionLinkParseUtil {

    public static String generateTransactionLinkKey(Long transactionId){
        return "/" + transactionId + "/";
    }

    public static String generateTransactionLinkKey(TransactionEntity parentTransaction){
        String path;
        if (parentTransaction == null) {
            path = null;
        } else if (parentTransaction != null && parentTransaction.getTransactionLink() == null) {
            path = TransactionLinkParseUtil.generateTransactionLinkKey(parentTransaction.getTransactionId());
        } else {
            path = parentTransaction.getTransactionLink().getFlatPath() + parentTransaction.getTransactionId() + "/";
        }

        return path;
    }

    public static List<String> getTransactionIdsFromLink(String link){
        String[] links = link.split("\\/");
        List<String> transactionIds = new ArrayList<>();
        for(int i = 0 ;i < links.length ; i ++){
            if(!StringUtils.isEmpty(links[i])) transactionIds.add(links[i]);
        }
        return transactionIds;
    }
}
