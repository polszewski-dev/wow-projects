package wow.scraper.util;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 2023-10-02
 */
@Getter
@Setter
public class SpellTraverser {
	@FunctionalInterface
	public interface SpellHandler {
		void onSpell(Spell spell, int level, int index);
	}

	@FunctionalInterface
	public interface EffectHandler {
		void onEffect(Effect effect, int level, int index);
	}

	private SpellHandler spellHandler = (spell, level, index) -> {};
	private EffectHandler effectHandler = (effect, level, index) -> {};

	private int spellIdx;
	private int effectIdx;

	public SpellTraverser setSpellHandler(SpellHandler spellHandler) {
		this.spellHandler = spellHandler;
		return this;
	}

	public SpellTraverser setEffectHandler(EffectHandler effectHandler) {
		this.effectHandler = effectHandler;
		return this;
	}

	public void traverse(Spell spell) {
		traverse(spell, 0);
	}

	public void traverse(Effect effect) {
		traverse(effect, 0);
	}

	private void traverse(Spell spell, int level) {
		if (spell == null) {
			return;
		}

		onSpell(spell, level, spellIdx++);

		for (var command : spell.getApplyEffectCommands()) {
			traverse(command.effect(), level);
		}
	}

	private void traverse(Effect effect, int level) {//level musi byc
		if (effect == null) {
			return;
		}

		onEffect(effect, level, effectIdx++);

		for (var event : effect.getEvents()) {
			traverse(event.triggeredSpell(), level + 1);
		}
	}

	private void onSpell(Spell spell, int level, int index) {
		spellHandler.onSpell(spell, level, index);
	}

	private void onEffect(Effect effect, int level, int index) {
		effectHandler.onEffect(effect, level, index);
	}
}
