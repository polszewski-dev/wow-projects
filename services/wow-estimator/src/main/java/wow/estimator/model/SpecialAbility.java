package wow.estimator.model;

import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.ActivatedAbility;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-11-10
 */
public record SpecialAbility(Effect effect, ActivatedAbility ability) implements Described {
	public SpecialAbility {
		if (effect == null) {
			Objects.requireNonNull(ability);
		} else if (ability == null) {
			Objects.requireNonNull(effect);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static SpecialAbility of(Effect effect) {
		return new SpecialAbility(effect, null);
	}

	public static SpecialAbility of(ActivatedAbility ability) {
		return new SpecialAbility(null, ability);
	}

	public void consume(Consumer<Effect> effectConsumer, Consumer<ActivatedAbility> abilityConsumer) {
		if (effect != null) {
			effectConsumer.accept(effect);
		} else {
			abilityConsumer.accept(ability);
		}
	}

	@Override
	public Description getDescription() {
		return effect != null ? effect.getDescription() : ability.getDescription();
	}

	public EffectSource getSource() {
		return effect != null ? effect.getSource() : ability.getSource();
	}

	public int getPriority() {
		return ability != null ? 1 : 2;
	}
}
