package wow.minmax.model;

import lombok.Data;
import wow.commons.model.buffs.Buff;
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
	private Spell damagingSpell;

	private List<Buff> selfBuffs;
	private List<Buff> partyBuffs;
	private List<Buff> consumeBuffs;
	private List<Buff> raidBuffs;

	public enum BuffSet {
		SelfBuff,
		PartyBuff,
		Consumes,
		RaidBuff,
	}

	public Build(String buildId) {
		this.buildId = buildId;
	}

	public List<Buff> getBuffs(BuffSet... buffSets) {
		List<Buff> result = new ArrayList<>();
		if (Arrays.asList(buffSets).contains(BuffSet.SelfBuff)) {
			result.addAll(selfBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.PartyBuff)) {
			result.addAll(partyBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.Consumes)) {
			result.addAll(consumeBuffs);
		}
		if (Arrays.asList(buffSets).contains(BuffSet.RaidBuff)) {
			result.addAll(raidBuffs);
		}
		return result;
	}
}
