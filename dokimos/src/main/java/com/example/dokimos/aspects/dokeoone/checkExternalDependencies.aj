package com.example.dokimos.aspects.dokeoone;

import com.example.dokimos.dokeos;

public aspect checkExternalDependencies {
	pointcut checkGeneral(): call(void checkExternalDependencies());
	
	before() : checkGeneral(){
		DokeoOne doki = new DokeoOne(System.currentTimeMillis());
		doki.checkExternalDependencies();
	}

}