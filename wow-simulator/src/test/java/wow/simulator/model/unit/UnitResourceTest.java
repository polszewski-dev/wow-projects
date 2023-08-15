package wow.simulator.model.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;

import static org.assertj.core.api.Assertions.*;
import static wow.commons.model.spells.ResourceType.HEALTH;
import static wow.commons.model.spells.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
class UnitResourceTest extends WowSimulatorSpringTest {
	@Test
	void set() {
		assertThat(resource.getType()).isEqualTo(HEALTH);
		assertThat(resource.getCurrent()).isZero();
		assertThat(resource.getMax()).isZero();

		resource.set(500, 1000);

		assertThat(resource.getCurrent()).isEqualTo(500);
		assertThat(resource.getMax()).isEqualTo(1000);
	}

	@Test
	void increase() {
		resource.set(500, 1000);

		int actualAmount1 = resource.increase(300, null);

		assertThat(resource.getCurrent()).isEqualTo(800);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resource.increase(300, null);

		assertThat(resource.getCurrent()).isEqualTo(1000);
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void decrease() {
		resource.set(500, 1000);

		int actualAmount1 = resource.decrease(300, null);

		assertThat(resource.getCurrent()).isEqualTo(200);
		assertThat(actualAmount1).isEqualTo(300);

		int actualAmount2 = resource.decrease(300, null);

		assertThat(resource.getCurrent()).isZero();
		assertThat(actualAmount2).isEqualTo(200);
	}

	@Test
	void canPayHealth() {
		var health = new UnitResource(HEALTH, player);

		health.set(500, 1000);

		assertThat(health.canPay(0)).isTrue();
		assertThat(health.canPay(100)).isTrue();
		assertThat(health.canPay(499)).isTrue();
		assertThat(health.canPay(500)).isFalse();
		assertThat(health.canPay(501)).isFalse();
		assertThat(health.canPay(1000)).isFalse();
	}

	@Test
	void canPayMana() {
		var mana = new UnitResource(MANA, player);

		mana.set(500, 1000);

		assertThat(mana.canPay(0)).isTrue();
		assertThat(mana.canPay(100)).isTrue();
		assertThat(mana.canPay(499)).isTrue();
		assertThat(mana.canPay(500)).isTrue();
		assertThat(mana.canPay(501)).isFalse();
		assertThat(mana.canPay(1000)).isFalse();
	}

	@Test
	void payHealth() {
		var health = new UnitResource(HEALTH, player);

		health.set(500, 1000);

		assertThatNoException().isThrownBy(() -> health.pay(250, null));
		assertThat(health.getCurrent()).isEqualTo(250);
		assertThatThrownBy(() -> health.pay(250, null)).isInstanceOf(IllegalArgumentException.class);
		assertThat(health.getCurrent()).isEqualTo(250);
	}

	@Test
	void payMana() {
		var health = new UnitResource(MANA, player);

		health.set(500, 1000);

		assertThatNoException().isThrownBy(() -> health.pay(250, null));
		assertThat(health.getCurrent()).isEqualTo(250);
		assertThatNoException().isThrownBy(() -> health.pay(250, null));
		assertThat(health.getCurrent()).isZero();
	}

	UnitResource resource;

	@BeforeEach
	void setUp() {
		setupTestObjects();
		resource = new UnitResource(HEALTH, player);
	}
}