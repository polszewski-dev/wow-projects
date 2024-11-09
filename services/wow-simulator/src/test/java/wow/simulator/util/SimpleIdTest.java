package wow.simulator.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
class SimpleIdTest {
	@Test
	void testEquals() {
		TestId id = new TestId(1);

		assertThat(id)
				.isEqualTo(id)
				.isEqualTo(new TestId(1))
				.isNotEqualTo(new TestId(2));
	}

	@Test
	void testHashCode() {
		Set<TestId> set = new HashSet<>();

		set.add(new TestId(1));

		assertThat(set)
				.contains(new TestId(1))
				.doesNotContain(new TestId(2));
	}

	@Test
	void testToString() {
		TestId id = new TestId(1);

		assertThat(id).hasToString("1");
	}

	static class TestId extends SimpleId {
		TestId(long value) {
			super(value);
		}
	}
}