package wow.commons.model.pve;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public class Faction implements Comparable<Faction> {
	private final int no;
	private final String name;
	private final GameVersion version;
	private final Phase phase;

	public Faction(int no, String name, GameVersion version, Phase phase) {
		this.no = no;
		this.name = name;
		this.version = version;
		this.phase = phase;
	}

	public int getNo() {
		return no;
	}

	public String getName() {
		return name;
	}

	public GameVersion getVersion() {
		return version;
	}

	public Phase getPhase() {
		return phase;
	}

	@Override
	public int compareTo(Faction o) {
		return this.no - o.no;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Faction)) return false;
		Faction faction = (Faction) o;
		return name.equals(faction.name);
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
