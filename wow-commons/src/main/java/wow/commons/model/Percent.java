package wow.commons.model;

/**
 * User: POlszewski
 * Date: 2022-09-25
 */
public final class Percent implements Comparable<Percent> {
	public static final Percent ZERO = new Percent(0);
	public static final Percent _100 = new Percent(100);

	private final double value;

	private Percent(double value) {
		this.value = value;
	}

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

	public double getCoefficient() {
		return getValue() / 100.0;
	}

	public boolean isZero() {
		return value == 0;
	}

	public double getValue() {
		return value;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Percent percent = (Percent) o;
		return Double.compare(percent.value, value) == 0;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public String toString() {
		if (value % 1 == 0) {
			return (int)value + "%";
		} else {
			return String.format("%.2f%%", value);
		}
	}

	@Override
	public int compareTo(Percent percent) {
		return Double.compare(this.value, percent.value);
	}
}