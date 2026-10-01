package wow.simulator.model.context;

import lombok.Getter;
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
@Setter
public class EventContext extends Context {
	private final Event event;
	private final Effect effect;
	private final Unit target;
	@Getter
	private final Context parentContext;

	public EventContext(Event event, Effect effect, Unit caster, Unit target, Spell spell, Context parentContext) {
		super(caster, spell);
		this.event = event;
		this.effect = effect;
		this.target = target;
		this.parentContext = parentContext;
	}

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
					((EffectInstance) effect).addStack(this);
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

		var resolutionContext = new SpellResolutionContext(caster, triggeredSpell, this);

		resolutionContext.setSourceSpellOverride(getSourceSpellOverride(triggeredSpell));
		resolutionContext.setValueParam(event.actionParameters().value());
		resolutionContext.resolveTriggeredSpell(target, effect);
	}

	private void increaseCountersByLastDamageDone() {
		var lastDamageDone = getParentCommandContext().getLastDamageDone();

		((EffectInstance) effect).addCounters(lastDamageDone, this);
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

	@Override
	public String toString() {
		return "%4s. EventContext %s, spell: %s, caster: %s, target: %s".formatted(
				event.types().size() == 1 ? event.types().getFirst() : event.types(), serialNo, spell, caster, target
		);
	}
}
