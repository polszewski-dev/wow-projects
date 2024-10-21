package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.config.TimeRestriction;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P1;
import static wow.commons.model.pve.PhaseId.TBC_P2;
import static wow.scraper.model.WowheadItemCategory.QUEST;
import static wow.scraper.model.WowheadItemCategory.TOKENS;

/**
 * User: POlszewski
 * Date: 2022-11-17
 */
class TradedItemParserTest extends TooltipParserTest<JsonItemDetails, TradedItemParser, WowheadItemCategory> {
	@Test
	@DisplayName("Token tooltip is parsed correctly")
	void tokenTooltipIsParsedCorrectly() {
		assertThat(pauldrons.getItemId()).isEqualTo(29762);
		assertThat(pauldrons.getName()).isEqualTo("Pauldrons of the Fallen Hero");
		assertThat(pauldrons.getItemLevel()).isEqualTo(70);
		assertThat(pauldrons.getRequiredLevel()).isEqualTo(70);
		assertThat(pauldrons.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(pauldrons.getRequiredClass()).isEqualTo(List.of(HUNTER, MAGE, WARLOCK));
		assertThat(pauldrons.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P1));
	}

	@Test
	@DisplayName("Quest item tooltip is parsed correctly")
	void questItemTooltipIsParsedCorrectly() {
		assertThat(verdantSphere.getItemId()).isEqualTo(32405);
		assertThat(verdantSphere.getName()).isEqualTo("Verdant Sphere");
		assertThat(verdantSphere.getItemLevel()).isEqualTo(70);
		assertThat(verdantSphere.getRequiredLevel()).isEqualTo(70);
		assertThat(verdantSphere.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(verdantSphere.isUnique()).isTrue();
		assertThat(verdantSphere.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P2));
	}

	@BeforeEach
	void readTestData() {
		pauldrons = getTooltip(29762, TOKENS);
		verdantSphere = getTooltip(32405, QUEST);
	}

	TradedItemParser pauldrons;
	TradedItemParser verdantSphere;

	@Override
	protected JsonItemDetails getData(int id, WowheadItemCategory category) {
		return itemDetailRepository.getDetail(TBC, category, id).orElseThrow();
	}

	@Override
	protected TradedItemParser createParser(JsonItemDetails data) {
		return new TradedItemParser(data, TBC, scraperContext);
	}
}