import { PhaseId } from "../../shared/model/character/PhaseId";

export interface CharacterIdParts {
	profileId: string;
	phaseId: PhaseId;
	level: number
	enemyTypeId: string;
	enemyLevelDiff: number;
}

const SEPARATOR = ',';

export function formatCharacterId(idParts: CharacterIdParts) {
	return (idParts.profileId + SEPARATOR +
			idParts.phaseId + SEPARATOR +
			idParts.level + SEPARATOR +
			idParts.enemyTypeId + SEPARATOR +
			idParts.enemyLevelDiff).toLowerCase();
}

export function parseCharacterId(value: string): CharacterIdParts {
	const parts = value.split(SEPARATOR);

	return {
			profileId: parts[0],
			phaseId: parts[1] as PhaseId,
			level: Number.parseInt(parts[2]),
			enemyTypeId: parts[3],
			enemyLevelDiff: Number.parseInt(parts[4])
	};
}
