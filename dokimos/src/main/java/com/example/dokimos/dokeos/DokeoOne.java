package com.example.dokimos.dokeos;

/*
 *		La classe conte els metodes que ens calen per aconseguir el DOKEO ONE
 *		- comprovar periodicament i en background la connexi� a internet
 *		- comprovar periodicament i en background la connexi� a la DB
 *		- pendre les mesures necessaries en els casos innesperats de connexi�
 * 
 * 
 */

import android.util.Log;

public class DokeoOne {
	
	private Long time;
	
	public DokeoOne(Long t){
		this.time = t;
	}
	public void  checkExternalDependencies(){
		// start with internet connection
		Log.d("aspects: ", "try some android here!"+time);
	}

}
