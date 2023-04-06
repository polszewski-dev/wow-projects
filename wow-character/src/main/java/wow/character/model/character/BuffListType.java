package wow.character.model.character;

import lombok.Getter;
import wow.commons.model.buffs.Buff;

import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2023-04-06
 */
@Getter
public enum BuffListType {
	CHARACTER_BUFF(x -> !x.isDebuff()),
	TARGET_DEBUFF(Buff::isDebuff);

	private final Predicate<Buff> filter;

	BuffListType(Predicate<Buff> filter) {
		this.filter = filter;
	}
}
