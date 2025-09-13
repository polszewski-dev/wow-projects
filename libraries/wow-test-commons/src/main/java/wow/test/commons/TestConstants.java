package wow.test.commons;

import org.assertj.core.data.Offset;

import java.util.Comparator;

/**
 * User: POlszewski
 * Date: 2025-09-13
 */
public interface TestConstants {
	Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);

	Offset<Double> PRECISION = Offset.offset(0.01);
}
