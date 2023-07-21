/**
 * User: POlszewski
 * Date: 2023-07-20
 */
module wow.scraper {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires spring.beans;
	requires spring.context;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires org.slf4j;

	requires wow.commons;

	exports wow.scraper.classifiers;
	exports wow.scraper.fetchers.impl;
	exports wow.scraper.model;

	opens wow.scraper.config;
	opens wow.scraper.repository.impl;
}