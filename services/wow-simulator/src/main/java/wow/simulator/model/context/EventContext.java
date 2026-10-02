package wow.simulator.model.context;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;

import java.util.Objects;

import static wow.commons.model.effect.EffectSource.*;

/**
 * User: POlszewski
 * Date: 2024-11-15
 */
@RequiredArgsConstructor
@Setter
public class EventContext {
	private final Event event;
	private final Effect effect;
	private final Unit caster;
	private final Unit target;
	private final Spell spell;
	private final Context parentContext;

	public void fireEvent() {
		for (var action : event.actions()) {
			performEventAction(action);
		}
	}

	private void performEventAction(EventAction action) {
		switch (action) {
			case TRIGGER_SPELL ->
					triggerSpell(false);
			case REMOVE ->
					((EffectInstance) effect).removeSelf();
			case ADD_STACK ->
					((EffectInstance) effect).addStack();
			case REMOVE_STACK ->
					((EffectInstance) effect).removeStack();
			case REMOVE_CHARGE ->
					((EffectInstance) effect).removeCharge();
			case REMOVE_CHARGE_AND_TRIGGER_SPELL ->
					triggerSpell(true);
			case INCREASE_CASTERS_EFFECT_ON_TARGET_BY_PCT ->
					increaseCastersEffectOnTarget();
			case INCREASE_COUNTERS_BY_LAST_DAMAGE_DONE ->
					increaseCountersByLastDamageDone();
		}
	}

	private void triggerSpell(boolean removeCharge) {
		var triggeredSpell = event.triggeredSpell();

		if (triggeredSpell.hasCooldown()) {
			var cooldownId = CooldownId.of(triggeredSpell);

			caster.triggerCooldown(cooldownId, triggeredSpell.getCooldown());
		}

		if (removeCharge) {
			((EffectInstance) effect).removeCharge();
		}

		var resolutionContext = new SpellResolutionContext(caster, triggeredSpell, parentContext);

		resolutionContext.setSourceSpellOverride(getSourceSpellOverride(triggeredSpell));
		resolutionContext.setValueParam(event.actionParameters().value());
		resolutionContext.resolveTriggeredSpell(target, effect);
	}

	private void increaseCountersByLastDamageDone() {
		var lastDamageDone = ((CommandContext) parentContext).getLastDamageDone();

		((EffectInstance) effect).addCounters(lastDamageDone);
	}

	private void increaseCastersEffectOnTarget() {
		var effectIncreasePct = Objects.requireNonNull(event.actionParameters().value());
		var abilityId = Objects.requireNonNull(event.actionParameters().abilityId());

		var optionalEffectOnTarget = target.getEffect(abilityId, caster);

		optionalEffectOnTarget.ifPresent(effectOnTarget -> effectOnTarget.increaseEffect(effectIncreasePct));
	}

	private Spell getSourceSpellOverride(Spell triggeredSpell) {
		return switch (effect.getSource()) {
			case AbilitySource(var ability) -> ability;
			case TalentSource ignored -> triggeredSpell;
			case ItemSource ignored -> triggeredSpell;
			case null, default -> null;
		};
	}
}
