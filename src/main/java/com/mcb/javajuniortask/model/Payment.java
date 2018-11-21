package com.mcb.javajuniortask.model;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Payment {

  @Id
  private UUID id;
  private BigDecimal value;

  @ManyToOne
  @JoinColumn(name = "dept_id")
  private Debt debt;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private Client client;

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setDebt(Debt debt) {
    this.debt = debt;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
