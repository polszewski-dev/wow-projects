import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'blankZero'
})
export class BlankZeroPipe implements PipeTransform {
	transform(value: number | string | null | undefined, ...args: unknown[]) {
		if (value === null || value === undefined) {
			return "";
		}
		if (value === 0.0) {
			return "";
		}
		if ((typeof value === 'string') && value.match(/^0\.?0*$/)) {
			return "";
		}
		return value.toString();
	}
}
