package wow.scraper.parsers;

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
class TokenTooltipParserTest {
	@Test
	@DisplayName("Tooltip is parsed correctly")
	void tooltipIsParsedCorrectly() {
		assertThat(pauldrons.getItemId()).isEqualTo(29762);
		assertThat(pauldrons.getName()).isEqualTo("Pauldrons of the Fallen Hero");
		assertThat(pauldrons.getItemLevel()).isEqualTo(70);
		assertThat(pauldrons.getRequiredLevel()).isEqualTo(70);
		assertThat(pauldrons.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(pauldrons.getClassRestriction()).isEqualTo(List.of(HUNTER, MAGE, WARLOCK));
	}

	@BeforeAll
	static void readTestData() throws IOException {
		pauldrons = getTooltip("29762");
	}

	static TokenTooltipParser pauldrons;

	static final ObjectMapper MAPPER = new ObjectMapper();

	static TokenTooltipParser getTooltip(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream("/tooltips/token/" + path),
				JsonItemDetailsAndTooltip.class
		);
		Integer itemId = data.getDetails().getId();
		String htmlTooltip = data.getHtmlTooltip();
		TokenTooltipParser parser = new TokenTooltipParser(itemId, htmlTooltip, TBC);
		parser.parse();
		return parser;
	}
}