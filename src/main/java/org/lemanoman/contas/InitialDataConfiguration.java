package org.lemanoman.contas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class InitialDataConfiguration {

    @Autowired
    private DatabaseService databaseService;

    @PostConstruct
    public void postConstruct() {
        databaseService.resetDatabase();
    }


}
