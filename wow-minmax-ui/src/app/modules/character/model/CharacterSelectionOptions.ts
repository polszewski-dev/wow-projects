import { EnemyType } from "../../shared/model/character/EnemyType";
import { LevelDifference } from "../../shared/model/character/LevelDifference";
import { Phase } from "../../shared/model/character/Phase";

export interface CharacterSelectionOptions {
	phases: Phase[];
	enemyTypes: EnemyType[];
	enemyLevelDiffs: LevelDifference[];
	lastModifiedCharacterId: string;
}
