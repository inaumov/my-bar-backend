package mybar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Slf4j
@Service
public class ScriptExecutor {
    @Autowired
    private DataSource dataSource;
    private final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();

    public void runScript(Resource script) {
        log.info("Working with script: {}", script.getFilename());
        executeScript(script);
        log.info("Done with script {}...", script.getFilename());
    }

    public void executeScript(Resource script) {
        try {
            resourceDatabasePopulator.setScripts(script);
            resourceDatabasePopulator.execute(this.dataSource);
        } catch (ScriptException e) {
            log.error("Error executing script, script: {}. Error: {}", script.getFilename(), e.getMessage(), e);
            throw e;
        }
    }

}
