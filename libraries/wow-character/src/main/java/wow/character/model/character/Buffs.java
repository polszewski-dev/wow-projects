package wow.character.model.character;

import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
public class Buffs extends Options<Buff, BuffId> implements EffectCollection {
	private final Character owner;

	public Buffs(Character owner) {
		this.owner = owner;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		forEach(buff -> collector.addEffect(buff.getEffect()));
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

	@Override
	protected void optionEnabled(Buff buff) {
		if (buff != null) {
			owner.buffEnabled(buff);
		}
	}

	@Override
	protected void optionDisabled(Buff buff) {
		if (buff != null) {
			owner.buffDisabled(buff);
		}
	}
}
