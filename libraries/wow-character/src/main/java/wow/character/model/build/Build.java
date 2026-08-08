package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@AllArgsConstructor
@Getter
@Setter
public class Build implements EffectCollection, Copyable<Build> {
	private final GameVersion gameVersion;
	private final Talents talents;
	private PveRole role;
	private String script;

	public Build(GameVersion gameVersion, Talents talents) {
		this.gameVersion = gameVersion;
		this.talents = talents;
		reset();
	}

	@Override
	public Build copy() {
		return new Build(
				gameVersion,
				talents.copy(),
				role,
				script
		);
	}

	public void reset() {
		this.talents.reset();
		this.role = null;
		this.script = null;
		invalidate();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		talents.collectEffects(collector);
	}

	public boolean hasTalent(String name) {
		return talents.has(name);
	}

	public boolean hasTalent(String name, int rank) {
		return talents.has(name, rank);
	}

	public void invalidate() {
		// void
	}
}
