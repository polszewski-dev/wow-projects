package wow.commons.repository.impl.parsers.items;

import org.junit.jupiter.api.Test;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.sources.Source;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
class SourceParserTest extends WowCommonsSpringTest {
	@Test
	void empty() {
		Set<Source> sources = getParser().parse(null);

		assertThat(sources).isEmpty();
	}

	@Test
	void multiple() {
		Set<Source> sources = getParser().parse("Badges#PvP#WorldDrop");

		assertThat(sources).hasSize(3);
	}

	@Test
	void boss() {
		Set<Source> sources = getParser().parse("BossDrop:Kil'jaeden:25315:4075");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isBossDrop()).isTrue();
		assertThat(source.getBoss().getId()).isEqualTo(25315);
		assertThat(source.getBoss().getName()).isEqualTo("Kil'jaeden");
		assertThat(source.getZone().getId()).isEqualTo(4075);
	}

	@Test
	void zone() {
		Set<Source> sources = getParser().parse("ZoneDrop:4075");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isZoneDrop()).isTrue();
		assertThat(source.getZone().getId()).isEqualTo(4075);
	}

	@Test
	void token() {
		Set<Source> sources = getParser().parse("Token:34856");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isTraded()).isTrue();
		assertThat(source.getSourceItem().getId()).isEqualTo(34856);
	}

	@Test
	void itemStartingQuest() {
		Set<Source> sources = getParser().parse("ItemStartingQuest:32405");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isTraded()).isTrue();
		assertThat(source.getSourceItem().getId()).isEqualTo(32405);
	}

	@Test
	void faction() {
		Set<Source> sources = getParser().parse("Faction:The Scryers");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isReputationReward()).isTrue();
		assertThat(source.getFaction().getName()).isEqualTo("The Scryers");
	}

	@Test
	void crafted() {
		Set<Source> sources = getParser().parse("Crafted:Tailoring");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isCrafted()).isTrue();
		assertThat(source.getProfessionId()).isEqualTo(ProfessionId.TAILORING);
	}

	@Test
	void quest() {
		Set<Source> sources = getParser().parse("Quest:test");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isQuestReward()).isTrue();
		assertThat(source.getQuestName()).isEqualTo("test");
	}

	@Test
	void quests() {
		Set<Source> sources = getParser().parse("Quests");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isQuestReward()).isTrue();
		assertThat(source.getQuestName()).isNull();
	}

	@Test
	void badges() {
		Set<Source> sources = getParser().parse("Badges");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isBadgeVendor()).isTrue();
	}

	@Test
	void pvp() {
		Set<Source> sources = getParser().parse("PvP");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isPvP()).isTrue();
	}

	@Test
	void worldDrop() {
		Set<Source> sources = getParser().parse("WorldDrop");

		assertThat(sources).hasSize(1);

		Source source = sources.iterator().next();

		assertThat(source.isWorldDrop()).isTrue();
	}

	private SourceParser getParser() {
		return new SourceParser(PhaseId.TBC_P5, pveRepository, itemRepository);
	}
}