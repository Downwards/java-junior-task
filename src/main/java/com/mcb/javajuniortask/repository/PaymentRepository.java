package com.mcb.javajuniortask.repository;

import com.mcb.javajuniortask.model.Payment;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, UUID> {

}
