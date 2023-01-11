package celtab.swge.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibge")
@Getter
@Setter
public class IBGEProperties {

    private Long timeout;

}
