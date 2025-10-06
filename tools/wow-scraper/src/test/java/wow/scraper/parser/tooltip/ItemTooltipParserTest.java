package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.Money;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.config.TimeRestriction;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.ItemType.*;
import static wow.commons.model.categorization.WeaponSubType.STAFF;
import static wow.commons.model.categorization.WeaponSubType.WAND;
import static wow.commons.model.item.SocketType.*;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.scraper.constant.AttributeConditions.*;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
class ItemTooltipParserTest extends TooltipParserTest<JsonItemDetails, ItemTooltipParser, WowheadItemCategory> {
	@Test
	@DisplayName("Item id/name are parsed correctly")
	void idAndName() {
		assertThat(sunfireRobe.getItemId()).isEqualTo(34364);
		assertThat(sunfireRobe.getName()).isEqualTo("Sunfire Robe");
	}

	@Test
	@DisplayName("Basic info is parsed correctly")
	void basicInfo() {
		assertThat(sunfireRobe.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P5));
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

		var hoodSocketBonus = hoodOfMalefic.getSocketBonus().orElseThrow();
		assertEffect(hoodSocketBonus, POWER, 5, SPELL, "+5 Spell Damage and Healing");

		assertThat(sunfireRobe.getSocketTypes()).isEqualTo(List.of(RED, RED, RED));

		var robeSocketBonus = sunfireRobe.getSocketBonus().orElseThrow();
		assertEffect(robeSocketBonus, POWER, 5, SPELL, "+5 Spell Damage and Healing");
	}

	@Test
	@DisplayName("Stats are parsed correctly")
	void stats() {
		var parsedEffects = sunfireRobe.getItemStatParser().getItemEffects();

		assertThat(parsedEffects).hasSize(6);

		assertEffect(parsedEffects, 0, ARMOR, 266, "266 Armor");
		assertEffect(parsedEffects, 1, STAMINA, 36, "+36 Stamina");
		assertEffect(parsedEffects, 2, INTELLECT, 34, "+34 Intellect");
		assertEffect(parsedEffects, 3, CRIT_RATING, 40, SPELL, "Equip: Improves spell critical strike rating by 40.");
		assertEffect(parsedEffects, 4, HASTE_RATING, 40, SPELL, "Equip: Improves spell haste rating by 40.");
		assertEffect(parsedEffects, 5, POWER, 71, SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 71.");
	}

	@Test
	void noWeaponStatsOnNonWeapon() {
		assertThat(sunfireRobe.getWeaponStats()).isNull();
	}

	@Test
	void weaponStatsOnNWeapon() {
		var weaponStats = magistersStaff.getWeaponStats();

		assertThat(weaponStats.damageMin()).isEqualTo(146);
		assertThat(weaponStats.damageMax()).isEqualTo(326);
		assertThat(weaponStats.damageType()).isNull();
		assertThat(weaponStats.dps()).isEqualTo(73.75);
		assertThat(weaponStats.speed()).isEqualTo(Duration.seconds(3.20));
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
		var itemSetBonuses = hoodOfMalefic.getItemSetBonuses();
		assertThat(itemSetBonuses).hasSize(2);
		assertThat(itemSetBonuses.get(0).numPieces()).isEqualTo(2);
		assertEffect(itemSetBonuses.get(0).bonusEffect(), 231051, "Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health.");
		assertThat(itemSetBonuses.get(1).numPieces()).isEqualTo(4);
		assertEffect(
				itemSetBonuses.get(1).bonusEffect(),
				Attributes.of(
						Attribute.of(DAMAGE_PCT, 6, AttributeCondition.comma(SHADOW_BOLT, INCINERATE))
				 ),
				"Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%."
		);
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
		assertThat(sunfireRobe.getRequiredClass()).isNull();
		assertThat(sunfireRobe.getRequiredRace()).isNull();
		assertThat(sunfireRobe.getRequiredSide()).isNull();

	}

	@Test
	@DisplayName("Misc info is parsed correctly")
	void misc() {
		assertThat(sunfireRobe.hasRandomEnchant()).isFalse();
		assertThat(sunfireRobe.getDroppedBy()).isNull();
		assertThat(sunfireRobe.getDropChance()).isNull();
		assertThat(sunfireRobe.getSellPrice()).isEqualTo(Money.parse("6g8s49c"));
	}

	ItemTooltipParser hoodOfMalefic;
	ItemTooltipParser sunfireRobe;
	ItemTooltipParser scryersBlodgem;
	ItemTooltipParser magistersStaff;
	ItemTooltipParser wandOfDemonsoul;

	@BeforeEach
	void readTestData() {
		hoodOfMalefic = getTooltip(31051, WowheadItemCategory.HEAD);
		sunfireRobe = getTooltip(34364, WowheadItemCategory.CHEST);
		scryersBlodgem = getTooltip(29132, WowheadItemCategory.TRINKETS);
		magistersStaff = getTooltip(34182, WowheadItemCategory.STAVES);
		wandOfDemonsoul = getTooltip(34347, WowheadItemCategory.WANDS);
	}

	@Override
	protected JsonItemDetails getData(int id, WowheadItemCategory category) {
		return itemDetailRepository.getDetail(TBC, category, id).orElseThrow();
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails data) {
		return new ItemTooltipParser(data, TBC, scraperContext);
	}

}