package wow.commons.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2019-07-22
 */
public final class Money implements Comparable<Money> {
	private final int coppers;

	public static final Money ZERO = new Money(0);
	public static final Money MAX_MONEY = new Money(Integer.MAX_VALUE);

	private static final Pattern PATTERN = Pattern.compile("^([+-])?(\\d{1,}g)?(\\d{1,2}s)?(\\d{1,2}c)?$");

	public static Money parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.equals("0")) {
			return ZERO;
		}
		Matcher matcher = PATTERN.matcher(value);
		if (matcher.find()) {
			String signStr = matcher.group(1);
			String gStr = matcher.group(2);
			String sStr = matcher.group(3);
			String cStr = matcher.group(4);

			int sign = "-".equals(signStr) ? -1 : 1;
			int g = gStr != null ? Integer.parseInt(gStr.replace("g", "")) : 0;
			int s = sStr != null ? Integer.parseInt(sStr.replace("s", "")) : 0;
			int c = cStr != null ? Integer.parseInt(cStr.replace("c", "")) : 0;

			return new Money(sign * (g * 100_00 + s * 100 + c));
		} else {
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}

	public static Money fromCoppers(int value) {
		return new Money(value);
	}

	private Money(int coppers) {
		this.coppers = coppers;
	}

	public int getG() {
		return Math.abs(coppers) / 100_00;
	}

	public int getS() {
		return (Math.abs(coppers) / 100) % 100;
	}

	public int getC() {
		return Math.abs(coppers) % 100;
	}

	public boolean isZero() {
		return coppers == 0;
	}

	public boolean isPositive() {
		return coppers > 0;
	}

	public boolean isNegative() {
		return coppers < 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Money money = (Money) o;
		return coppers == money.coppers;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coppers);
	}

	@Override
	public String toString() {
		if (isZero()) {
			return "0";
		}
		return (isNegative() ? "-" : "") +
				(getG() != 0 ? getG() + "g" : "") +
				(getS() != 0 ? getS() + "s" : "") +
				(getC() != 0 ? getC() + "c" : "");
	}

	@Override
	public int compareTo(Money money) {
		return coppers - money.coppers;
	}

	public Money negate() {
		return new Money(-this.coppers);
	}

	public Money abs() {
		return new Money(Math.abs(this.coppers));
	}

	public Money add(Money money) {
		return new Money(this.coppers + money.coppers);
	}

	public Money subtract(Money money) {
		return new Money(this.coppers - money.coppers);
	}

	public Money multiply(int multiplier) {
		return new Money(this.coppers * multiplier);
	}

	public Money multiply(double multiplier) {
		return new Money((int)(this.coppers * multiplier));
	}

	public Money divide(int divisor) {
		return new Money(this.coppers / divisor);
	}

	public Money divide(double divisor) {
		return new Money((int)(this.coppers / divisor));
	}

	public double ratio(Money money) {
		if (money.coppers == 0) {
			return 0;
		}
		return this.coppers / (double)money.coppers;
	}

	public Money min(Money value) {
		return this.compareTo(value) < 0 ? this : value;
	}

	public Money max(Money value) {
		return this.compareTo(value) > 0 ? this : value;
	}

	public Money roundToSilvers() {
		return new Money(100 * (int)Math.round(coppers / 100.0));
	}
}
