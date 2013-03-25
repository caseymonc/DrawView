package com.itv.drawview;

import android.util.SparseArray;

public class Grid<T>{
	
	private SparseArray<SparseArray<T>> grid;
	
	public Grid(){
		grid = new SparseArray<SparseArray<T>>();
	}
	
	public void put(int x, int y, T item){
		SparseArray<T> xArray = grid.get(x);
		if(xArray == null){
			xArray = new SparseArray<T>();
			grid.put(x, xArray);
		}
		
		xArray.put(y, item);
	}
	
	public T get(int x, int y){
		SparseArray<T> xArray = grid.get(x);
		if(xArray == null)
			return null;
		
		return xArray.get(y);
	}
	
	public T remove(int x, int y){
		SparseArray<T> xArray = grid.get(x);
		if(xArray == null)
			return null;
		T result = xArray.get(y);
		xArray.remove(y);
		if(xArray.size() == 0)
			grid.remove(x);
		return result;
	}
	
	public boolean containsKey(int x, int y){
		SparseArray<T> xArray = grid.get(x);
		if(xArray == null)
			return false;
		return xArray.get(y) != null;
	}
	
	public boolean isEmpty(){
		return grid.size() == 0;
	}
	
	public void clear(){
		grid.clear();
	}
}
