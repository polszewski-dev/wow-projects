package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Buffs extends Options<Buff, BuffId> implements EffectCollection, Copyable<Buffs> {
	@Override
	public void collectEffects(EffectCollector collector) {
		forEach(buff -> collector.addEffect(buff.getEffect(), buff.getStacks()));
	}

	@Override
	public Buffs copy() {
		var copy = new Buffs();
		copyInto(copy);
		return copy;
	}

	@Override
	protected BuffId getId(Buff buff) {
		return buff.getId();
	}

	@Override
	protected String getName(Buff buff) {
		return buff.getName();
	}

	@Override
	protected String getKey(Buff buff) {
		if (buff.getExclusionGroup() != null) {
			return buff.getExclusionGroup().toString();
		}

		return buff.getName();
	}
}
