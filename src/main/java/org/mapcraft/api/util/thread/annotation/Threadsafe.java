/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util.thread.annotation;

public @interface Threadsafe {
	public String author() default "MapCraftDev";

	public String version() default "1.0";

	public String shortDescription() default "Indicates that the method is inherently thread-safe.";
}