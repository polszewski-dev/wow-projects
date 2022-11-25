package wow.commons.model.unit;

import lombok.Data;
import wow.commons.model.Copyable;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.util.AttributesBuilder;

import java.util.*;
import java.util.stream.Collectors;

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
		CURRENT,
		SELF_BUFFS,
		PARTY_BUFFS,
		RAID_BUFFS,
		WORLD_BUFFS,
		CONSUMES,
	}

	private Map<BuffSetId, List<Buff>> buffSets;
	private List<Buff> buffs;

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
		copy.buffs = new ArrayList<>(buffs);
		return copy;
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		Attributes talentAttributes = new AttributesBuilder()
				.addAttributes(talents.values())
				.toAttributes();
		collector.addAttributes(talentAttributes);
		collector.addAttributes(getBuffsModifiedByTalents(talentAttributes));
	}

	public void setBuffs(List<Buff> buffs) {
		validateExclusionGroups(buffs);
		this.buffs = buffs.stream()
				.distinct()
				.collect(Collectors.toList());
	}

	public void setBuffsFromSets(BuffSetId... buffSetIds) {
		Set<Buff> newBuffs = new HashSet<>();
		for (BuffSetId buffSetId : buffSetIds) {
			newBuffs.addAll(getBuffSet(buffSetId));
		}
		this.buffs = new ArrayList<>(newBuffs);
	}

	private List<Buff> getBuffSet(BuffSetId buffSetId) {
		if (buffSetId == BuffSetId.CURRENT) {
			return buffs;
		}
		return buffSets.getOrDefault(buffSetId, List.of());
	}

	public void enableBuff(Buff buff, boolean enable) {
		if (!enable) {
			buffs.removeIf(existingBuff -> existingBuff.getId() == buff.getId());
			return;
		}

		if (hasBuff(buff)) {
			return;
		}

		if (buff.getExclusionGroup() != null) {
			buffs.removeIf(existingBuff -> existingBuff.getExclusionGroup() == buff.getExclusionGroup());
		}

		buffs.add(buff);
	}

	private boolean hasBuff(Buff buff) {
		return buffs.stream().anyMatch(existingBuff -> existingBuff.getId() == buff.getId());
	}

	private void validateExclusionGroups(List<Buff> buffs) {
		var groups = buffs.stream()
				.filter(buff -> buff.getExclusionGroup() != null)
				.collect(Collectors.groupingBy(Buff::getExclusionGroup));

		for (var group : groups.entrySet()) {
			if (group.getValue().size() > 1) {
				throw new IllegalArgumentException("Group:  " + group.getKey() + " has more than one buff");
			}
		}
	}

	private List<AttributeSource> getBuffsModifiedByTalents(Attributes talentAttributes) {
		List<AttributeSource> result = new ArrayList<>(buffs.size());

		for (Buff buff : buffs) {
			if (buff.getType() == BuffType.SELF_BUFF) {
				Percent effectIncreasePct = talentAttributes.getEffectIncreasePct(buff.getSourceSpell());
				result.add(buff.modifyEffectByPct(effectIncreasePct));
			} else {
				result.add(buff);
			}
		}

		return result;
	}
}
