package com.supers.demo;

import java.util.Arrays;
import java.util.List;

public class Test {
	
	public static void main(String[] args) {
		List<Integer> strings = Arrays.asList(1,2,3,4,56,76,88,3,7);
		strings.stream().sorted().forEach(System.out::println);
	}


}
