package wow.commons.model.character;

import lombok.Data;
import wow.commons.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@Data
public class Build implements Copyable<Build>, AttributeCollection {
	private final String buildId;
	private String talentLink;
	private Map<TalentId, Talent> talents;
	private PVERole role;
	private Spell damagingSpell;
	private List<Spell> relevantSpells;
	private PetType activePet;

	public enum BuffSetId {
		SELF_BUFFS,
		PARTY_BUFFS,
		RAID_BUFFS,
		WORLD_BUFFS,
		CONSUMES,
	}

	private Map<BuffSetId, List<Buff>> buffSets;

	public Build(String buildId) {
		this.buildId = buildId;
	}

	@Override
	public Build copy() {
		Build copy = new Build(buildId);
		copy.talentLink = talentLink;
		copy.talents = talents;
		copy.role = role;
		copy.damagingSpell = damagingSpell;
		copy.relevantSpells = new ArrayList<>(relevantSpells);
		copy.activePet = activePet;
		copy.buffSets = !buffSets.isEmpty() ? new EnumMap<>(buffSets) : new EnumMap<>(BuffSetId.class);
		return copy;
	}

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
}
