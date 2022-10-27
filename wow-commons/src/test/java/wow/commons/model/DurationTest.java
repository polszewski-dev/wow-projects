package wow.commons.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
class DurationTest {
	@Test
	void parse() {
		assertThat(Duration.parse(null)).isNull();
		assertThat(Duration.parse("0").getSeconds()).isEqualTo(0);
		assertThat(Duration.parse("15").getSeconds()).isEqualTo(15);
		assertThat(Duration.parse("25ms").getSeconds()).isEqualTo(0.025);
		assertThat(Duration.parse("25s").getSeconds()).isEqualTo(25);
		assertThat(Duration.parse("25m").getSeconds()).isEqualTo(25 * 60);
		assertThat(Duration.parse("25h").getSeconds()).isEqualTo(25 * 60 * 60);
	}
}