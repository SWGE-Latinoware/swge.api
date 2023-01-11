package celtab.swge.model.registration.pagseguro;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payment {

    private String card;

    private Integer installments;
}
