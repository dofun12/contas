package org.lemanoman.contas.config;

import org.lemanoman.contas.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InitialDataConfig {

    @Autowired
    private DatabaseService databaseService;

    @PostConstruct
    public void postConstruct() {
        databaseService.resetDatabase(false);
    }


}
