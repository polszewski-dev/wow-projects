package wow.commons.model.pve;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-12-18
 */
public abstract class Instance implements Comparable<Instance> {
	private final int no;
	private final String name;
	private final int partySize;
	private final GameVersion version;
	private final Phase phase;
	private final String shortName;
	private List<Boss> bosses;

	Instance(int no, String name, int partySize, GameVersion version, Phase phase, String shortName) {
		this.no = no;
		this.name = name;
		this.partySize = partySize;
		this.version = version;
		this.phase = phase;
		this.shortName = shortName;
	}

	public int getNo() {
		return no;
	}

	public String getName() {
		return name;
	}

	public int getPartySize() {
		return partySize;
	}

	public GameVersion getVersion() {
		return version;
	}

	public Phase getPhase() {
		return phase;
	}

	public String getShortName() {
		return shortName != null ? shortName : name;
	}

	public List<Boss> getBosses() {
		return bosses;
	}

	public boolean isRaid() {
		return this instanceof Raid;
	}

	public boolean isDungeon() {
		return this instanceof Dungeon;
	}

	@Override
	public int compareTo(Instance o) {
		return this.no - o.no;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Instance)) return false;
		Instance instance = (Instance) o;
		return name.equals(instance.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public void setBosses(List<Boss> bosses) {
		this.bosses = bosses;
	}
}
