package wow.character.model.build;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.character.Character;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;

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

	private List<Spell> cooldowns;
	private Spell filler;

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

	public static Rotation onlyFiller(Spell spell) {
		Rotation rotation = new Rotation(null);
		rotation.cooldowns = List.of();
		rotation.filler = spell;
		return rotation;
	}

	public Rotation compile(Character character) {
		if (isCompiled()) {
			return this;
		}

		cooldowns = new ArrayList<>();

		for (SpellId spellId : template.getSpellIds()) {
			character.getSpellbook()
					.getSpell(spellId)
					.ifPresent(this::addSpell);
		}

		if (filler == null) {
			this.filler = character.getSpellbook()
					.getSpell(SpellId.SHOOT)
					.orElseThrow();
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

	private void addSpell(Spell spell) {
		if ((spell.hasDotComponent() && !spell.isChanneled()) || spell.getCooldown().isPositive()) {
			cooldowns.add(spell);
		} else if (filler == null) {
			filler = spell;
		} else {
			throw new IllegalArgumentException("Can't have two fillers: %s, %s".formatted(filler, spell));
		}
	}
}
