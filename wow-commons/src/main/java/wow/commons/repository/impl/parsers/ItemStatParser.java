package wow.commons.repository.impl.parsers;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.Item;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.spells.SpellSchool;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ItemStatParser {
	public static void resetAll() {
		StatParserRepository.getInstance().resetAllItemParsers();
	}

	public static boolean tryParse(String line) {
		for (StatParser parser : StatParserRepository.getInstance().getItemStatParsers()) {
			if (parser.tryParse(line)) {
				return true;
			}
		}
		return false;
	}

	public static void setItemStats(Item item) {
		get("RequiredLevel").setMatched(item::setRequiredLevel);
		get("ItemLevel").setMatched(item::setItemLevel);
		item.setWeaponStats(getWeaponStats());

		item.setStats(getParsedStats());

		if (get("Projectile").hasMatch()) {
			get("Projectile").getDouble();
		}
	}

	public static Attributes getParsedStats() {
		AttributesBuilder stats = new AttributesBuilder();
		for (StatParser parser : StatParserRepository.getInstance().getItemStatParsers()) {
			parser.setStat(stats);
		}
		return stats.toAttributes();
	}

	private static WeaponStats getWeaponStats() {
		WeaponStats result = null;
		StatParser weaponDamage = get("WeaponDamage");
		if (weaponDamage.hasMatch()) {
			result = new WeaponStats();
			String damageType;
			if (weaponDamage.groupCount() == 3) {
				result.setWeaponDamageMin(weaponDamage.get(1));
				result.setWeaponDamageMax(weaponDamage.get(2));
				damageType = weaponDamage.getString(3);
			} else {
				result.setWeaponDamageMin(weaponDamage.get(1));
				result.setWeaponDamageMax(weaponDamage.get(1));
				damageType = weaponDamage.getString(2);
			}
			if (damageType != null) {
				result.setDamageType(SpellSchool.parse(damageType));
			}
		}
		if (get("WeaponDps").hasMatch()) {
			if (result == null) {
				result = new WeaponStats();
			}
			result.setWeaponDps(get("WeaponDps").getDouble());
		}
		if (get("WeaponSpeed").hasMatch()) {
			if (result == null) {
				result = new WeaponStats();
			}
			result.setWeaponSpeed(get("WeaponSpeed").getDouble());
		}
		if (get("WeaponExtraDamage").hasMatch()) {
			get("WeaponExtraDamage").get();
		}
		return result;
	}

	private static StatParser get(String alias) {
		return StatParserRepository.getInstance().getByAlias(alias);
	}

	public static void checkForNotUsedMatches() {
		for (StatParser parser : StatParserRepository.getInstance().getItemStatParsers()) {
			if (parser.hasUnusedMatch()) {
				throw new IllegalArgumentException("Not used: " + parser.getMatchedLine());
			}
		}
	}
}
