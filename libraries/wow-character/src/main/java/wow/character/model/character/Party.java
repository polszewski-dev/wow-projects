package wow.character.model.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
@Getter
@RequiredArgsConstructor
public class Party<M extends Character> {
	public static final int MAX_MEMBERS = 5;

	private final Raid<M> raid;
	private final List<M> members = new ArrayList<>();

	public void add(M member) {
		if (members.size() == MAX_MEMBERS) {
			throw new IllegalArgumentException("Party has already %s characters".formatted(MAX_MEMBERS));
		}

		if (members.contains(member)) {
			return;
		}

		members.add(member);

		if (member instanceof Party.OnAdd<?> handler) {
			handler.onAdd((Party) this);
		}
	}

	public interface OnAdd<M extends Character> {
		void onAdd(Party<M> party);
	}

	@SafeVarargs
	public final void add(M... members) {
		for (var member : members) {
			add(member);
		}
	}

	public void add(Collection<? extends M> members) {
		for (var member : members) {
			add(member);
		}
	}

	public void remove(M member) {
		members.remove(member);
	}

	public boolean canAddAnotherMember() {
		return members.size() < MAX_MEMBERS;
	}

	public int getNumMembers() {
		return members.size();
	}

	public M getFirstMember() {
		return members.isEmpty() ? null : members.getFirst();
	}

	public boolean has(M member) {
		return members.contains(member);
	}

	public void forEach(Consumer<? super M> consumer) {
		members.forEach(consumer);
	}

	public void forEachMemberOrPet(Consumer<? super  M> consumer) {
		members.forEach(consumer);
	}
}
