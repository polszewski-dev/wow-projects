package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.talent.TalentTree;

import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.talent.TalentTree.*;
import static wow.scraper.model.WowheadSpellCategory.Type.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@AllArgsConstructor
@Getter
public enum WowheadSpellCategory {
	ABILITIES_WARLOCK_AFFLI("spells/abilities/warlock/affliction", ABILITY, WARLOCK, AFFLICTION),
	ABILITIES_WARLOCK_DEMO("spells/abilities/warlock/demonology", ABILITY, WARLOCK, DEMONOLOGY),
	ABILITIES_WARLOCK_DESTRO("spells/abilities/warlock/destruction", ABILITY, WARLOCK, DESTRUCTION),
	TALENTS_WARLOCK_AFFLI("spells/talents/warlock/affliction", TALENT, WARLOCK, AFFLICTION),
	TALENTS_WARLOCK_DEMO("spells/talents/warlock/demonology", TALENT, WARLOCK, DEMONOLOGY),
	TALENTS_WARLOCK_DESTRO("spells/talents/warlock/destruction", TALENT, WARLOCK, DESTRUCTION),

	ABILITIES_PRIEST_DISC("spells/abilities/priest/discipline", ABILITY, PRIEST, DISCIPLINE),
	ABILITIES_PRIEST_HOLY("spells/abilities/priest/holy", ABILITY, PRIEST, HOLY),
	ABILITIES_PRIEST_SHADOW("spells/abilities/priest/shadow-magic", ABILITY, PRIEST, SHADOW),
	TALENTS_PRIEST_DISC("spells/talents/priest/discipline", TALENT, PRIEST, DISCIPLINE),
	TALENTS_PRIEST_HOLY("spells/talents/priest/holy", TALENT, PRIEST, HOLY),
	TALENTS_PRIEST_SHADOW("spells/talents/priest/shadow", TALENT, PRIEST, SHADOW),

	ABILITIES_MAGE_ARCANE("spells/abilities/mage/arcane", ABILITY, MAGE, ARCANE),
	ABILITIES_MAGE_FIRE("spells/abilities/mage/fire", ABILITY, MAGE, FIRE),
	ABILITIES_MAGE_FROST("spells/abilities/mage/frost", ABILITY, MAGE, FROST),
	TALENTS_MAGE_ARCANE("spells/talents/mage/arcane", TALENT, MAGE, ARCANE),
	TALENTS_MAGE_FIRE("spells/talents/mage/fire", TALENT, MAGE, FIRE),
	TALENTS_MAGE_FROST("spells/talents/mage/frost", TALENT, MAGE, FROST),

	ABILITIES_DRUID_BALANCE("spells/abilities/druid/balance", ABILITY, DRUID, BALANCE),
	ABILITIES_DRUID_FERAL_COMBAT("spells/abilities/druid/feral-combat", ABILITY, DRUID, FERAL_COMBAT),
	ABILITIES_DRUID_RESTORATION("spells/abilities/druid/restoration", ABILITY, DRUID, RESTORATION),
	TALENTS_DRUID_BALANCE("spells/talents/druid/balance", TALENT, DRUID, BALANCE),
	TALENTS_DRUID_FERAL_COMBAT("spells/talents/druid/feral-combat", TALENT, DRUID, FERAL_COMBAT),
	TALENTS_DRUID_RESTORATION("spells/talents/druid/restoration", TALENT, DRUID, RESTORATION),

	ABILITIES_SHAMAN_ELEMENTAL("spells/abilities/shaman/elemental-combat", ABILITY, SHAMAN, ELEMENTAL),
	ABILITIES_SHAMAN_ENHANCEMENT("spells/abilities/shaman/enhancement", ABILITY, SHAMAN, ENHANCEMENT),
	ABILITIES_SHAMAN_RESTORATION("spells/abilities/shaman/restoration", ABILITY, SHAMAN, RESTORATION),
	TALENTS_SHAMAN_ELEMENTAL("spells/talents/shaman/elemental", TALENT, SHAMAN, ELEMENTAL),
	TALENTS_SHAMAN_ENHANCEMENT("spells/talents/shaman/enhancement", TALENT, SHAMAN, ENHANCEMENT),
	TALENTS_SHAMAN_RESTORATION("spells/talents/shaman/restoration", TALENT, SHAMAN, RESTORATION),

	ABILITIES_PALADIN_HOLY("spells/abilities/paladin/holy", ABILITY, PALADIN, HOLY),
	ABILITIES_PALADIN_PROTECTION("spells/abilities/paladin/protection", ABILITY, PALADIN, PROTECTION),
	ABILITIES_PALADIN_RETRIBUTION("spells/abilities/paladin/retribution", ABILITY, PALADIN, RETRIBUTION),
	TALENTS_PALADIN_HOLY("spells/talents/paladin/holy", TALENT, PALADIN, HOLY),
	TALENTS_PALADIN_PROTECTION("spells/talents/paladin/protection", TALENT, PALADIN, PROTECTION),
	TALENTS_PALADIN_RETRIBUTION("spells/talents/paladin/retribution", TALENT, PALADIN, RETRIBUTION),

	ENCHANTS("spells/professions/enchanting", ENCHANT, null, null);

	public enum Type {
		ABILITY,
		TALENT,
		ENCHANT
	}

	private final String url;
	private final Type type;
	private final CharacterClassId characterClass;
	private final TalentTree talentTree;

	public static Optional<WowheadSpellCategory> abilitiesOf(CharacterClassId characterClass, TalentTree talentTree) {
		return Stream.of(WowheadSpellCategory.notIgnoredValues())
				.filter(x -> x.characterClass == characterClass && x.talentTree == talentTree)
				.findAny();
	}

	public Optional<WowheadSpellCategory> getAbilityCategory() {
		return abilitiesOf(getCharacterClass(), getTalentTree());
	}

	public static WowheadSpellCategory[] notIgnoredValues() {
		return values();
	}
}
