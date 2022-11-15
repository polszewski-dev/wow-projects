package wow.scraper.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.Money;
import wow.scraper.model.JsonItemDetailsAndTooltip;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.ItemType.*;
import static wow.commons.model.categorization.WeaponSubType.STAFF;
import static wow.commons.model.categorization.WeaponSubType.WAND;
import static wow.commons.model.item.SocketType.*;
import static wow.commons.model.professions.Profession.TAILORING;
import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.pve.Phase.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
class ItemTooltipParserTest {
	@Test
	@DisplayName("Item id/name are parsed correctly")
	void idAndName() {
		assertThat(sunfireRobe.getItemId()).isEqualTo(34364);
		assertThat(sunfireRobe.getName()).isEqualTo("Sunfire Robe");
	}

	@Test
	@DisplayName("Basic info is parsed correctly")
	void basicInfo() {
		assertThat(sunfireRobe.getPhase()).isEqualTo(TBC_P5);
		assertThat(sunfireRobe.getRequiredLevel()).isEqualTo(70);
		assertThat(sunfireRobe.getItemLevel()).isEqualTo(159);
		assertThat(sunfireRobe.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(sunfireRobe.isUnique()).isFalse();

		assertThat(hoodOfMalefic.getItemType()).isEqualTo(HEAD);
		assertThat(hoodOfMalefic.getItemSubType()).isEqualTo(CLOTH);

		assertThat(sunfireRobe.getItemType()).isEqualTo(CHEST);
		assertThat(sunfireRobe.getItemSubType()).isEqualTo(CLOTH);

		assertThat(scryersBlodgem.getItemType()).isEqualTo(TRINKET);
		assertThat(scryersBlodgem.getItemSubType()).isNull();

		assertThat(magistersStaff.getItemType()).isEqualTo(TWO_HAND);
		assertThat(magistersStaff.getItemSubType()).isEqualTo(STAFF);

		assertThat(wandOfDemonsoul.getItemType()).isEqualTo(RANGED);
		assertThat(wandOfDemonsoul.getItemSubType()).isEqualTo(WAND);
	}

	@Test
	@DisplayName("Socket info is parsed correctly")
	void socketInfo() {
		assertThat(hoodOfMalefic.getSocketTypes()).isEqualTo(List.of(META, YELLOW));
		assertThat(hoodOfMalefic.getSocketBonus()).isEqualTo("+5 Spell Damage and Healing");

		assertThat(sunfireRobe.getSocketTypes()).isEqualTo(List.of(RED, RED, RED));
		assertThat(sunfireRobe.getSocketBonus()).isEqualTo("+5 Spell Damage and Healing");
	}

	@Test
	@DisplayName("Stats are parsed correctly")
	void stats() {
		assertThat(sunfireRobe.getStatLines()).isEqualTo(List.of(
				"266 Armor",
				"+36 Stamina",
				"+34 Intellect",
				"Equip: Improves spell critical strike rating by 40.",
				"Equip: Improves spell haste rating by 40.",
				"Equip: Increases damage and healing done by magical spells and effects by up to 71."
		));
	}

	@Test
	@DisplayName("Weapon stats are parsed correctly")
	void weaponStats() {
		assertThat(sunfireRobe.getWeaponStats()).isNull();
		assertThat(magistersStaff.getWeaponStats()).isNotNull();
		assertThat(magistersStaff.getWeaponStats().getWeaponDamageMin()).isEqualTo(146);
		assertThat(magistersStaff.getWeaponStats().getWeaponDamageMax()).isEqualTo(326);
		assertThat(magistersStaff.getWeaponStats().getDamageType()).isNull();
		assertThat(magistersStaff.getWeaponStats().getWeaponDps()).isEqualTo(73.75);
		assertThat(magistersStaff.getWeaponStats().getWeaponSpeed()).isEqualTo(3.20);
	}

	@Test
	@DisplayName("Item set info is parsed correctly")
	void itemSet() {
		assertThat(hoodOfMalefic.getItemSetName()).isEqualTo("Malefic Raiment");
		assertThat(hoodOfMalefic.getItemSetPieces()).isEqualTo(List.of(
				"Gloves of the Malefic",
				"Hood of the Malefic",
				"Leggings of the Malefic",
				"Mantle of the Malefic",
				"Robe of the Malefic",
				"Boots of the Malefic",
				"Bracers of the Malefic",
				"Belt of the Malefic"
		));
		assertThat(hoodOfMalefic.getItemSetBonuses()).hasSize(2);
		assertThat(hoodOfMalefic.getItemSetBonuses().get(0).getNumPieces()).isEqualTo(2);
		assertThat(hoodOfMalefic.getItemSetBonuses().get(0).getDescription()).isEqualTo("Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health.");
		assertThat(hoodOfMalefic.getItemSetBonuses().get(1).getNumPieces()).isEqualTo(4);
		assertThat(hoodOfMalefic.getItemSetBonuses().get(1).getDescription()).isEqualTo("Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%.");
		assertThat(hoodOfMalefic.getItemSetRequiredProfession()).isNull();
		assertThat(hoodOfMalefic.getItemSetRequiredProfessionLevel()).isNull();
	}

	@Test
	@DisplayName("Faction restriction is parsed correctly")
	void factionRestriction() {
		assertThat(scryersBlodgem.getRequiredFactionName()).isEqualTo("The Scryers");
		assertThat(scryersBlodgem.getRequiredFactionStanding()).isEqualTo("Revered");
	}

	@Test
	@DisplayName("Profession restriction is parsed correctly")
	void professionRestriction() {
		assertThat(sunfireRobe.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(sunfireRobe.getRequiredProfessionLevel()).isEqualTo(350);
		assertThat(sunfireRobe.getRequiredProfessionSpec()).isNull();
	}

	@Test
	@DisplayName("Misc restriction is parsed correctly")
	void miscRestriction() {
		assertThat(sunfireRobe.getClassRestriction()).isNull();
		assertThat(sunfireRobe.getRaceRestriction()).isNull();
		assertThat(sunfireRobe.getSideRestriction()).isNull();

	}

	@Test
	@DisplayName("Misc info is parsed correctly")
	void misc() {
		assertThat(sunfireRobe.isRandomEnchantment()).isFalse();
		assertThat(sunfireRobe.getDroppedBy()).isNull();
		assertThat(sunfireRobe.getDropChance()).isNull();
		assertThat(sunfireRobe.getSellPrice()).isEqualTo(Money.parse("6g8s49c"));
	}

	static ItemTooltipParser hoodOfMalefic;
	static ItemTooltipParser sunfireRobe;
	static ItemTooltipParser scryersBlodgem;
	static ItemTooltipParser magistersStaff;
	static ItemTooltipParser wandOfDemonsoul;

	@BeforeAll
	static void readTestData() throws IOException {
		hoodOfMalefic = getReadValue("31051");
		sunfireRobe = getReadValue("34364");
		scryersBlodgem = getReadValue("29132");
		magistersStaff = getReadValue("34182");
		wandOfDemonsoul = getReadValue("34347");
	}

	static final ObjectMapper MAPPER = new ObjectMapper();

	static ItemTooltipParser getReadValue(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream("/tooltips/item/" + path),
				JsonItemDetailsAndTooltip.class
		);
		Integer itemId = data.getDetails().getId();
		String htmlTooltip = data.getHtmlTooltip();
		ItemTooltipParser parser = new ItemTooltipParser(itemId, htmlTooltip, TBC);
		parser.parse();
		return parser;
	}
}