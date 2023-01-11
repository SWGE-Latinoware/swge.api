package celtab.swge.service;

import celtab.swge.model.Notice;
import celtab.swge.repository.NoticeRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends GenericService<Notice, Long> {

    public NoticeService(NoticeRepository noticeRepository) {
        super(noticeRepository, "notice(s)", new GenericSpecification<>(Notice.class));
    }

}
