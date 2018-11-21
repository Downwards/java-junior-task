package com.mcb.javajuniortask;

import com.mcb.javajuniortask.dto.ClientDTO;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.shell.Shell;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(TestApplicationRunner.class)
public class JavaJuniorTaskApplicationTests {

	@Autowired
	private Shell shell;

	@Test
	public void testRegress(){
		UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
		shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));
		shell.evaluate(() -> String.format("add-debt-to-client %s 1000", clientUUID));
		Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("1500.00"), client.getTotalDebt());
      }
    });
	}

	@Test
	public void testPartlyPayment(){
		UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
		UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

		shell.evaluate(() -> String.format("repay-debt-to-client %s %s 100", clientUUID, debt1UUID));
    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 100", clientUUID, debt1UUID));

		Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
		clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("300.00"), client.getTotalDebt());
      }
		});
	}

  @Test
  public void testPartlyPayment2Dept(){
    UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));
    UUID debt2UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 100", clientUUID, debt1UUID));
    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 200",  clientUUID, debt2UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("700.00"), client.getTotalDebt());
      }
    });
  }

	@Test
  public void testEqualPayment(){
    UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 500", clientUUID, debt1UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("0.00"), client.getTotalDebt());
      }
    });
  }

  @Test
  public void testOverPayment(){
    UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 1000", clientUUID, debt1UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("0.00"), client.getTotalDebt());
      }
    });
  }

  @Test
  public void testDoublePayment(){
    UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 500", clientUUID, debt1UUID));
    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 500", clientUUID, debt1UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");
    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("0.00"), client.getTotalDebt());
      }
    });
  }

  @Test
  public void testPartlyOverPayment(){
    UUID clientUUID  = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID  = (UUID) shell.evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 400", clientUUID, debt1UUID));
    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 500", clientUUID, debt1UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");

    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("0.00"), client.getTotalDebt());
      }
    });
  }

  @Test
  public void testZero() {
    UUID clientUUID = (UUID) shell.evaluate(() -> "add-client BestClient");
    UUID debt1UUID = (UUID) shell
        .evaluate(() -> String.format("add-debt-to-client %s 500", clientUUID));

    shell.evaluate(() -> String.format("repay-debt-to-client %s %s 0", clientUUID, debt1UUID));

    Iterable<ClientDTO> clients = (Iterable<ClientDTO>) shell.evaluate(() -> "show-all-clients");

    clients.iterator().forEachRemaining(client -> {
      if(client.getId().equals(clientUUID)) {
        Assert.assertEquals(new BigDecimal("500.00"), client.getTotalDebt());
      }
    });

  }
}
