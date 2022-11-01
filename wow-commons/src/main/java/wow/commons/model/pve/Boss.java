package wow.commons.model.pve;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-02-01
 */
public class Boss {
	private final int id;
	private final String name;
	private final List<Zone> zones;

	public Boss(int id, String name, List<Zone> zones) {
		this.id = id;
		this.name = name;
		this.zones = zones;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Zone> getZones() {
		return zones;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Boss)) return false;
		Boss boss = (Boss) o;
		return id == boss.id;
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
