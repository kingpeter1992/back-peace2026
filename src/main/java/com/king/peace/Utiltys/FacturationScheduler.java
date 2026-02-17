package com.king.peace.Utiltys;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.king.peace.ImplementServices.FacturationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FacturationScheduler {

    private final FacturationService facturationService;

    // Exécute tous les jours à minuit
    @Scheduled(cron = "0 0 0 * * ?")
    public void runFacturation() {
        facturationService.genererFacturesAutomatiques();
    }
    
}
