package wow.simulator.model.unit;

import lombok.Getter;
import wow.character.model.character.Party;
import wow.simulator.model.effect.Auras;

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
		if (member instanceof OnAdd<?> handler) {
			handler.onAdd((UnitParty) this);
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

	public interface OnAdd<M extends Unit> {
		void onAdd(UnitParty<M> party);
	}
}
