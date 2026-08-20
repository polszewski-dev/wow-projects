package wow.simulator.model.unit;

import lombok.Getter;
import wow.character.model.character.Party;
import wow.simulator.model.effect.Auras;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-08-16
 */
@Getter
public class UnitParty<M extends Unit> extends Party<M> {
	private final Auras auras;

	public UnitParty(UnitRaid<M> raid) {
		super(raid);
		this.auras = new Auras(this);
	}

	@Override
	public UnitRaid<M> getRaid() {
		return (UnitRaid<M>) super.getRaid();
	}

	@Override
	protected void onMemberAdded(M member) {
		if (member instanceof OnAddRemove<?> handler) {
			handler.onAddedToParty((UnitParty) this);
		}
		invalidateAuras();
	}

	@Override
	protected void onMemberRemoved(M member) {
		invalidateAuras();
	}

	public void invalidateAuras() {
		auras.invalidate();
	}

	public void disband() {
		var membersCopy = List.copyOf(getMembers());

		for (M member : membersCopy) {
			remove(member);
			if (member instanceof OnAddRemove<?> handler) {
				handler.onRemovedFromParty();
			}
		}
	}

	public interface OnAddRemove<M extends Unit> {
		void onAddedToParty(UnitParty<M> party);

		void onRemovedFromParty();
	}
}
