package wow.commons.model.pve;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-02-01
 */
public class Boss implements Comparable<Boss> {
	private final int no;
	private final String name;
	private final Instance instance;

	public Boss(int no, String name, Instance instance) {
		this.no = no;
		this.name = name;
		this.instance = instance;
	}

	public int getNo() {
		return no;
	}

	public String getName() {
		return name;
	}

	public Instance getInstance() {
		return instance;
	}

	@Override
	public int compareTo(Boss o) {
		return this.no - o.no;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Boss)) return false;
		Boss boss = (Boss) o;
		return name.equals(boss.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}
}
