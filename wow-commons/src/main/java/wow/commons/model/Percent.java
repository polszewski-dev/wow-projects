package wow.commons.model;

import wow.commons.util.FormatUtil;

/**
 * User: POlszewski
 * Date: 2022-09-25
 */
public record Percent(double value) implements Comparable<Percent> {
	public static final Percent ZERO = new Percent(0);
	public static final Percent _100 = new Percent(100);

	public static Percent of(double value) {
		if (value == 0) {
			return ZERO;
		}
		if (value == 100) {
			return _100;
		}
		return new Percent(value);
	}

	public static Percent ofNullable(double value) {
		return value != 0 ? of(value) : null;
	}

	public static Percent fromMultiplier(double multiplier) {
		double value = (multiplier - 1) * 100;
		return of(value);
	}

	public static Percent parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		if (value.endsWith("%")) {
			return parse(value.substring(0, value.length() - 1));
		}

		return of(Double.parseDouble(value));
	}

	public double getCoefficient() {
		return value() / 100.0;
	}

	public boolean isZero() {
		return value == 0;
	}

	public double toMultiplier() {
		return 1 + value / 100;
	}

	public Percent negate() {
		return of(-value);
	}

	public Percent add(Percent percent) {
		if (this.isZero()) {
			return percent;
		}
		if (percent.isZero()) {
			return this;
		}
		return of(this.value + percent.value);
	}

	public Percent subtract(Percent percent) {
		return of(this.value - percent.value);
	}

	public Percent addMultiplicatively(Percent percent) {
		return fromMultiplier(this.toMultiplier() * percent.toMultiplier());
	}

	public Percent scale(double factor) {
		return of(value * factor);
	}

	@Override
	public String toString() {
		return FormatUtil.decimalPointOnlyIfNecessary(value, "%s%%");
	}

	@Override
	public int compareTo(Percent percent) {
		return Double.compare(this.value, percent.value);
	}
}
