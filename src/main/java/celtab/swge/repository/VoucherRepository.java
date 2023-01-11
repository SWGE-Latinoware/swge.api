package celtab.swge.repository;

import celtab.swge.model.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends GenericRepository<Voucher, Long> {

    Optional<Voucher> findByEditionIdAndUserEmail(Long editionId, String userEmail);

    List<Voucher> findAllByEditionId(Long editionId);
}
