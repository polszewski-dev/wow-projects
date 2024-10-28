import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, loading, pending, success } from "../../shared/state/Loadable";
import { ProfileInfo } from "../model/ProfileInfo";
import { loadProfiles, loadProfilesFailure, loadProfilesSuccess, selectProfile } from "./profile.actions";

export interface ProfileState {
	profiles: Loadable<ProfileInfo[]>;
	selectedProfile: ProfileInfo | null;
}

const initialState: ProfileState = {
	profiles: pending([]),
	selectedProfile: null,
};

export const profileReducer = createReducer(
	initialState,
	on(loadProfiles, (state) => ({
		...state,
		profiles: loading([])
	})),
	on(loadProfilesSuccess, (state, { profiles }) => ({
		...state,
		profiles : success(profiles),
		selectedProfile: null,
	})),
	on(loadProfilesFailure, (state, { error }) => ({
		...state,
		profiles: failure([], error),
		selectedProfile: null,
	})),
	on(selectProfile, (state, { selectedProfile }) => ({
		...state,
		selectedProfile
	}))
);
