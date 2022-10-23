package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.Item;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.parsers.stats.StatMatcher;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ItemStatParser {
	private final StatParser parser;

	public ItemStatParser() {
		this.parser = StatPatternRepository.getInstance().getItemStatParser();
	}

	public boolean tryParse(String line) {
		return parser.tryParse(line);
	}

	public void setItemStats(Item item) {
		get("RequiredLevel").setMatched(item::setRequiredLevel);
		get("ItemLevel").setMatched(item::setItemLevel);
		item.setWeaponStats(getWeaponStats());
		item.setStats(getParsedStats());
		get("Projectile").setMatched(x -> {});//ignore
	}

	public Attributes getParsedStats() {
		return parser.getParsedStats();
	}

	public void checkForNotUsedMatches() {
		parser.checkForNotUsedMatches();
	}

	private WeaponStats getWeaponStats() {
		WeaponStats result = null;
		StatMatcher weaponDamage = get("WeaponDamage");
		if (weaponDamage.hasMatch()) {
			result = new WeaponStats();
			String damageType;
			if (weaponDamage.groupCount() == 3) {
				result.setWeaponDamageMin(weaponDamage.getInt(1));
				result.setWeaponDamageMax(weaponDamage.getInt(2));
				damageType = weaponDamage.getString(3);
			} else {
				result.setWeaponDamageMin(weaponDamage.getInt(1));
				result.setWeaponDamageMax(weaponDamage.getInt(1));
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
			get("WeaponExtraDamage").getInt();
		}
		return result;
	}

	private StatMatcher get(String alias) {
		return parser.getByAlias(alias);
	}
}
