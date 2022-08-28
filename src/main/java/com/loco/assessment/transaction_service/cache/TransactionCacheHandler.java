package com.loco.assessment.transaction_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loco.assessment.transaction_service.utils.TransactionLinkParseUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionCacheHandler {

    final ConcurrentMap<String, Object> cache;

    @Autowired
    ObjectMapper objectMapper;

    public TransactionCacheHandler(){
        cache = new ConcurrentHashMap<>();
    }

    public Long getCachedConnectedSum(Long transactionId){
        Object val = cache.get(transactionId.toString());
        return val == null ? null : objectMapper.convertValue(val,Long.class);
    }

    @Async
    public void updateCachedConnectedSum(String flatPath, Long value){
        List<String> transactionIds = TransactionLinkParseUtil.getTransactionIdsFromLink(flatPath);
        if(transactionIds.isEmpty()){
            transactionIds.forEach(id -> {
                if(cache.containsKey(id)) cache.put(id,objectMapper.convertValue(cache.get(id),Long.class) + value);
            });
        }
    }

    @Async
    public void removeCachedConnectedSum(String flatPath){
        List<String> transactionIds = TransactionLinkParseUtil.getTransactionIdsFromLink(flatPath);
        if(!transactionIds.isEmpty()){
            transactionIds.forEach(id -> {
                if(cache.containsKey(id)) cache.remove(id);
            });
        }
    }

    @Async
    public void insertConnectedSumIntoCache(Long transactionId, Long sum){
        cache.put(transactionId.toString(),sum);
    }

}
