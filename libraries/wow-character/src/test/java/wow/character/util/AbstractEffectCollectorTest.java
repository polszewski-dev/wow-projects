package wow.character.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Gem;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
class AbstractEffectCollectorTest extends WowCharacterSpringTest {
	@Test
	void activatedAbilities() {
		character.equip(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		character.equip(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);

		effectCollector.solveAll();

		assertThat(effectCollector.abilities).isEqualTo(List.of(
				"Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)",
				"Use: Conjures a Power Circle lasting for 15 sec. While standing in this circle, the caster gains up to 320 spell damage and healing. (1 Min, 30 Sec Cooldown)"
		));
	}

	@Test
	void setBonuses2Pieces() {
		character.equip(getItem("Mantle of the Malefic"));
		character.equip(getItem("Bracers of the Malefic"));

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEqualTo(List.of(
				"Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health."
		));
	}

	@Test
	void setBonuses4Pieces() {
		character.equip(getItem("Mantle of the Malefic"));
		character.equip(getItem("Bracers of the Malefic"));
		character.equip(getItem("Belt of the Malefic"));
		character.equip(getItem("Boots of the Malefic"));

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEqualTo(List.of(
				"Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health.",
				"Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%."
		));
	}

	@Test
	void setBonusesHasRequiredProfession() {
		assertThat(character.hasProfession(ProfessionId.TAILORING, 350)).isTrue();

		character.equip(getItem("Spellstrike Hood"));
		character.equip(getItem("Spellstrike Pants"));

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEqualTo(List.of(
				"Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)"
		));
	}

	@Test
	void setBonusesHasNotRequiredProfession() {
		character.resetProfessions();

		assertThat(character.hasProfession(ProfessionId.TAILORING, 350)).isFalse();

		character.equip(getItem("Spellstrike Hood"));
		character.equip(getItem("Spellstrike Pants"));

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEmpty();
	}

	@Test
	void metaSocketMetaConditionMet() {
		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		character.equip(getItem("Hood of the Corruptor").gem(metaGem, violetGem));//mv
		character.equip(getItem("Mantle of the Malefic").gem(violetGem, orangeGem));//by

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEqualTo(List.of(
				"+12 Spell Critical",
				"3% Increased Critical Damage",
				"+6 Spell Damage",
				"+7 Stamina",
				"+5 Spell Damage and Healing"
		));
	}

	@Test
	void metaSocketMetaConditionNotMet() {
		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		character.equip(getItem("Hood of the Corruptor").gem(metaGem, violetGem));//mv
		character.equip(getItem("Mantle of the Malefic").gem(orangeGem, orangeGem));//by

		onlySetBonusesAndMeta();

		assertThat(effectCollector.effects).isEqualTo(List.of(
				"+6 Spell Damage",
				"+7 Stamina"
		));
	}

	private void onlySetBonusesAndMeta() {
		effectCollector.collectEffects();
		effectCollector.effects.clear();
		effectCollector.collectRest();
	}

	PlayerCharacter character;
	TestEffectCollector effectCollector;

	@BeforeEach
	void setup() {
		character = getCharacter();
		character.resetBuffs();
		character.resetEquipment();
		character.resetBuild();

		effectCollector = new TestEffectCollector(character);
	}

	static class TestEffectCollector extends AbstractEffectCollector {
		final List<String> effects = new ArrayList<>();
		final List<String> abilities = new ArrayList<>();

		TestEffectCollector(Character character) {
			super(character);
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			effects.add(effect.getTooltip() + (stackCount != 1 ? "#" + stackCount : ""));
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			abilities.add(activatedAbility.getTooltip());
		}
	}
}