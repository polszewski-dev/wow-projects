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

	exports wow.commons.constant;
	exports wow.commons.model;
	exports wow.commons.model.attribute;
	exports wow.commons.model.attribute.complex;
	exports wow.commons.model.attribute.complex.special;
	exports wow.commons.model.attribute.condition;
	exports wow.commons.model.attribute.primitive;
	exports wow.commons.model.buff;
	exports wow.commons.model.categorization;
	exports wow.commons.model.character;
	exports wow.commons.model.config;
	exports wow.commons.model.config.impl;
	exports wow.commons.model.effect;
	exports wow.commons.model.item;
	exports wow.commons.model.profession;
	exports wow.commons.model.pve;
	exports wow.commons.model.source;
	exports wow.commons.model.spell;
	exports wow.commons.model.talent;
	exports wow.commons.repository;
	exports wow.commons.repository.impl;
	exports wow.commons.repository.impl.parser.excel;
	exports wow.commons.repository.impl.parser.excel.mapper to wow.scraper;
	exports wow.commons.repository.impl.parser.item to wow.scraper;
	exports wow.commons.repository.impl.parser.pve to wow.scraper;
	exports wow.commons.repository.impl.parser.spell to wow.scraper;
	exports wow.commons.util;
	exports wow.commons.util.parser;

	opens wow.commons.repository.impl;
}