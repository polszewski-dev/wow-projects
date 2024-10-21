package wow.scraper.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.TalentId;
import wow.commons.util.CollectionUtil;
import wow.scraper.fetcher.PageCache;
import wow.scraper.fetcher.PageFetcher;
import wow.scraper.fetcher.impl.CachedPageFetcher;
import wow.scraper.fetcher.impl.WebPageFetcher;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.model.WowheadZoneType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 12.10.2024
 */
@PropertySource("classpath:datafixes.properties")
@Component
@NoArgsConstructor
@Getter
@Setter
public class ScraperDatafixes {
	@Value("#{'${ignored.item.ids}'.split(',')}")
	private Set<Integer> ignoredItemIds;

	@Value("#{'${ignored.npc.ids}'.split(',')}")
	private Set<Integer> ignoredNpcIds;

	@Value("#{'${ignored.zone.ids}'.split(',')}")
	private Set<Integer> ignoredZoneIds;

	@Value("#{'${npc.ids.to.fetch}'.split(',')}")
	private Set<Integer> npcIdsToFetch;

	@Value("#{${npc.to.create}}")
	private Map<Integer, String> npcToCreate;

	@Value("#{${zone.type.overrides}}")
	private Map<Integer, WowheadZoneType> zoneTypeOverrides;

	@Value("#{${npc.location.overrides}}")
	private Map<Integer, List<Integer>> npcLocationOverrides;

	@Value("#{${dungeon.short.names}}")
	private Map<String, String> dungeonShortNames;

	@Value("#{${pvp.item.name.parts}}")
	private List<String> pvpItemNameParts;

	@Value("#{${pvp.item.ids}}")
	private Set<Integer> pvpItemIds;

	@Value("#{${source.npc.to.npc.replacement}}")
	private Map<Integer, Integer> sourceNpcToNpcReplacements;

	@Value("#{${source.object.to.npc.replacement}}")
	private Map<Integer, Integer> sourceObjectToNpcReplacements;

	@Value("#{'${world.drop.overrides}'.split(',')}")
	private List<Integer> worldDropOverrides;

	@Value("#{${npc.drop.overrides}}")
	private Map<Integer, List<Integer>> npcDropOverrides;

	@Value("#{${token.to.traded.for}}")
	private Map<Integer, List<Integer>> tokenToTradedFor;

	@Value("#{${item.starting.quest.to.traded.for}}")
	private Map<Integer, List<Integer>> itemStartingQuestToTradedFor;

	@Value("#{${item.phase.overrides}}")
	private Map<Integer, PhaseId> itemPhaseOverrides;

	@Value("#{${pve.role.overrides}}")
	private Map<Integer, PveRole> pveRoleOverrides;

	@Value("#{${spell.phase.overrides}}")
	private Map<Integer, PhaseId> spellPhaseOverrides;

	@Value("#{${item.required.side.overrides}}")
	private Map<Side, Set<Integer>> itemRequiredSideOverrides;

	@Value("#{${alliance.only.factions}}")
	private Set<String> allianceOnlyFactions;

	@Value("#{${horde.only.factions}}")
	private Set<String> hordeOnlyFactions;

	@Value("#{${talent.spells}}")
	private Map<WowheadSpellCategory, Map<GameVersionId, List<String>>> talentSpells;

	@Value("#{${rank.overrides}}")
	private Map<Integer, Integer> rankOverrides;

	@Value("#{${spell.name.overrides}}")
	private Map<String, String> spellNameOverrides;

	@Value("#{${talent.calculator.positions}}")
	private Map<GameVersionId, Map<String, Integer>> talentCalculatorPositions;

	@Value("#{${missing.spell.ids}}")
	private Map<WowheadSpellCategory, Map<GameVersionId, List<Integer>>> missingSpells;

	@Value("#{${ignored.spell.ids}}")
	private Map<WowheadSpellCategory, Map<GameVersionId, List<Integer>>> ignoredSpellIds;


	public Optional<Side> getItemRequiredSideOverride(int itemId) {
		return getSide(itemRequiredSideOverrides, itemId);
	}

	private Optional<Side> getSide(Map<Side, Set<Integer>> map, int id) {
		return map.entrySet().stream()
				.filter(x -> x.getValue().contains(id))
				.map(Map.Entry::getKey)
				.collect(CollectionUtil.toOptionalSingleton());
	}

	public Side getRequiredSideFromFaction(String faction) {
		if (allianceOnlyFactions.contains(faction)) {
			return Side.ALLIANCE;
		}

		if (hordeOnlyFactions.contains(faction)) {
			return Side.HORDE;
		}

		return null;
	}

	@Bean
	@Primary
	public PageFetcher pageFetcher(PageCache pageCache) {
		return new CachedPageFetcher(new WebPageFetcher(), pageCache);
	}

	public boolean isTalentSpell(String name, WowheadSpellCategory category, GameVersionId gameVersion) {
		WowheadSpellCategory abilityCategory = WowheadSpellCategory.abilitiesOf(category.getCharacterClass(), category.getTalentTree()).orElseThrow();
		return talentSpells.get(abilityCategory).get(gameVersion).contains(name);
	}

	public Integer getTalentCalculatorPosition(GameVersionId gameVersion, TalentId talentId) {
		return talentCalculatorPositions.get(gameVersion).get(talentId.getName());
	}

	public List<Integer> getMissingSpellIds(WowheadSpellCategory category, GameVersionId gameVersion) {
		if (category == ENCHANTS) {
			return List.of();
		}
		return missingSpells.get(category).get(gameVersion);
	}

	public boolean isSpellIgnored(Integer spellId, WowheadSpellCategory category, GameVersionId gameVersion) {
		return ignoredSpellIds.getOrDefault(category, Map.of()).getOrDefault(gameVersion, List.of()).contains(spellId);
	}
}
