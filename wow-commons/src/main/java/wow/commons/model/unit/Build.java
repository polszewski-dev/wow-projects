package wow.commons.model.unit;

import lombok.Data;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.TalentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@Data
public class Build {
	private final String buildId;
	private String talentLink;
	private List<TalentInfo> talentInfos;
	private PVERole role;
	private Spell damagingSpell;
	private List<Spell> relevantSpells;
	private PetType activePet;

	public enum BuffSetId {
		SELF_BUFFS,
		PARTY_BUFFS,
		RAID_BUFFS,
		CONSUMES,
	}

	private Map<BuffSetId, List<Buff>> buffSets;

	public Build(String buildId) {
		this.buildId = buildId;
	}

	public List<Buff> getBuffs(BuffSetId... buffSetIds) {
		List<Buff> result = new ArrayList<>();
		for (BuffSetId buffSetId : buffSetIds) {
			result.addAll(buffSets.getOrDefault(buffSetId, List.of()));
		}
		return result;
	}
}
