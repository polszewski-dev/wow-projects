package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponentBonus;
import wow.scraper.constant.AttributeConditions;
import wow.scraper.constant.EventConditions;
import wow.scraper.parser.spell.ability.AbilityMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.effect.component.EventAction.REMOVE_CHARGE;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.OWNER_ATTACKED;
import static wow.commons.model.spell.SpellSchool.FIRE;
import static wow.commons.model.spell.SpellSchool.SHADOW;
import static wow.commons.model.spell.component.ComponentCommand.*;
import static wow.scraper.constant.AbilityIds.*;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
class AbilityParserTest extends SpellParserTest {
	@Test
	void incinerate() {
		var ability = parse(INCINERATE, "Deals 444 to 514 Fire damage to your target and an additional 111 to 128 Fire damage if the target is affected by an Immolate spell.");

		assertThat(ability.getDirectCommands()).hasSize(1);

		assertThat(ability.getDirectCommands()).isEqualTo(List.of(
				new DealDamageDirectly(
						SpellTargets.ENEMY,
						SpellTargetCondition.EMPTY,
						new Coefficient(Percent.of(71.43), FIRE),
						444,
						514,
						new DirectComponentBonus(
								111, 128, IMMOLATE
						)
				)
		));
	}

	@Test
	void curseOfAgony() {
		var matcher = getAbilityMatcher(CURSE_OF_AGONY, "Curses the target with agony, causing 1356 Shadow damage over 24 sec. This damage is dealt slowly at first, and builds up as the Curse reaches its full duration. Only one Curse per Warlock can be active on any one target.");
		var ability = matcher.getAbility();

		var effect = ability.getApplyEffectCommands().getFirst().effect();
		var periodicComponent = effect.getPeriodicComponent();
		var tickScheme = new TickScheme(List.of(0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.5, 1.5, 1.5, 1.5));

		assertThat(periodicComponent.commands()).isEqualTo(List.of(
				new DealDamagePeriodically(
						SpellTargets.TARGET,
						new Coefficient(Percent.of(120), SHADOW),
						1356,
						12,
						tickScheme
				)
		));
		assertTickInterval(periodicComponent, 2);
		assertThat(matcher.getAbilityCategory()).isEqualTo(AbilityCategory.CURSES);
	}
	
	@Test
	void immolate() {
		var ability = parse(IMMOLATE, "Burns the enemy for 332 Fire damage and then an additional 615 Fire damage over 15 sec.");

		assertThat(ability.getDirectCommands()).hasSize(1);

		assertThat(ability.getDirectCommands()).isEqualTo(List.of(
				new DealDamageDirectly(
						SpellTargets.ENEMY,
						SpellTargetCondition.EMPTY,
						new Coefficient(Percent.of(18.65), FIRE),
						332,
						332,
						null
				)
		));

		assertEffectApplication(ability, SpellTargets.ENEMY, 15, 1, 1, 1);

		var effect = ability.getApplyEffectCommands().getFirst().effect();
		var periodicComponent = effect.getPeriodicComponent();

		assertThat(periodicComponent.commands()).isEqualTo(List.of(
				new DealDamagePeriodically(
						SpellTargets.TARGET,
						new Coefficient(Percent.of(63.63), FIRE),
						615,
						5,
						TickScheme.DEFAULT
				)
		));

		assertTickInterval(periodicComponent, 3);
	}

	@Test
	void drainLife() {
		var ability = parse(DRAIN_LIFE, "Transfers 108 health every 1 sec from the target to the caster. Lasts 5 sec.");

		assertEffectApplication(ability, SpellTargets.ENEMY, 5, 1, 1, 1);

		var effect = ability.getApplyEffectCommands().getFirst().effect();
		var periodicComponent = effect.getPeriodicComponent();

		assertThat(periodicComponent.commands()).isEqualTo(List.of(
				new DealDamagePeriodically(
						SpellTargets.TARGET,
						new Coefficient(Percent.of(71.43), SHADOW),
						540,
						5,
						TickScheme.DEFAULT
				)/*,
				new Copy(
						SpellTargets.SELF,
						SpellTargetCondition.EMPTY,
						SHADOW,
						From.DAMAGE,
						To.HEAL,
						Percent._100
				)*/
		));

		assertTickInterval(periodicComponent, 1);
	}

