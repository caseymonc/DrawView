package com.itv.drawview;

import com.itv.drawview.Point.Type;

public class Point {

	public enum Type{
		FreeHand,
		Circle,
		Arrow,
		Rectangle,
		X,
		Line
	}
	
	private Point prev;
	private Point next;

	private int color;
	private float thikness;
	
	private boolean isStartingPoint;
	private boolean isEndingPoint;
	private int y;
	private int x;
	private boolean isDrawn;
	private Type type = Type.FreeHand;
	
	
	public Point(int color, float thickness, int x, int y){
		this.color = color;
		this.thikness = thickness;
		this.setEndingPoint(false);
		this.setStartingPoint(false);
		this.setX(x);
		this.setY(y);
		setDrawn(false);
	}
	
	public Point(int color, float thickness, int x, int y, Type type){
		this(color, thickness, x, y);
		this.type = type;
	}
	
	public Point getPrev() {
		return prev;
	}
	
	public void setPrev(Point prev) {
		if(prev != null){
			prev.setNext(this);
		}
		this.prev = prev;
	}
	
	public Point getNext() {
		return next;
	}

	public void setNext(Point next) {
		this.next = next;
	}
	
	public void remove(){
		if(next != null){
			if(isStartingPoint)
				next.setStartingPoint(true);
			next.setPrev(null);
		}
		
		if(prev != null){
			if(isEndingPoint)
				prev.setEndingPoint(true);
			prev.setNext(null);
		}
		
		next = null;
		prev = null;
	}

	public float getThikness() {
		return thikness;
	}
	
	public int getColor() {
		return color;
	}

	public boolean isStartingPoint() {
		return isStartingPoint;
	}

	public void setStartingPoint(boolean isStartingPoint) {
		this.isStartingPoint = isStartingPoint;
	}

	public boolean isEndingPoint() {
		return isEndingPoint;
	}

	public void setEndingPoint(boolean isEndingPoint) {
		this.isEndingPoint = isEndingPoint;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public boolean hasNext() {
		return next != null;
	}

	public boolean isDrawn() {
		return isDrawn;
	}

	public void setDrawn(boolean isDrawn) {
		this.isDrawn = isDrawn;
	}

	public Type getType() {
		return type;
	}

	public Point getLastPoint(){
		Point p = this;
		while(p.hasNext()){
			p = p.getNext();
		}
		
		return p;
	}
}
