package wow.scraper.parsers.tooltip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.item.MetaEnabler;
import wow.scraper.model.JsonItemDetailsAndTooltip;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.item.GemColor.*;
import static wow.commons.model.professions.Profession.JEWELCRAFTING;
import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.pve.Phase.TBC_P0;
import static wow.commons.model.pve.Phase.TBC_P3;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
class GemTooltipParserTest {
	@Test
	@DisplayName("Item id/name are parsed correctly")
	void idAndName() {
		assertThat(redGem.getItemId()).isEqualTo(32196);
		assertThat(redGem.getName()).isEqualTo("Runed Crimson Spinel");

		assertThat(metaGem.getItemId()).isEqualTo(34220);
		assertThat(metaGem.getName()).isEqualTo("Chaotic Skyfire Diamond");
	}

	@Test
	@DisplayName("Basic info is parsed correctly")
	void basicInfo() {
		assertThat(redGem.getPhase()).isEqualTo(TBC_P3);
		assertThat(redGem.getItemLevel()).isEqualTo(100);
		assertThat(redGem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(redGem.isUnique()).isFalse();

		assertThat(blueGem.getPhase()).isEqualTo(TBC_P0);
		assertThat(blueGem.getItemLevel()).isEqualTo(130);
		assertThat(blueGem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(blueGem.isUnique()).isTrue();
	}

	@Test
	@DisplayName("Color are parsed correctly")
	void color() {
		assertThat(redGem.getColor()).isEqualTo(RED);
		assertThat(orangeGem.getColor()).isEqualTo(ORANGE);
		assertThat(yellowGem.getColor()).isEqualTo(YELLOW);
		assertThat(greenGem.getColor()).isEqualTo(GREEN);
		assertThat(blueGem.getColor()).isEqualTo(BLUE);
		assertThat(purpleGem.getColor()).isEqualTo(PURPLE);
		assertThat(metaGem.getColor()).isEqualTo(META);
	}

	@Test
	@DisplayName("Stats are parsed correctly")
	void stats() {
		assertThat(redGem.getStats().getSpellDamage()).isEqualTo(12);
		assertThat(orangeGem.getStats().getSpellHasteRating()).isEqualTo(5);
		assertThat(orangeGem.getStats().getSpellDamage()).isEqualTo(6);
		assertThat(yellowGem.getStats().getSpellHasteRating()).isEqualTo(10);
		assertThat(greenGem.getStats().getSpellHasteRating()).isEqualTo(5);
		assertThat(greenGem.getStats().getStamina()).isEqualTo(7);
		assertThat(blueGem.getStats().getStamina()).isEqualTo(18);

		assertThat(metaGem.getStats().getSpellCritRating()).isEqualTo(12);
		assertThat(metaGem.getStats().getIncreasedCriticalDamagePct().getValue()).isEqualTo(3);
		assertThat(metaGem.getMetaEnablers()).isEqualTo(List.of(MetaEnabler.AT_LEAST_2_BLUES));
	}

	@Test
	@DisplayName("Profession restriction is parsed correctly")
	void professionRestriction() {
		assertThat(blueGem.getRequiredProfession()).isEqualTo(JEWELCRAFTING);
		assertThat(blueGem.getRequiredProfessionLevel()).isEqualTo(360);
	}

	static GemTooltipParser redGem;
	static GemTooltipParser orangeGem;
	static GemTooltipParser yellowGem;
	static GemTooltipParser greenGem;
	static GemTooltipParser blueGem;
	static GemTooltipParser purpleGem;
	static GemTooltipParser metaGem;

	@BeforeAll
	static void readTestData() throws IOException {
		redGem = getTooltip("32196");
		orangeGem = getTooltip("35760");
		yellowGem = getTooltip("35761");
		greenGem = getTooltip("35759");
		blueGem = getTooltip("33135");
		purpleGem = getTooltip("32215");
		metaGem = getTooltip("34220");
	}

	static final ObjectMapper MAPPER = new ObjectMapper();

	static GemTooltipParser getTooltip(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream("/tooltips/gem/" + path),
				JsonItemDetailsAndTooltip.class
		);
		GemTooltipParser parser = new GemTooltipParser(data, TBC);
		parser.parse();
		return parser;
	}
}