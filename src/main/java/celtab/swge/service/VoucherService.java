package celtab.swge.service;

import celtab.swge.model.Voucher;
import celtab.swge.repository.VoucherRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService extends GenericService<Voucher, Long> {

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        super(voucherRepository, "voucher(s)", new GenericSpecification<>(Voucher.class));
        this.voucherRepository = voucherRepository;
    }

    public Voucher findOneByEditionAndUser(Long editionId, String userEmail) {
        return voucherRepository.findByEditionIdAndUserEmail(editionId, userEmail).orElse(null);
    }

    public List<Voucher> findAllByEdition(Long editionId) {
        return voucherRepository.findAllByEditionId(editionId);
    }
}
