package com.lti.data.recasttableaumigrator.model;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(a(6));
	}
	
	public static int a(int n) {
		if(n<=1) {
			return n;
		}
		System.out.println(a(n-1));
		System.out.println(a(n-2));
		return a(n-1)+a(n-2);
		}
	}


