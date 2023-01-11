package celtab.swge.service;

import celtab.swge.model.Audit;
import celtab.swge.repository.AuditRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class AuditService extends GenericService<Audit, Long> {
    public AuditService(AuditRepository auditRepository) {
        super(auditRepository, "audit(s)", new GenericSpecification<>(Audit.class));
    }
}
