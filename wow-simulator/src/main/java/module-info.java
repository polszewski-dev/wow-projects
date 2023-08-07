/**
 * User: POlszewski
 * Date: 2023-08-07
 */
module wow.simulator {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires java.desktop;
	requires spring.beans;
	requires spring.context;
	requires org.slf4j;

	requires wow.commons;
	requires wow.character;

	exports wow.simulator;

	opens wow.simulator;
	opens wow.simulator.config;
}
