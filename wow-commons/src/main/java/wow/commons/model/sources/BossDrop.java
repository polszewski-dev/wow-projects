package wow.commons.model.sources;

import wow.commons.model.pve.Zone;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class BossDrop extends SourcedFromInstance {
	private final String boss;

	BossDrop(Zone instance, String boss) {
		super(instance);
		this.boss = boss;
	}

	@Override
	public String getBoss() {
		return boss;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BossDrop)) return false;
		BossDrop bossDrop = (BossDrop) o;
		return Objects.equals(boss, bossDrop.boss);
	}

	@Override
	public int hashCode() {
		return Objects.hash(boss);
	}

	@Override
	public String toString() {
		return "Boss: " + boss;
	}
}
