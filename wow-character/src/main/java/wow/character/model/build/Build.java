package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.PetType;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@AllArgsConstructor
@Getter
public class Build implements AttributeCollection {
	private final BuildId buildId;
	private final String talentLink;
	private final Map<TalentId, Talent> talents;
	private final PveRole role;
	private final Spell damagingSpell;
	private final List<Spell> relevantSpells;
	private final PetType activePet;
	private final Map<BuffSetId, List<Buff>> buffSets;

	public static final Build EMPTY = new Build(null, null, Map.of(), null, null, List.of(), null, Map.of());

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		collector.addAttributes(talents.values());
	}

	public boolean hasTalent(TalentId talentId) {
		return talents.containsKey(talentId);
	}

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return buffSets.getOrDefault(buffSetId, List.of());
	}

	public Build setDamagingSpell(Spell damagingSpell) {
		return new Build(
				buildId,
				talentLink,
				talents,
				role,
				damagingSpell,
				relevantSpells,
				activePet,
				buffSets
		);
	}

	public Build setRelevantSpells(List<Spell> relevantSpells) {
		return new Build(
				buildId,
				talentLink,
				talents,
				role,
				damagingSpell,
				relevantSpells,
				activePet,
				buffSets
		);
	}

	public Build setBuffSets(Map<BuffSetId, List<Buff>> buffSets) {
		return new Build(
				buildId,
				talentLink,
				talents,
				role,
				damagingSpell,
				relevantSpells,
				activePet,
				buffSets
		);
	}
}
