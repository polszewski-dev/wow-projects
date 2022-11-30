package wow.minmax.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.Build;
import wow.commons.model.character.PVERole;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.TalentCalculatorUtil;
import wow.minmax.model.BuildIds;
import wow.minmax.repository.BuildRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.character.Build.BuffSetId.*;
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
	public Optional<Build> getBuild(String buildId, int level) {
		switch (buildId) {
			case BuildIds.NONE:
				return createEmptyBuild();
			case BuildIds.DESTRO_SHADOW_BUILD:
				return createDestroShadowBuild(buildId, level);
			default:
				return Optional.empty();
		}
	}

	private Optional<Build> createEmptyBuild() {
		Build build = new Build(BuildIds.NONE);
		build.setTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000000000000000000000000000000000000000000000000");
		build.setTalents(Map.of());
		build.setRole(null);
		build.setDamagingSpell(null);
		build.setRelevantSpells(List.of());
		build.setBuffSets(Map.of());
		return Optional.of(build);
	}

	private Optional<Build> createDestroShadowBuild(String buildId, int level) {
		Build build = new Build(buildId);

		build.setTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250");
		build.setTalents(getTalents(build.getTalentLink()));
		build.setRole(PVERole.CASTER_DPS);
		build.setDamagingSpell(spellDataRepository.getSpellHighestRank(SpellId.SHADOW_BOLT, level).orElseThrow());

		build.setRelevantSpells(
				getSpells(
						level,
						SHADOW_BOLT,
						CURSE_OF_DOOM,
						CURSE_OF_AGONY,
						CORRUPTION,
						IMMOLATE,
						SHADOWBURN,
						SEED_OF_CORRUPTION_DIRECT
				)
		);

		Map<Build.BuffSetId, List<Buff>> buffSets = new EnumMap<>(Build.BuffSetId.class);

		buffSets.put(SELF_BUFFS, getBuffs(
				"Fel Armor",
				"Touch of Shadow"
		));

		buffSets.put(PARTY_BUFFS, getBuffs(
				"Arcane Brilliance",
				"Gift of the Wild",
				"Greater Blessing of Kings"
		));

		buffSets.put(CONSUMES, getBuffs(
				"Well Fed (sp)",
				"Brilliant Wizard Oil",
				"Flask of Pure Death"
		));

		buffSets.put(RAID_BUFFS, getBuffs(
				"Curse of the Elements",
				"Wrath of Air Totem",
				"Totem of Wrath"
		));

		build.setBuffSets(buffSets);

		return Optional.of(build);
	}

	private Map<TalentId, Talent> getTalents(String talentLink) {
		return TalentCalculatorUtil.parseFromLink(talentLink, spellDataRepository);
	}

	private List<Spell> getSpells(int level, SpellId... spellsIds) {
		return Stream.of(spellsIds)
				.map(spellId -> spellDataRepository.getSpellHighestRank(spellId, level).orElseThrow())
				.collect(Collectors.toList());
	}

	private List<Buff> getBuffs(String... buffNames) {
		return spellDataRepository.getBuffs(List.of(buffNames));
	}
}
