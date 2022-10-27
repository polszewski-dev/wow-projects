package wow.commons.model.unit;

import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum Race {
	Undead(Side.Horde),
	Orc(Side.Horde),
	Troll(Side.Horde),
	Tauren(Side.Horde),
	BloodElf(Side.Horde),
	;

	private final Side side;

	Race(Side side) {
		this.side = side;
	}

	public static Race parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value);
	}

	public Side getSide() {
		return side;
	}
}
