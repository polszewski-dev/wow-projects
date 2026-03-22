import { OptionStatus } from './OptionStatus';

export interface OptionGroup<T> {
	groupId: string;
	statuses: OptionStatus<T>[];
}

export function copyOptionGroup<T>(group: OptionGroup<T>) {
	return {
		...group,
		statuses: group.statuses.map(status => ({ ...status }))
	};
}
