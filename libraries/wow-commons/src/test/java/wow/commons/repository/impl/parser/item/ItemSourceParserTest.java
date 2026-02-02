package wow.commons.repository.impl.parser.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.PhaseId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
class ItemSourceParserTest extends WowCommonsSpringTest {
	@Autowired
	ItemSourceParserFactory itemSourceParserFactory;

	@Test
	void empty() {
		var sources = getParser().parse(null);

		assertThat(sources).isEmpty();
	}

	@Test
	void multiple() {
		var sources = getParser().parse("Badges#PvP#WorldDrop");

		assertThat(sources).hasSize(3);
	}

	@Test
	void npc() {
		var sources = getParser().parse("NpcDrop:Kil'jaeden:25315");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isNpcDrop()).isTrue();
		assertThat(source.npc().getId()).isEqualTo(25315);
		assertThat(source.npc().getName()).isEqualTo("Kil'jaeden");
		assertThat(source.zones().getFirst().getId()).isEqualTo(4075);
	}

	@Test
	void zone() {
		var sources = getParser().parse("ZoneDrop:4075");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isZoneDrop()).isTrue();
		assertThat(source.zones().getFirst().getId()).isEqualTo(4075);
	}

	@Test
	void token() {
		var sources = getParser().parse("Token:34856");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isTraded()).isTrue();
		assertId(source.sourceItem(), 34856);
	}

	@Test
	void itemStartingQuest() {
		var sources = getParser().parse("ItemStartingQuest:32405");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isTraded()).isTrue();
		assertId(source.sourceItem(), 32405);
	}

	@Test
	void faction() {
		var sources = getParser().parse("Faction:The Scryers");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isReputationReward()).isTrue();
		assertThat(source.faction().getName()).isEqualTo("The Scryers");
	}

	@Test
	void crafted() {
		var sources = getParser().parse("Crafted:Tailoring");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isCrafted()).isTrue();
		assertThat(source.professionId()).isEqualTo(ProfessionId.TAILORING);
	}

	@Test
	void quest() {
		var sources = getParser().parse("Quest:test");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isQuestReward()).isTrue();
		assertThat(source.questName()).isEqualTo("test");
	}

	@Test
	void quests() {
		var sources = getParser().parse("Quests");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isQuestReward()).isTrue();
		assertThat(source.questName()).isNull();
	}

	@Test
	void badges() {
		var sources = getParser().parse("Badges");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isBadgeVendor()).isTrue();
	}

	@Test
	void pvp() {
		var sources = getParser().parse("PvP");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isPvP()).isTrue();
	}

	@Test
	void worldDrop() {
		var sources = getParser().parse("WorldDrop");

		assertThat(sources).hasSize(1);

		var source = sources.iterator().next();

		assertThat(source.isWorldDrop()).isTrue();
	}

	private ItemSourceParser getParser() {
		return itemSourceParserFactory.create(PhaseId.TBC_P5);
	}
}