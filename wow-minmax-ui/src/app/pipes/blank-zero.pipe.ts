import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'blankZero'
})
export class BlankZeroPipe implements PipeTransform {
	transform(value: number | string | null, ...args: unknown[]): string {
		if (value === null) {
			return "";
		}
		if (value === 0.0) {
			return "";
		}
		if ((value as string).match(/^0\.0*$/)) {
			return "";
		}
		return value.toString();
	}
}
