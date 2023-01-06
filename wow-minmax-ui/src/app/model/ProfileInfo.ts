import { BuildId } from './character/BuildId';
import { CharacterClass } from './character/CharacterClass';
import { CharacterProfession } from './character/CharacterProfession';
import { CreatureType } from './character/CreatureType';
import { Phase } from './character/Phase';
import { Race } from './character/Race';

export interface ProfileInfo {
	profileId?: string;
	profileName: string;
	characterClass: CharacterClass;
	race: Race;
	level: number;
	enemyType: CreatureType;
	buildId: BuildId;
	professions: CharacterProfession[];
	phase: Phase;
	lastModified?: Date;
}
