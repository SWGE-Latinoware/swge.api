package celtab.swge.model.registration.pagseguro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardCharge {
    private Amount amount;
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;
    private String referenceId;
    private String description;
}
