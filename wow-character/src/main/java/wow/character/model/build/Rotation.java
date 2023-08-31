package wow.character.model.build;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.character.Character;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-06
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Rotation implements Copyable<Rotation> {
	private final RotationTemplate template;

	private List<Ability> cooldowns;
	private Ability filler;

	public static Rotation create(RotationTemplate template) {
		return new Rotation(template);
	}

	@Override
	public Rotation copy() {
		Rotation copy = new Rotation(template);
		if (this.isCompiled()) {
			copy.cooldowns = new ArrayList<>(this.cooldowns);
			copy.filler = this.filler;
		}
		return copy;
	}

	public static Rotation onlyFiller(Ability ability) {
		Rotation rotation = new Rotation(null);
		rotation.cooldowns = List.of();
		rotation.filler = ability;
		return rotation;
	}

	public Rotation compile(Character character) {
		if (isCompiled()) {
			return this;
		}

		cooldowns = new ArrayList<>();

		for (AbilityId abilityId : template.getAbilityIds()) {
			character.getAbility(abilityId).ifPresent(this::addAbility);
		}

		if (filler == null) {
			this.filler = character.getAbility(AbilityId.SHOOT).orElseThrow();
		}

		return this;
	}

	private boolean isCompiled() {
		return cooldowns != null;
	}

	public Rotation invalidate() {
		this.cooldowns = null;
		this.filler = null;
		return this;
	}

	private void addAbility(Ability ability) {
		if ((hasDotComponent(ability) && !ability.isChanneled()) || ability.getCooldown().isPositive()) {
			cooldowns.add(ability);
		} else if (filler == null) {
			filler = ability;
		} else {
			throw new IllegalArgumentException("Can't have two fillers: %s, %s".formatted(filler, ability));
		}
	}

	private boolean hasDotComponent(Spell spell) {
		var effect = spell.getAppliedEffect();
		if (effect == null) {
			return false;
		}
		var periodicComponent = effect.getPeriodicComponent();
		return periodicComponent != null && periodicComponent.type() == ComponentType.DAMAGE;
	}

	public List<Ability> getAbilities() {
		var result = new ArrayList<Ability>(cooldowns.size() + 1);
		result.addAll(cooldowns);
		result.add(filler);
		return result;
	}
}
