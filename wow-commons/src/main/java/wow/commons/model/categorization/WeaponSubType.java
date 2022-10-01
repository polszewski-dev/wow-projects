package wow.commons.model.categorization;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum WeaponSubType implements ItemSubType {
	Dagger("Dagger"),
	Sword("Sword"),
	Axe("Axe"),
	Polearm("Polearm"),
	Mace("Mace"),
	Staff("Staff"),
	FistWeapon("Fist Weapon"),

	HeldInOffHand("Held In Off-hand"),
	Shield("Shield"),

	Wand("Wand"),
	Gun("Gun"),
	Bow("Bow"),
	Crossbow("Crossbow"),
	Thrown("Thrown"),

	Totem("Totem"),
	Libram("Libram"),
	Idol("Idol"),

	;

	private final String tooltipText;

	WeaponSubType(String tooltipText) {
		this.tooltipText = tooltipText;
	}

	public static WeaponSubType tryParse(String line) {
		return Stream.of(values()).filter(x -> x.tooltipText.equalsIgnoreCase(line)).findAny().orElse(null);
	}

	@Override
	public String toString() {
		return tooltipText;
	}
}
