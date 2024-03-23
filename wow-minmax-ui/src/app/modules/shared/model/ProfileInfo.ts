import { CharacterClass } from './character/CharacterClass';
import { Race } from './character/Race';

export interface ProfileInfo {
	profileId?: string;
	profileName: string;
	characterClass: CharacterClass;
	race: Race;
	lastModified?: Date;
	lastUsedCharacterId?: string;
}
