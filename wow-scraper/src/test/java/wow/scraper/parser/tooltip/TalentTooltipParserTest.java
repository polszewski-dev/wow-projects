package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.model.WowheadSpellInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.talent.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2023-10-01
 */
class TalentTooltipParserTest extends TooltipParserTest<JsonSpellDetails, TalentTooltipParser> {
	@Test
	void test() {
		TalentTooltipParser parser = getTooltip("talent/17803");

		assertThat(parser.getName()).isEqualTo("Improved Shadow Bolt");
		assertThat(parser.getRank()).isEqualTo(5);
		assertThat(parser.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(parser.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(parser.getGameVersion()).isEqualTo(TBC);
		assertThat(parser.getTalentCalculatorPosition()).isEqualTo(44);
		assertThat(parser.getIcon()).isEqualTo("spell_shadow_shadowbolt");
		assertThat(parser.getDescription()).isEqualTo("Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by 20% until 4 non-periodic damage sources are applied.  Effect lasts a maximum of 12 sec.");
	}

	@Override
	protected TalentTooltipParser createParser(JsonSpellDetails data) {
		return new TalentTooltipParser(data, scraperContext);
	}

	@Override
	protected JsonSpellDetails getData(String path) {
		var info = read(path, WowheadSpellInfo.class);
		var details = new JsonSpellDetails();
		details.setCategory(WowheadSpellCategory.TALENTS_WARLOCK_DESTRO);
		details.setName(info.getName());
		details.setReqVersion(TBC);
		details.setHtmlTooltip(info.getTooltip());
		details.setIcon(info.getIcon());
		return details;
	}

	@Override
	protected Class<JsonSpellDetails> getDetailsClass() {
		return JsonSpellDetails.class;
	}
}