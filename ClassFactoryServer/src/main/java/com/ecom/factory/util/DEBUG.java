package com.ecom.factory.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DEBUG {
	private String prefix;
	public void print(String msg) {
		System.out.println("["+prefix+"] ~ "+msg);
	}
}
