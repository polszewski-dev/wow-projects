import { LoadStatus } from './LoadStatus';

export interface Loadable<T> {
	value: T;
	error: string | null;
	status: LoadStatus;
};

export function pending<T>(value: T): Loadable<T> {
	return {
		value, error: null, status: LoadStatus.PENDING
	};
}

export function loading<T>(value: T): Loadable<T> {
	return {
		value, error: null, status: LoadStatus.LOADING
	};
}

export function success<T>(value: T): Loadable<T> {
	return {
		value, error: null, status: LoadStatus.SUCCESS
	};
}

export function failure<T>(value: T, error: string): Loadable<T> {
	return {
		value, error, status: LoadStatus.ERROR
	};
}