package celtab.swge.model.registration.pagseguro;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Amount {
    private Integer value;
    private String currency;
}
