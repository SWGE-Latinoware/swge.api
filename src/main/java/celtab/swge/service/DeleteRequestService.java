package celtab.swge.service;

import celtab.swge.model.user.DeleteRequest;
import celtab.swge.repository.DeleteRequestRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class DeleteRequestService extends GenericService<DeleteRequest, Long> {

    public DeleteRequestService(DeleteRequestRepository deleteRequestRepository) {
        super(deleteRequestRepository, "delete-request(s)", new GenericSpecification<>(DeleteRequest.class));
    }
}
