package wow.simulator.model.unit;

import lombok.Getter;
import wow.character.model.character.Party;

/**
 * User: POlszewski
 * Date: 2026-08-16
 */
@Getter
public class UnitParty<M extends Unit> extends Party<M> {
	public UnitParty(UnitRaid<M> raid) {
		super(raid);
	}

	@Override
	public UnitRaid<M> getRaid() {
		return (UnitRaid<M>) super.getRaid();
	}

	@Override
	protected void onMemberAdded(M member) {
		if (member instanceof OnAdd<?> handler) {
			handler.onAdd((UnitParty) this);
		}
	}

	public interface OnAdd<M extends Unit> {
		void onAdd(UnitParty<M> party);
	}
}
