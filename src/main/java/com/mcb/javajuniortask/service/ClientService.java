package com.mcb.javajuniortask.service;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.model.Client;
import com.mcb.javajuniortask.model.Debt;
import com.mcb.javajuniortask.model.Payment;
import com.mcb.javajuniortask.repository.ClientRepository;

import com.mcb.javajuniortask.repository.DebtRepository;
import com.mcb.javajuniortask.repository.PaymentRepository;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ShellComponent
public class ClientService {

  private final ClientRepository clientRepository;
  private final DebtRepository debtRepository;
  private final PaymentRepository paymentRepository;

  public ClientService(ClientRepository clientRepository,
      DebtRepository debtRepository,
      PaymentRepository paymentRepository) {
    this.clientRepository = clientRepository;
    this.debtRepository = debtRepository;
    this.paymentRepository = paymentRepository;
  }

  @ShellMethod("Shows all clients in db")
  @Transactional
  public Iterable<ClientDTO> showAllClients() {
    return StreamSupport.stream(clientRepository.findAll().spliterator(), false).map(client -> {
      ClientDTO clientDTO = new ClientDTO();
      clientDTO.setName(client.getName());
      clientDTO.setId(client.getId());
      BigDecimal totalDebt = client.getDebts().stream().map(Debt::getValue).reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO);

      BigDecimal totalPayed = BigDecimal.ZERO;

      for (Debt i : client.getDebts()) {
        totalPayed = totalPayed.add(
            i.getPayments().stream().map(Payment::getValue).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO));
      }

      clientDTO.setTotalDebt(totalDebt.subtract(totalPayed));

      return clientDTO;
    }).collect(Collectors.toList());
  }

  @ShellMethod("Adds client to db")
  @Transactional
  public UUID addClient(@ShellOption String name) {
    Client client = new Client();
    client.setName(name);
    client.setId(UUID.randomUUID());
    client = clientRepository.save(client);
    return client.getId();
  }

  @ShellMethod("Adds debt to client")
  @Transactional
  public UUID addDebtToClient(@ShellOption UUID clientId, @ShellOption BigDecimal value) {
    Client client = clientRepository.findOne(clientId);
    Debt debt = new Debt();
    debt.setValue(value);
    debt.setId(UUID.randomUUID());
    debt.setClient(client);
    client.getDebts().add(debt);
    clientRepository.save(client);
    return debt.getId();
  }

  @ShellMethod("Repay debt to client")
  @Transactional
  public UUID repayDebtToClient(@ShellOption UUID clientId, @ShellOption UUID debtId,
      @ShellOption BigDecimal value) {
    Debt debt = debtRepository.findOne(debtId);
    if (debt == null) {
      return null;
    }
    Client client = clientRepository.findOne(clientId);
    if (client == null) {
      return null;
    }

    BigDecimal totalPayed = debt.getPayments().stream().map(Payment::getValue)
        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    BigDecimal leftToPay = debt.getValue().subtract(totalPayed);
    BigDecimal moneyToProceed = leftToPay.min(value);

    Payment payment = new Payment();
    payment.setClient(client);
    if (!moneyToProceed.equals(BigDecimal.ZERO)) {
      payment.setId(UUID.randomUUID());
      payment.setValue(moneyToProceed);
      payment.setDebt(debt);
      paymentRepository.save(payment);
    }

    //излишки
    if (moneyToProceed.compareTo(value) < 0) {
      payment.setId(UUID.randomUUID());
      payment.setValue(value.subtract(moneyToProceed));
      payment.setDebt(null);
      paymentRepository.save(payment);
    }

    return payment.getId();
  }
}
