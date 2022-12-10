package wow.commons.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.commons.model.Duration.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
class DurationTest {
	@Test
	void parse() {
		assertThat(Duration.parse(null)).isNull();
		assertThat(Duration.parse("0").getSeconds()).isZero();
		assertThat(Duration.parse("2").getSeconds()).isEqualTo(2);
		assertThat(Duration.parse("2.0").getSeconds()).isEqualTo(2);
		assertThat(Duration.parse("2.5").getSeconds()).isEqualTo(2.5);
		assertThat(Duration.parse("15").getSeconds()).isEqualTo(15);
		assertThat(Duration.parse("25ms").getSeconds()).isEqualTo(0.025);
		assertThat(Duration.parse("25s").getSeconds()).isEqualTo(25);
		assertThat(Duration.parse("25m").getSeconds()).isEqualTo(25 * 60);
		assertThat(Duration.parse("25h").getSeconds()).isEqualTo(25 * 60 * 60);
	}

	@Test
	void add() {
		Duration nearInf = millis(Integer.MAX_VALUE - 1000);

		Duration one = seconds(1);
		Duration two = seconds(2);
		Duration three = seconds(3);
		Duration five = seconds(5);
		Duration ten = seconds(10);

		assertThat(two.add(three)).isEqualTo(five);
		assertThat(three.add(two)).isEqualTo(five);

		assertThat(INFINITE.add(three)).isEqualTo(INFINITE);
		assertThat(two.add(INFINITE)).isEqualTo(INFINITE);
		assertThat(INFINITE.add(INFINITE)).isEqualTo(INFINITE);

		assertThat(nearInf.add(one)).isEqualTo(INFINITE);
		assertThat(nearInf.add(ten)).isEqualTo(INFINITE);
		assertThat(nearInf.add(INFINITE)).isEqualTo(INFINITE);
	}

	@Test
	void subtract() {
		Duration two = seconds(2);
		Duration three = seconds(3);
		Duration five = seconds(5);

		assertThat(five.subtract(two)).isEqualTo(three);
		assertThatThrownBy(() -> two.subtract(five)).isInstanceOf(IllegalArgumentException.class);

		assertThat(INFINITE.subtract(two)).isEqualTo(INFINITE);
		assertThatThrownBy(() -> two.subtract(INFINITE)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> INFINITE.subtract(INFINITE)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void multiplyBy() {
		Duration zero = seconds(0);
		Duration five = seconds(5);
		Duration twelveAndHalf = seconds(12.5);

		assertThat(five.multiplyBy(2.5)).isEqualTo(twelveAndHalf);
		assertThat(five.multiplyBy(0)).isEqualTo(zero);
		assertThatThrownBy(() -> five.multiplyBy(-2.5)).isInstanceOf(IllegalArgumentException.class);

		assertThat(INFINITE.multiplyBy(2.5)).isEqualTo(INFINITE);
		assertThat(INFINITE.multiplyBy(0)).isEqualTo(zero);
		assertThatThrownBy(() -> INFINITE.multiplyBy(-2.5)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void divideByFactor() {
		Duration zero = seconds(0);
		Duration twoAndHalf = seconds(2.5);
		Duration five = seconds(5);

		assertThat(five.divideBy(2)).isEqualTo(twoAndHalf);
		assertThat(five.divideBy(0)).isEqualTo(INFINITE);
		assertThat(zero.divideBy(5)).isEqualTo(zero);
		assertThatThrownBy(() -> five.divideBy(-2.5)).isInstanceOf(IllegalArgumentException.class);

		assertThat(INFINITE.divideBy(2.5)).isEqualTo(INFINITE);
		assertThat(INFINITE.divideBy(0)).isEqualTo(INFINITE);
		assertThatThrownBy(() -> INFINITE.divideBy(-2.5)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void divideByDuration() {
		Duration zero = seconds(0);
		Duration two = seconds(2);
		Duration five = seconds(5);

		assertThat(five.divideBy(two)).isEqualTo(2.5);
		assertThat(zero.divideBy(two)).isEqualTo(0.0);
		assertThatThrownBy(() -> five.divideBy(zero)).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> INFINITE.divideBy(two)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> INFINITE.divideBy(zero)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> INFINITE.divideBy(INFINITE)).isInstanceOf(IllegalArgumentException.class);
		assertThat(five.divideBy(INFINITE)).isZero();
	}

	@Test
	void min() {
		Duration two = seconds(2);
		Duration three = seconds(3);

		assertThat(two.min(three)).isEqualTo(two);
		assertThat(three.min(two)).isEqualTo(two);

		assertThat(INFINITE.min(three)).isEqualTo(three);
		assertThat(three.min(INFINITE)).isEqualTo(three);
		assertThat(INFINITE.min(INFINITE)).isEqualTo(INFINITE);
	}

	@Test
	void max() {
		Duration two = seconds(2);
		Duration three = seconds(3);

		assertThat(two.max(three)).isEqualTo(three);
		assertThat(three.max(two)).isEqualTo(three);

		assertThat(INFINITE.max(three)).isEqualTo(INFINITE);
		assertThat(three.max(INFINITE)).isEqualTo(INFINITE);
		assertThat(INFINITE.max(INFINITE)).isEqualTo(INFINITE);
	}
}