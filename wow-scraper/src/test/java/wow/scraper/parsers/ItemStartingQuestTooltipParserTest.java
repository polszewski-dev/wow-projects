package wow.scraper.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonItemDetailsAndTooltip;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.pve.GameVersion.TBC;

/**
 * User: POlszewski
 * Date: 2022-11-17
 */
class ItemStartingQuestTooltipParserTest {
	@Test
	@DisplayName("Tooltip is parsed correctly")
	void tooltipIsParsedCorrectly() {
		assertThat(verdantSphere.getItemId()).isEqualTo(32405);
		assertThat(verdantSphere.getName()).isEqualTo("Verdant Sphere");
		assertThat(verdantSphere.getItemLevel()).isEqualTo(70);
		assertThat(verdantSphere.getRequiredLevel()).isEqualTo(70);
		assertThat(verdantSphere.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(verdantSphere.isUnique()).isTrue();
	}

	@BeforeAll
	static void readTestData() throws IOException {
		verdantSphere = getTooltip("32405");
	}

	static ItemStartingQuestTooltipParser verdantSphere;

	static final ObjectMapper MAPPER = new ObjectMapper();

	static ItemStartingQuestTooltipParser getTooltip(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream("/tooltips/quest/" + path),
				JsonItemDetailsAndTooltip.class
		);
		Integer itemId = data.getDetails().getId();
		String htmlTooltip = data.getHtmlTooltip();
		ItemStartingQuestTooltipParser parser = new ItemStartingQuestTooltipParser(itemId, htmlTooltip, TBC);
		parser.parse();
		return parser;
	}
}