package com.sb;

import java.util.Arrays;
import java.util.List;

public class SB1 {

	/**
	 * Sandbox of Lambda exception capture patterns.
	 */
	
	public static class X extends Exception {
		public X(String message) {
			super(message);
		}
	}
	public static class XRT extends RuntimeException {
		public XRT(X x) {
			super(x.getMessage(), x);
		}
		public X getX() {
			return (X) super.getCause();
		}
	}
	
	public static void main(String[] args) {
		try {
			trial1();
		}
		catch (X x) {
			x.printStackTrace(System.err);
		}
	}
	
	public static void trial1() throws X {
		
		List<String> coll = Arrays.asList("Foo", "Bar");
		try {
			coll.forEach(s -> getsRT(s));
		}
		catch (XRT xrt) {
			throw xrt.getX();
		}

	}
	
	
	public static String gets(String s) throws X {
		throw new X("hey!");
//		return s;
	}
	
	public static String getsRT(String s) throws XRT {
		try {
			return gets(s);
		}
		catch (X x) {
			throw new XRT(x);
		}
	}
}
