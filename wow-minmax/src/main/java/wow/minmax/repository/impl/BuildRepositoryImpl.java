package wow.minmax.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.TalentCalculatorUtil;
import wow.minmax.model.Build;
import wow.minmax.model.BuildIds;
import wow.minmax.model.Spell;
import wow.minmax.repository.BuildRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-01-15
 */
@Repository
@AllArgsConstructor
public class BuildRepositoryImpl implements BuildRepository {
	private final SpellDataRepository spellDataRepository;

	@Override
	public Build getBuild(String buildId) {
		if (!buildId.equals(BuildIds.DESTRO_SHADOW_BUILD)) {
			throw new IllegalArgumentException(buildId);
		}

		Build build = new Build(buildId);
		build.setTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250");
		build.setTalentInfos(TalentCalculatorUtil.parseFromLink(build.getTalentLink(), spellDataRepository)
									 .values()
									 .stream()
									 .collect(Collectors.toUnmodifiableList()));
		build.setDamagingSpell(new Spell(spellDataRepository.getSpellInfo(SpellId.ShadowBolt)));

		build.setSelfBuffs(spellDataRepository.getBuffs(List.of(
				"Fel Armor",
				"Touch of Shadow"
		)));

		build.setPartyBuffs(spellDataRepository.getBuffs(List.of(
				"Arcane Brilliance",
				"Gift of the Wild",
				"Blessing of Kings"
		)));

		build.setConsumeBuffs(spellDataRepository.getBuffs(List.of(
				"Well Fed (sp)",
				"Brilliant Wizard Oil",
				"Flask of Pure Death"
		)));

		build.setRaidBuffs(spellDataRepository.getBuffs(List.of(
				"Curse of the Elements",
				"Wrath of Air Totem",
				"Totem of Wrath"
		)));

		return build;
	}
}