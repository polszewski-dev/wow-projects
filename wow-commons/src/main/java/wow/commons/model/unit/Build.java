package wow.commons.model.unit;

import lombok.Data;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.TalentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	private List<Buff> selfBuffs;
	private List<Buff> partyBuffs;
	private List<Buff> consumeBuffs;
	private List<Buff> raidBuffs;

	public enum BuffSet {
		SELF_BUFF,
		PARTY_BUFF,
		CONSUMES,
		RAID_BUFF,
	}

	public Build(String buildId) {
		this.buildId = buildId;
	}

	public List<Buff> getBuffs(BuffSet... buffSets) {
		List<Buff> result = new ArrayList<>();
		if (Arrays.asList(buffSets).contains(BuffSet.SELF_BUFF)) {
			result.addAll(selfBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.PARTY_BUFF)) {
			result.addAll(partyBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.CONSUMES)) {
			result.addAll(consumeBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.RAID_BUFF)) {
			result.addAll(raidBuffs);
		}
		return result;
	}
}
