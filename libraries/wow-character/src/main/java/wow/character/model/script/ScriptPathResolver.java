package wow.character.model.script;

import wow.character.model.character.PlayerCharacter;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2025-09-27
 */
public final class ScriptPathResolver {
	public static String getScriptPath(String scriptName, GameVersionId gameVersionId) {
		if (scriptName.startsWith("/")) {
			return scriptName;
		}

		var mainDir = "/wow/character/script";
		var gameVersionDir = gameVersionId.toString().toLowerCase();

		return "%s/%s/%s.txt".formatted(mainDir, gameVersionDir, scriptName);
	}

	public static String getScriptPath(String scriptName, GameVersion gameVersion) {
		return getScriptPath(scriptName, gameVersion.getGameVersionId());
	}

	public static String getScriptPath(PlayerCharacter player) {
		var scriptName = player.getBuild().getScript();

		return getScriptPath(scriptName, player.getGameVersionId());
	}

	private ScriptPathResolver() {}
}
