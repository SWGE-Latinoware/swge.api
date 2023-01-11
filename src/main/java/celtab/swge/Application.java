package celtab.swge;

import celtab.swge.property.FileStorageProperties;
import celtab.swge.property.IBGEProperties;
import celtab.swge.property.JWTProperties;
import celtab.swge.property.OAuth2Properties;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties({
    JWTProperties.class, FileStorageProperties.class, IBGEProperties.class, OAuth2Properties.class
})
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class Application {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
