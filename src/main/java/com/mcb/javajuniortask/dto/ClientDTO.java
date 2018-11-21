package com.mcb.javajuniortask.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ClientDTO {

  private UUID id;
  private String name;
  private BigDecimal totalDebt;

  public void setName(String name) {
    this.name = name;
  }

  public void setTotalDebt(BigDecimal totalDebt) {
    this.totalDebt = totalDebt;
  }

  public BigDecimal getTotalDebt() {
    return totalDebt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
