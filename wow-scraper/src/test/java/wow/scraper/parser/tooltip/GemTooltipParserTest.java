package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.item.MetaEnabler;
import wow.scraper.model.JsonItemDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.item.GemColor.*;
import static wow.commons.model.profession.ProfessionId.JEWELCRAFTING;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P1;
import static wow.commons.model.pve.PhaseId.TBC_P3;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
class GemTooltipParserTest extends TooltipParserTest<JsonItemDetails, GemTooltipParser> {
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
		assertThat(redGem.getItemLevel()).isEqualTo(100);
		assertThat(redGem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(redGem.isUnique()).isFalse();

		assertThat(blueGem.getItemLevel()).isEqualTo(130);
		assertThat(blueGem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(blueGem.isUnique()).isTrue();
	}

	@Test
	@DisplayName("Phase is parsed correctly")
	void gameVersionPhase() {
		assertThat(redGem.getPhase()).isEqualTo(TBC_P3);
		assertThat(blueGem.getPhase()).isEqualTo(TBC_P1);
		assertThat(metaGem.getPhase()).isEqualTo(TBC_P1);
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
		assertThat(metaGem.getStats().getCritDamagePct().value()).isEqualTo(3);
		assertThat(metaGem.getMetaEnablers()).isEqualTo(List.of(MetaEnabler.AT_LEAST_2_BLUES));
	}

	@Test
	@DisplayName("Profession restriction is parsed correctly")
	void professionRestriction() {
		assertThat(blueGem.getRequiredProfession()).isEqualTo(JEWELCRAFTING);
		assertThat(blueGem.getRequiredProfessionLevel()).isEqualTo(360);
	}

	GemTooltipParser redGem;
	GemTooltipParser orangeGem;
	GemTooltipParser yellowGem;
	GemTooltipParser greenGem;
	GemTooltipParser blueGem;
	GemTooltipParser purpleGem;
	GemTooltipParser metaGem;

	@BeforeEach
	void readTestData() throws IOException {
		redGem = getTooltip("gem/32196");
		orangeGem = getTooltip("gem/35760");
		yellowGem = getTooltip("gem/35761");
		greenGem = getTooltip("gem/35759");
		blueGem = getTooltip("gem/33135");
		purpleGem = getTooltip("gem/32215");
		metaGem = getTooltip("gem/34220");
	}

	@Override
	protected GemTooltipParser createParser(JsonItemDetails data) {
		return new GemTooltipParser(data, TBC, scraperContext);
	}

	@Override
	protected Class<JsonItemDetails> getDetailsClass() {
		return JsonItemDetails.class;
	}
}