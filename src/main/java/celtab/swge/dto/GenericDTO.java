package celtab.swge.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public abstract class GenericDTO<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;
}
