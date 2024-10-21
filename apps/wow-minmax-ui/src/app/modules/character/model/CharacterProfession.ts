import { Profession } from '../../shared/model/character/Profession';
import { ProfessionSpecialization } from '../../shared/model/character/ProfessionSpecialization';

export interface CharacterProfession {
	profession?: Profession;
	specialization?: ProfessionSpecialization;
	level: number;
}
