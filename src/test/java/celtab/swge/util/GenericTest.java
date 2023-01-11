package celtab.swge.util;

import celtab.swge.property.FileStorageProperties;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class GenericTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private List<String> getTables() {
        return jdbcTemplate.query(
            "select * from pg_tables where tableowner = 'test' and schemaname = 'public' and tablename != 'flyway_schema_history';",
            (rs, rowNum) -> rs.getString("tablename")
        );
    }

    protected void cleanTables(List<String> tables) {
        var truncate = tables.stream().map(table -> "TRUNCATE TABLE " + table + " restart identity cascade;").collect(Collectors.joining());
        jdbcTemplate.execute(truncate);
        var triggers = tables.stream().map(table -> "ALTER SEQUENCE " + table + "_id_seq RESTART WITH 1;").collect(Collectors.joining());
        jdbcTemplate.execute(triggers);
    }

    protected void cleanFileDir() {
        try {
            var dir = fileStorageProperties.getUploadDir();
            FileSystemUtils.deleteRecursively(new File(dir));
        } catch (Exception e) {
            System.err.println("It was not possible to clean '" + fileStorageProperties.getUploadDir() + "'");
        }
    }

    @AfterEach
    protected void clean() {
        cleanTables(getTables());
        cleanFileDir();
    }
}
