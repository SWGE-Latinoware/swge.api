package celtab.swge.model.registration.pagseguro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class PixCharge {
    @JsonProperty("calendario")
    private HashMap<String, Integer> calendar;
    @JsonProperty("devedor")
    private HashMap<String, String> debtor;
    @JsonProperty("valor")
    private HashMap<String, String> value;
    @JsonProperty("chave")
    private String key;
    @JsonProperty("solicitacaopagador")
    private String payerRequest;
}
