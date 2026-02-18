package wow.character.model.script;

import wow.character.model.character.PlayerCharacter;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;

import java.util.Objects;

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

	public static void requireExistingScriptFile(String scriptName, GameVersionId gameVersionId) {
		if (scriptName == null) {
			return;
		}

		var path = getScriptPath(scriptName, gameVersionId);
		var url = ScriptPathResolver.class.getResource(path);

		Objects.requireNonNull(url, "No script in " + path);
	}

	private ScriptPathResolver() {}
}
