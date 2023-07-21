/**
 * User: POlszewski
 * Date: 2023-07-20
 */
module wow.commons {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires spring.beans;
	requires spring.context;

	exports wow.commons.constants;
	exports wow.commons.model;
	exports wow.commons.model.attributes;
	exports wow.commons.model.attributes.complex;
	exports wow.commons.model.attributes.complex.special;
	exports wow.commons.model.attributes.condition;
	exports wow.commons.model.attributes.primitive;
	exports wow.commons.model.buffs;
	exports wow.commons.model.categorization;
	exports wow.commons.model.character;
	exports wow.commons.model.config;
	exports wow.commons.model.config.impl;
	exports wow.commons.model.item;
	exports wow.commons.model.professions;
	exports wow.commons.model.pve;
	exports wow.commons.model.sources;
	exports wow.commons.model.spells;
	exports wow.commons.model.talents;
	exports wow.commons.repository;
	exports wow.commons.repository.impl;
	exports wow.commons.repository.impl.parsers.excel;
	exports wow.commons.repository.impl.parsers.excel.mapper to wow.scraper;
	exports wow.commons.repository.impl.parsers.items to wow.scraper;
	exports wow.commons.repository.impl.parsers.pve to wow.scraper;
	exports wow.commons.util;
	exports wow.commons.util.parser;

	opens wow.commons.repository.impl to spring.core;
}