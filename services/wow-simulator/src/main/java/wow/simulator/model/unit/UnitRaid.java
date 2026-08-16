package wow.simulator.model.unit;

import wow.character.model.character.Party;
import wow.character.model.character.Raid;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-08-16
 */
public class UnitRaid<M extends Unit> extends Raid<M> {
	public static <M extends Unit> UnitRaid<M> newUnitRaid(M member) {
		var raid = new UnitRaid<M>();
		raid.getFirstParty().add(member);
		return raid;
	}

	@Override
	protected Party<M> newParty() {
		return new UnitParty<>(this);
	}

	@Override
	public List<UnitParty<M>> getParties() {
		return (List<UnitParty<M>>) super.getParties();
	}

	@Override
	public UnitParty<M> getFirstParty() {
		return (UnitParty<M>) super.getFirstParty();
	}

	@Override
	public UnitParty<M> getParty(int idx) {
		return (UnitParty<M>) super.getParty(idx);
	}
}
