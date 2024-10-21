package wow.simulator.model.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spell.Cost;
import wow.simulator.WowSimulatorSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
class UnitResourcesTest extends WowSimulatorSpringTest {
	@Test
	void getCurrentHealth() {
		resources.setHealth(500, 1000);

		assertThat(resources.getCurrentHealth()).isEqualTo(500);
	}

	@Test
	void getCurrentMana() {
		resources.setMana(500, 1000);

		assertThat(resources.getCurrentMana()).isEqualTo(500);
	}

	@Test
	void increaseHealth() {
		resources.setHealth(500, 1000);

		int actualAmount1 = resources.increaseHealth(300, false, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(800);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resources.increaseHealth(300, false, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(1000);
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void decreaseHealth() {
		resources.setHealth(500, 1000);

		int actualAmount1 = resources.decreaseHealth(300, false, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(200);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resources.decreaseHealth(300, false, null);

		assertThat(resources.getCurrentHealth()).isZero();
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void increaseMana() {
		resources.setMana(500, 1000);

		int actualAmount1 = resources.increaseMana(300, false, null);

		assertThat(resources.getCurrentMana()).isEqualTo(800);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resources.increaseMana(300, false, null);

		assertThat(resources.getCurrentMana()).isEqualTo(1000);
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void decreaseMana() {
		resources.setMana(500, 1000);

		int actualAmount1 = resources.decreaseMana(300, false, null);

		assertThat(resources.getCurrentMana()).isEqualTo(200);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resources.decreaseMana(300, false, null);

		assertThat(resources.getCurrentMana()).isZero();
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void canPayHealth() {
		resources.setHealth(500, 1000);

		Cost cost1 = new Cost(HEALTH, 100);
		Cost cost2 = new Cost(HEALTH, 499);
		Cost cost3 = new Cost(HEALTH, 500);

		assertThat(resources.canPay(cost1)).isTrue();
		assertThat(resources.canPay(cost2)).isTrue();
		assertThat(resources.canPay(cost3)).isFalse();
	}

	@Test
	void canPayMana() {
		resources.setMana(500, 1000);

		Cost cost1 = new Cost(MANA, 200);
		Cost cost2 = new Cost(MANA, 500);
		Cost cost3 = new Cost(MANA, 600);

		assertThat(resources.canPay(cost1)).isTrue();
		assertThat(resources.canPay(cost2)).isTrue();
		assertThat(resources.canPay(cost3)).isFalse();
	}

	@Test
	void payHealth() {
		resources.setHealth(500, 1000);

		Cost cost1 = new Cost(HEALTH, 200);
		Cost cost2 = new Cost(HEALTH, 100);
		Cost cost3 = new Cost(HEALTH, 99);

		resources.pay(cost1, null);
		assertThat(resources.getCurrentHealth()).isEqualTo(300);

		resources.pay(cost1, null);
		assertThat(resources.getCurrentHealth()).isEqualTo(100);

		assertThatThrownBy(() -> resources.pay(cost2, null)).isInstanceOf(IllegalArgumentException.class);

		resources.pay(cost3, null);
		assertThat(resources.getCurrentHealth()).isEqualTo(1);
	}

	@Test
	void payMana() {
		resources.setMana(500, 1000);

		Cost cost1 = new Cost(MANA, 100);
		Cost cost2 = new Cost(MANA, 300);

		resources.pay(cost1, null);
		assertThat(resources.getCurrentMana()).isEqualTo(400);

		resources.pay(cost1, null);
		assertThat(resources.getCurrentMana()).isEqualTo(300);

		resources.pay(cost2, null);
		assertThat(resources.getCurrentMana()).isZero();
	}

	UnitResources resources;

	@BeforeEach
	void setUp() {
		setupTestObjects();
		resources = new UnitResources(player);
	}
}