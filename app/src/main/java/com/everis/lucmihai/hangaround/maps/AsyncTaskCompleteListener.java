package com.everis.lucmihai.hangaround.maps;

/**
 * Created by lucmihai on 11/10/2016.
 * This is frome here: http://stackoverflow.com/questions/28854875/java-asynctask-passing-variable-to-main-thread/28855151#28855151
 *
 *
 */

public interface AsyncTaskCompleteListener<T> {
	public void onTaskComplete(T result, int number);

}
