package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.talent.TalentTree.DESTRUCTION;
import static wow.scraper.model.WowheadSpellCategory.TALENTS_WARLOCK_DESTRO;

/**
 * User: POlszewski
 * Date: 2023-10-01
 */
class TalentTooltipParserTest extends TooltipParserTest<JsonSpellDetails, TalentTooltipParser, WowheadSpellCategory> {
	@Test
	void test() {
		TalentTooltipParser parser = getTooltip(17803, TALENTS_WARLOCK_DESTRO);

		assertThat(parser.getName()).isEqualTo("Improved Shadow Bolt");
		assertThat(parser.getRank()).isEqualTo(5);
		assertThat(parser.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(parser.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(parser.getGameVersion()).isEqualTo(TBC);
		assertThat(parser.getTalentCalculatorPosition()).isEqualTo(44);
		assertThat(parser.getIcon()).isEqualTo("spell_shadow_shadowbolt");
		assertThat(parser.getDescription()).isEqualTo("Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by 20% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec.");
	}

	@Override
	protected JsonSpellDetails getData(int id, WowheadSpellCategory category) {
		return spellDetailRepository.getDetail(TBC, category, id).orElseThrow();
	}

	@Override
	protected TalentTooltipParser createParser(JsonSpellDetails data) {
		return new TalentTooltipParser(data, scraperContext);
	}
}