package com.mcb.javajuniortask.repository;

import com.mcb.javajuniortask.model.Debt;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends PagingAndSortingRepository<Debt, UUID> {

}
