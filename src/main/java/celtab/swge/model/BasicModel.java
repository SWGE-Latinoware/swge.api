package celtab.swge.model;

import celtab.swge.event.handler.AuditListener;
import celtab.swge.event.handler.FluxEventHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EntityListeners({FluxEventHandler.class, AuditingEntityListener.class, AuditListener.class})
@MappedSuperclass
@Getter
@Setter
public abstract class BasicModel<T extends Number> implements GenericModel<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;

}
