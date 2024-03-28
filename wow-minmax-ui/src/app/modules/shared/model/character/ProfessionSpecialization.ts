import { Profession } from "./Profession";

export enum ProfessionSpecialization {
	GNOMISH_ENGINEER = 'GNOMISH_ENGINEER',
	GOBLIN_ENGINEER = 'GOBLIN_ENGINEER',
	MASTER_SWORDSMITH = 'MASTER_SWORDSMITH',
	MOONCLOTH_TAILORING = 'MOONCLOTH_TAILORING',
	SHADOWEAVE_TAILORING = 'SHADOWEAVE_TAILORING',
	SPELLFIRE_TAILORING = 'SPELLFIRE_TAILORING',
}

export function getProfessionSpecializations(profession?: Profession) {
	if (!profession) {
		return [];
	}
	if (profession === Profession.TAILORING) {
		return [
			ProfessionSpecialization.MOONCLOTH_TAILORING,
			ProfessionSpecialization.SHADOWEAVE_TAILORING,
			ProfessionSpecialization.SPELLFIRE_TAILORING,
		];
	}
	if (profession === Profession.ENGINEERING) {
		return [
			ProfessionSpecialization.GNOMISH_ENGINEER,
			ProfessionSpecialization.GOBLIN_ENGINEER,
			ProfessionSpecialization.MASTER_SWORDSMITH,
		];
	}
	return [];
}
