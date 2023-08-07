package wow.simulator.model.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spells.Cost;
import wow.simulator.WowSimulatorSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.commons.model.spells.ResourceType.HEALTH;
import static wow.commons.model.spells.ResourceType.MANA;

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

		resources.increaseHealth(300, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(800);

		resources.increaseHealth(300, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(1000);
	}

	@Test
	void decreaseHealth() {
		resources.setHealth(500, 1000);

		resources.decreaseHealth(300, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(200);

		resources.decreaseHealth(300, null);

		assertThat(resources.getCurrentHealth()).isZero();
	}

	@Test
	void increaseMana() {
		resources.setMana(500, 1000);

		resources.increaseMana(300, null);

		assertThat(resources.getCurrentMana()).isEqualTo(800);

		resources.increaseMana(300, null);

		assertThat(resources.getCurrentMana()).isEqualTo(1000);
	}

	@Test
	void decreaseMana() {
		resources.setMana(500, 1000);

		resources.decreaseMana(300, null);

		assertThat(resources.getCurrentMana()).isEqualTo(200);

		resources.decreaseMana(300, null);

		assertThat(resources.getCurrentMana()).isZero();
	}

	@Test
	void canPay() {
		resources.setHealth(500, 1000);
		resources.setMana(500, 1000);

		var costs1 = List.of(
			new Cost(HEALTH, 100),
			new Cost(MANA, 200)
		);

		assertThat(resources.canPay(costs1)).isTrue();

		var costs2 = List.of(
				new Cost(HEALTH, 100),
				new Cost(MANA, 600)
		);

		assertThat(resources.canPay(costs2)).isFalse();

		var costs3 = List.of(
				new Cost(HEALTH, 500),
				new Cost(MANA, 500)
		);

		assertThat(resources.canPay(costs3)).isFalse();

		var costs4 = List.of(
				new Cost(HEALTH, 499),
				new Cost(MANA, 500)
		);

		assertThat(resources.canPay(costs4)).isTrue();
	}

	@Test
	void pay() {
		resources.setHealth(500, 1000);
		resources.setMana(500, 1000);

		var costs1 = List.of(
				new Cost(HEALTH, 200),
				new Cost(MANA, 100)
		);

		resources.pay(costs1, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(300);
		assertThat(resources.getCurrentMana()).isEqualTo(400);

		resources.pay(costs1, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(100);
		assertThat(resources.getCurrentMana()).isEqualTo(300);

		assertThatThrownBy(() -> resources.pay(costs1, null)).isInstanceOf(IllegalArgumentException.class);

		var costs2 = List.of(
				new Cost(HEALTH, 100),
				new Cost(MANA, 300)
		);

		assertThatThrownBy(() -> resources.pay(costs2, null)).isInstanceOf(IllegalArgumentException.class);

		var costs3 = List.of(
				new Cost(HEALTH, 99),
				new Cost(MANA, 300)
		);

		resources.pay(costs3, null);

		assertThat(resources.getCurrentHealth()).isEqualTo(1);
		assertThat(resources.getCurrentMana()).isZero();
	}

	UnitResources resources;

	@BeforeEach
	void setUp() {
		setupTestObjects();
		resources = new UnitResources(player);
	}
}