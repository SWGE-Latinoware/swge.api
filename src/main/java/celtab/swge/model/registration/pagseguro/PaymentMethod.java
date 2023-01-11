package celtab.swge.model.registration.pagseguro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethod {
    private Card card;
    private String type;
    private Integer installments;
    private Boolean capture;
    @JsonProperty("soft_descriptor")
    private String softDescriptor;
}
