package wow.estimator.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.script.ScriptPathResolver;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.Pet;
import wow.commons.model.pve.GameVersion;
import wow.estimator.model.Rotation;
import wow.estimator.model.RotationTemplate;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
@Getter
@Setter
public class BuildWithRotation extends Build {
	private Rotation rotation;

	public BuildWithRotation(GameVersion gameVersion, Talents talents, PveRole role, Rotation rotation, String script, Pet activePet) {
		super(gameVersion, talents, role, script, activePet);
		this.rotation = rotation;
	}

	public BuildWithRotation(GameVersion gameVersion, Talents talents) {
		super(gameVersion, talents);
	}

	@Override
	public BuildWithRotation copy() {
		return new BuildWithRotation(
				getGameVersion(),
				getTalents().copy(),
				getRole(),
				null,
				getScript(),
				getActivePet()
		);
	}

	@Override
	public void invalidate() {
		this.rotation = null;
	}

	@Override
	public void setScript(String script) {
		super.setScript(script);
		this.rotation = null;
	}

	public Rotation getRotation() {
		if (rotation == null) {
			var scriptPath = ScriptPathResolver.getScriptPath(getScript(), getGameVersion());

			this.rotation = RotationTemplate.parse(scriptPath).createRotation();
		}
		return rotation;
	}
}