	@Test
	void lifeTap() {
		var ability = parse(LIFE_TAP, "Converts 582 health into 582 mana.");

		assertCost(ability.getCost(), 582, ResourceType.HEALTH, 0, 80, null);

		assertThat(ability.getDirectCommands()).isEqualTo(List.of(
				new Copy(
						SpellTargets.SELF,
						SpellTargetCondition.EMPTY,
						null,//SHADOW,
						From.DAMAGE,//HEALTH_PAID,
						To.DAMAGE,//MANA_GAIN,
						Percent._100
				)
		));
	}

	@Test
	void demonArmor() {
		var ability = parse(DEMON_ARMOR, "Protects the caster, increasing armor by 660, Shadow resistance by 18 and restores 18 health every 5 sec. Only one type of Armor spell can be active on the Warlock at any time. Lasts 30 min.");

		assertEffectApplication(ability, SpellTargets.SELF, 30 * 60, 1, 1, 1);

		var effect = ability.getApplyEffectCommands().getFirst().effect();

		assertModifier(effect, List.of(
				Attribute.of(ARMOR, 660),
				Attribute.of(RESISTANCE, 18, AttributeConditions.SHADOW),
				Attribute.of(HP5, 18)
		));
	}

	@Test
	void vampiricEmbrace() {
		var ability = parse(VAMPIRIC_EMBRACE, "Afflicts your target with Shadow energy that causes all party members to be healed for 15% of any Shadow spell damage you deal for 1 min.");
		var command = ability.getApplyEffectCommands().getFirst();
		var event = command.effect().getEvents().getFirst();

		assertDuration(command.duration(), Duration.minutes(1));

		assertEvent(
				event,
				List.of(EventType.SPELL_DAMAGE),
				EventConditions.SHADOW,
				100,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);
	}

	@Test
	void shadowguard() {
		var ability = parse(SHADOWGUARD, "The caster is surrounded by shadows. When a spell, melee or ranged attack hits the caster, the attacker will be struck for 130 Shadow damage. Attackers can only be damaged once every few seconds. This damage causes no threat. 3 charges. Lasts 10 min.");
		var effect = ability.getApplyEffectCommands().getFirst().effect();

		assertEffectApplication(ability, SpellTargets.SELF, 10 * 60, 3, 1, 1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(OWNER_ATTACKED),
				EventCondition.EMPTY,
				100,
				List.of(REMOVE_CHARGE, TRIGGER_SPELL),
				Duration.seconds(1)
		);

		assertThat(event.triggeredSpell().getDirectCommands()).isEqualTo(List.of(
			new DealDamageDirectly(
					SpellTargets.ATTACKER,
					SpellTargetCondition.EMPTY,
					new Coefficient(Percent.of(33.33), SHADOW),
					130,
					130,
					null
			)
		));
	}

	@Test
	void noSpell() {
		var parser = spellPatternRepository.getAbilityParser(SUMMON_DREADSTEED, GameVersionId.TBC);
		var success = parser.tryParse("Summons a Dreadsteed, which serves as a mount for the caster. Speed is increased by 100%.");

		assertThat(success).isFalse();
	}

	@Test
	void incorrectDescription() {
		var parser = spellPatternRepository.getAbilityParser(LIFE_TAP, GameVersionId.TBC);
		boolean success = parser.tryParse("xyz");

		assertThat(success).isFalse();
	}

	private ClassAbility parse(AbilityId abilityId, String line) {
		return getAbilityMatcher(abilityId, line).getAbility();
	}

	private AbilityMatcher getAbilityMatcher(AbilityId abilityId, String line) {
		var parser = spellPatternRepository.getAbilityParser(abilityId, GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getSuccessfulMatcher().orElseThrow();
	}
}
