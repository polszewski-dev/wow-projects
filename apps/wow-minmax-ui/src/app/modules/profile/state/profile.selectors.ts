import { createSelector } from "@ngrx/store";
import { ProfileModuleState } from "./profile-module.state";

const selectProfileState = (state: ProfileModuleState) => state.profile;

export const selectProfiles = createSelector(
	selectProfileState,
	state => state.profiles.value
)

export const selectSelectedProfile = createSelector(
	selectProfileState,
	state => state.selectedProfile
)
