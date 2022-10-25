package wow.commons.model.sources;

import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Phase;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class BossDrop extends SourcedFromInstance {
	private final Boss boss;

	BossDrop(Boss boss, Phase phase) {
		super(boss.getInstance(), phase);
		this.boss = boss;
	}

	@Override
	public Boss getBoss() {
		return boss;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BossDrop bossDrop = (BossDrop) o;
		return boss == bossDrop.boss;
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
