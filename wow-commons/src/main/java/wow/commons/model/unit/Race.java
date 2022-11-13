package wow.commons.model.unit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.Side;
import wow.commons.util.EnumUtil;

import static wow.commons.model.pve.Side.ALLIANCE;
import static wow.commons.model.pve.Side.HORDE;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
@Getter
public enum Race {
	UNDEAD(HORDE),
	ORC(HORDE),
	TROLL(HORDE),
	TAUREN(HORDE),
	BLOOD_ELF(HORDE),

	HUMAN(ALLIANCE),
	DWARF(ALLIANCE),
	GNOME(ALLIANCE),
	NIGHT_ELF(ALLIANCE),
	DRANEI(ALLIANCE);

	private final Side side;

	public static Race parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
