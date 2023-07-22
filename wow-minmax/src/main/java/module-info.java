/**
 * User: POlszewski
 * Date: 2023-07-20
 */
module wow.minmax {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.core;
	requires spring.web;
	requires org.slf4j;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;

	requires wow.commons;
	requires wow.character;

	exports wow.minmax;
	exports wow.minmax.config;
	exports wow.minmax.controller;
	exports wow.minmax.converter;
	exports wow.minmax.converter.dto;
	exports wow.minmax.converter.persistent;
	exports wow.minmax.converter.spring;
	exports wow.minmax.model;
	exports wow.minmax.model.config;
	exports wow.minmax.model.dto;
	exports wow.minmax.model.persistent;
	exports wow.minmax.repository;
	exports wow.minmax.repository.impl;
	exports wow.minmax.service;
	exports wow.minmax.service.impl;
	exports wow.minmax.util;

	opens wow.minmax to spring.core;
	opens wow.minmax.config to spring.core;
	opens wow.minmax.controller to spring.core;
	opens wow.minmax.converter.spring to spring.core;
	opens wow.minmax.repository.impl to spring.core;
	opens wow.minmax.service.impl to spring.core;
}