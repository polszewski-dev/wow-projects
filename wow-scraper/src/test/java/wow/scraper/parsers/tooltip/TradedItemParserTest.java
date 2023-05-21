package wow.scraper.parsers.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonItemDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2022-11-17
 */
class TradedItemParserTest extends TooltipParserTest<JsonItemDetails, TradedItemParser> {
	@Test
	@DisplayName("Token tooltip is parsed correctly")
	void tokenTooltipIsParsedCorrectly() {
		assertThat(pauldrons.getItemId()).isEqualTo(29762);
		assertThat(pauldrons.getName()).isEqualTo("Pauldrons of the Fallen Hero");
		assertThat(pauldrons.getItemLevel()).isEqualTo(70);
		assertThat(pauldrons.getRequiredLevel()).isEqualTo(70);
		assertThat(pauldrons.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(pauldrons.getRequiredClass()).isEqualTo(List.of(HUNTER, MAGE, WARLOCK));
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
	}

	@BeforeEach
	void readTestData() throws IOException {
		pauldrons = getTooltip("token/29762");
		verdantSphere = getTooltip("quest/32405");
	}

	static TradedItemParser pauldrons;
	static TradedItemParser verdantSphere;

	@Override
	protected TradedItemParser createParser(JsonItemDetails data) {
		return new TradedItemParser(data, TBC, statPatternRepository);
	}

	@Override
	protected Class<JsonItemDetails> getDetailsClass() {
		return JsonItemDetails.class;
	}
}