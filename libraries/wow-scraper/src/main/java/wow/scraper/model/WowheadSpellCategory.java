package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.talent.TalentTree;

import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.scraper.model.WowheadSpellCategory.Type.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@AllArgsConstructor
@Getter
public enum WowheadSpellCategory {
	ABILITIES_WARLOCK_AFFLI("spells/abilities/warlock/affliction", ABILITY, WARLOCK, TalentTree.AFFLICTION),
	ABILITIES_WARLOCK_DEMO("spells/abilities/warlock/demonology", ABILITY, WARLOCK, TalentTree.DEMONOLOGY),
	ABILITIES_WARLOCK_DESTRO("spells/abilities/warlock/destruction", ABILITY, WARLOCK, TalentTree.DESTRUCTION),
	TALENTS_WARLOCK_AFFLI("spells/talents/warlock/affliction", TALENT, WARLOCK, TalentTree.AFFLICTION),
	TALENTS_WARLOCK_DEMO("spells/talents/warlock/demonology", TALENT, WARLOCK, TalentTree.DEMONOLOGY),
	TALENTS_WARLOCK_DESTRO("spells/talents/warlock/destruction", TALENT, WARLOCK, TalentTree.DESTRUCTION),

	ABILITIES_PRIEST_DISC("spells/abilities/priest/discipline", ABILITY, PRIEST, TalentTree.DISCIPLINE),
	ABILITIES_PRIEST_HOLY("spells/abilities/priest/holy", ABILITY, PRIEST, TalentTree.HOLY),
	ABILITIES_PRIEST_SHADOW("spells/abilities/priest/shadow-magic", ABILITY, PRIEST, TalentTree.SHADOW),
	TALENTS_PRIEST_DISC("spells/talents/priest/discipline", TALENT, PRIEST, TalentTree.DISCIPLINE),
	TALENTS_PRIEST_HOLY("spells/talents/priest/holy", TALENT, PRIEST, TalentTree.HOLY),
	TALENTS_PRIEST_SHADOW("spells/talents/priest/shadow", TALENT, PRIEST, TalentTree.SHADOW),

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
		return Stream.of(WowheadSpellCategory.values())
				.filter(x -> x.characterClass == characterClass && x.talentTree == talentTree)
				.findAny();
	}
}
