package wow.estimator.model;

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

		for (var abilityId : template.getAbilityIds()) {
			character.getAbility(abilityId).ifPresent(this::addAbility);
		}

		removeMutuallyExclusiveAbilities();

		if (filler == null) {
			this.filler = character.getAbility(AbilityId.SHOOT).orElseThrow();
		}

		return this;
	}

	private boolean isCompiled() {
		return cooldowns != null;
	}

	private void addAbility(Ability ability) {
		if (!ability.hasDamagingComponent()) {
			return;
		}

		if ((hasDotComponent(ability) && !ability.isChanneled()) || ability.hasCooldown()) {
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
		return effect.hasPeriodicComponent(ComponentType.DAMAGE);
	}

	private void removeMutuallyExclusiveAbilities() {
		if (hasCooldown(CURSE_OF_DOOM) && hasCooldown(CURSE_OF_AGONY)) {
			cooldowns.removeIf(x -> x.getAbilityId().equals(CURSE_OF_AGONY));
		}
	}

	private boolean hasCooldown(AbilityId abilityId) {
		return cooldowns.stream().anyMatch(x -> x.getAbilityId().equals(abilityId));
	}

	private static final AbilityId CURSE_OF_DOOM = AbilityId.of("Curse of Doom");
	private static final AbilityId CURSE_OF_AGONY = AbilityId.of("Curse of Agony");

	public List<Ability> getAbilities() {
		var result = new ArrayList<Ability>(cooldowns.size() + 1);
		result.addAll(cooldowns);
		result.add(filler);
		return result;
	}
}
