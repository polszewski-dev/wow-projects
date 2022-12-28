export interface DropdownSelectValueFormatter<T> {
	formatElement:(value: T) => string;
	formatSelection:(value?: T) => string;
	formatTooltip:(value?: T) => string;
}
