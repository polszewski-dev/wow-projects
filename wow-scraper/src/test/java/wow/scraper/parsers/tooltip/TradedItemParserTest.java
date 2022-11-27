package wow.scraper.parsers.tooltip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonItemDetailsAndTooltip;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.unit.CharacterClass.*;

/**
 * User: POlszewski
 * Date: 2022-11-17
 */
class TradedItemParserTest {
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

	@BeforeAll
	static void readTestData() throws IOException {
		pauldrons = getTooltip("/tooltips/token/29762");
		verdantSphere = getTooltip("/tooltips/quest/32405");
	}

	static TradedItemParser pauldrons;
	static TradedItemParser verdantSphere;

	static final ObjectMapper MAPPER = new ObjectMapper();

	static TradedItemParser getTooltip(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream(path),
				JsonItemDetailsAndTooltip.class
		);
		TradedItemParser parser = new TradedItemParser(data, TBC);
		parser.parse();
		return parser;
	}
}