package wow.minmax.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.TalentCalculatorUtil;
import wow.minmax.model.Build;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PVERole;
import wow.minmax.repository.BuildRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.spells.SpellId.*;

/**
 * User: POlszewski
 * Date: 2022-01-15
 */
@Repository
@AllArgsConstructor
public class BuildRepositoryImpl implements BuildRepository {
	private final SpellDataRepository spellDataRepository;

	@Override
	public Optional<Build> getBuild(String buildId) {
		if (buildId.equals(BuildIds.NONE)) {
			return createEmptyBuild();
		}
		if (buildId.equals(BuildIds.DESTRO_SHADOW_BUILD)) {
			return createDestroShadowBuild(buildId);
		}
		return Optional.empty();
	}

	private Optional<Build> createEmptyBuild() {
		Build build = new Build(BuildIds.NONE);
		build.setTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000000000000000000000000000000000000000000000000");
		build.setTalentInfos(List.of());
		build.setRole(null);
		build.setDamagingSpell(null);
		build.setRelevantSpells(List.of());
		build.setSelfBuffs(List.of());
		build.setPartyBuffs(List.of());
		build.setConsumeBuffs(List.of());
		build.setRaidBuffs(List.of());
		return Optional.of(build);
	}

	private Optional<Build> createDestroShadowBuild(String buildId) {
		Build build = new Build(buildId);

		build.setTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250");
		build.setTalentInfos(getTalentInfos(build.getTalentLink()));
		build.setRole(PVERole.CASTER_DPS);
		build.setDamagingSpell(new Spell(spellDataRepository.getSpellInfo(SpellId.SHADOW_BOLT).orElseThrow()));

		build.setRelevantSpells(
				getSpells(
						SHADOW_BOLT,
						CURSE_OF_DOOM,
						CURSE_OF_AGONY,
						CORRUPTION,
						IMMOLATE,
						SHADOWBURN,
						SEED_OF_CORRUPTION_DIRECT
				)
		);

		build.setSelfBuffs(getBuffs(
				"Fel Armor",
				"Touch of Shadow"
		));

		build.setPartyBuffs(getBuffs(
				"Arcane Brilliance",
				"Gift of the Wild",
				"Blessing of Kings"
		));

		build.setConsumeBuffs(getBuffs(
				"Well Fed (sp)",
				"Brilliant Wizard Oil",
				"Flask of Pure Death"
		));

		build.setRaidBuffs(getBuffs(
				"Curse of the Elements",
				"Wrath of Air Totem",
				"Totem of Wrath"
		));

		return Optional.of(build);
	}

	private List<TalentInfo> getTalentInfos(String talentLink) {
		return TalentCalculatorUtil.parseFromLink(talentLink, spellDataRepository)
				.values()
				.stream()
				.collect(Collectors.toUnmodifiableList());
	}

	private List<Spell> getSpells(SpellId... spellsIds) {
		return Stream.of(spellsIds)
				.map(spellId -> new Spell(spellDataRepository.getSpellInfo(spellId).orElseThrow()))
				.collect(Collectors.toList());
	}

	private List<Buff> getBuffs(String... buffNames) {
		return spellDataRepository.getBuffs(List.of(buffNames));
	}
}
