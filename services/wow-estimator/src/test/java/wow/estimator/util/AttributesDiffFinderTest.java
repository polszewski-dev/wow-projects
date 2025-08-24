package wow.estimator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Gem;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.model.Player;
import wow.estimator.model.SpecialAbility;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
class AttributesDiffFinderTest extends WowEstimatorSpringTest {
	@Test
	void singleItem() {
		var chest = getItem("Sunfire Robe").gem(orangeGem, orangeGem, orangeGem);

		var finder = new AttributesDiffFinder(character, ItemSlotGroup.CHEST, List.of(chest));
		var diff = finder.getDiff();

		assertThat(diff.attributes().list()).isEqualTo(List.of(
				Attribute.of(STAMINA, 36),
				Attribute.of(INTELLECT, 34),
				Attribute.of(POWER, 76, AttributeCondition.parse("Spell")),
				Attribute.of(POWER, 18, AttributeCondition.parse("SpellDamage")),
				Attribute.of(CRIT_RATING, 40, AttributeCondition.parse("Spell")),
				Attribute.of(HASTE_RATING, 55, AttributeCondition.parse("Spell")),
				Attribute.of(ARMOR, 266)
		));
		assertThat(diff.addedAbilities()).isEmpty();
		assertThat(diff.removedAbilities()).isEmpty();
	}

	@Test
	void meta() {
		var head = getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem);
		var shoulderWrongGem = getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(orangeGem, orangeGem);
		var shoulder = getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem);

		character.equip(head);
		character.equip(shoulderWrongGem);

		var finder = new AttributesDiffFinder(character, ItemSlotGroup.SHOULDER, List.of(shoulder));
		var diff = finder.getDiff();

		assertThat(diff.attributes().list()).isEqualTo(List.of(
				Attribute.of(STAMINA, 7),
				Attribute.of(POWER, 9, AttributeCondition.parse("Spell")),
				Attribute.of(CRIT_RATING, 12, AttributeCondition.parse("Spell")),
				Attribute.of(HASTE_RATING, -5, AttributeCondition.parse("Spell")),
				Attribute.of(CRIT_DAMAGE_PCT, 3)
		));
		assertThat(diff.addedAbilities()).isEmpty();
		assertThat(diff.removedAbilities()).isEmpty();
	}

	@Test
	void setBonus() {
		var t4Shoulder = getItem("Voidheart Mantle");
		var t4Hands = getItem("Voidheart Gloves");

		var t6Head = getItem("Hood of the Malefic");
		var t6Shoulder = getItem("Mantle of the Malefic");

		character.equip(t4Shoulder);
		character.equip(t4Hands);
		character.equip(t6Head);

		var finder = new AttributesDiffFinder(character, ItemSlotGroup.SHOULDER, List.of(t6Shoulder));
		var diff = finder.getDiff();

		assertThat(diff.attributes().list()).isEqualTo(List.of(
				Attribute.of(STAMINA, 19),
				Attribute.of(POWER, 9, AttributeCondition.parse("Spell")),
				Attribute.of(HIT_RATING, 7, AttributeCondition.parse("Spell")),
				Attribute.of(CRIT_RATING, 13, AttributeCondition.parse("Spell")),
				Attribute.of(ARMOR, 31)
		));
		assertThat(getTooltips(diff.addedAbilities())).hasSameElementsAs(List.of(
				"Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health."
		));
		assertThat(getTooltips(diff.removedAbilities())).hasSameElementsAs(List.of(
				"Your fire damage spells have a chance to grant you 135 bonus fire damage for 15 sec. (Proc chance: 5%)",
				"Your shadow damage spells have a chance to grant you 135 bonus shadow damage for 15 sec. (Proc chance: 5%)"
		));
	}

	@Test
	void activatedAbilities() {
		var bloodGem = getItem("Scryer's Bloodgem");
		var silver = getItem("Shifting Naaru Sliver");

		character.equip(bloodGem, ItemSlot.TRINKET_1);

		var finder = new AttributesDiffFinder(character, ItemSlotGroup.TRINKET_1, List.of(silver));
		var diff = finder.getDiff();

		assertThat(diff.attributes().list()).isEqualTo(List.of(
				Attribute.of(HIT_RATING, -32, AttributeCondition.parse("Spell")),
				Attribute.of(HASTE_RATING, 54, AttributeCondition.parse("Spell"))
		));
		assertThat(getTooltips(diff.addedAbilities())).hasSameElementsAs(List.of(
				"Use: Conjures a Power Circle lasting for 15 sec.  While standing in this circle, the caster gains up to 320 spell damage and healing. (1 Min, 30 Sec Cooldown)"
		));
		assertThat(getTooltips(diff.removedAbilities())).hasSameElementsAs(List.of(
				"Use: Increases spell damage by up to 150 and healing by up to 280 for 15 sec. (1 Min, 30 Sec Cooldown)"
		));
	}

	static List<String> getTooltips(List<SpecialAbility> specialAbilities) {
		return specialAbilities.stream().map(SpecialAbility::getTooltip).toList();
	}

	Player character;

	Gem metaGem;
	Gem redGem;
	Gem orangeGem;
	Gem violetGem;

	@BeforeEach
	void setup() {
		character = getCharacter();
		character.resetEquipment();

		metaGem = getGem("Chaotic Skyfire Diamond");
		redGem = getGem(32196);
		orangeGem = getGem("Reckless Pyrestone");
		violetGem = getGem("Glowing Shadowsong Amethyst");
	}
}