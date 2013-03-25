package com.itv.drawview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PointGrid extends Grid<List<Point>> {

	private Set<Point> startingPoints;
	private Set<Point> endingPoints;
	private Stack<Point> undo;
	private Stack<Point> redo;
	
	public PointGrid(){
		super();
		startingPoints = new HashSet<Point>();
		endingPoints = new HashSet<Point>();
		undo = new Stack<Point>();
		redo = new Stack<Point>();
	}
	
	public void put(int x, int y, Point item){
		List<Point> list = this.get(x, y);
		if(list == null){
			list = new ArrayList<Point>();
			put(x, y, list);
		}
		
		list.add(item);
		
		if(item.isEndingPoint())
			endingPoints.add(item);
		
		if(item.isStartingPoint()){
			startingPoints.add(item);
			undo.push(item);
		}
	}
	
	public Point getTop(int x, int y){
		List<Point> list = this.get(x, y);
		if(list != null){
			return list.get(list.size() - 1);
		}
		
		return null;
	}
	
	
	public List<Point> remove(int x, int y){
		List<Point> list = super.remove(x, y);
		for(Point p : list){
			if(p.isStartingPoint()){
				startingPoints.remove(p);
				startingPoints.add(p.getNext());
			}
			
			if(p.isEndingPoint()){
				endingPoints.remove(p);
				endingPoints.add(p.getPrev());
			}
			
			p.remove();
		}
		
		
		return list;
	}
	
	public Set<Point> getStartingPoints(){
		return new HashSet<Point>(startingPoints);
	}
	
	public Set<Point> getEndingPoints(){
		return endingPoints;
	}
	
	private void redraw(){
		Set<Point> starts = getStartingPoints();
		for(Point p : starts){
			p.setDrawn(false);
		}
	}

	public boolean undo(){
		if(undo.size() == 0)
			return false;
		Point p = undo.get(undo.size() - 1);
		undo.remove(undo.get(undo.size() - 1));
		this.startingPoints.remove(p);
		redo.push(p);
		redraw();
		return true;
	}
	
	public boolean redo(){
		if(redo.size() == 0)
			return false;
		Point p = redo.get(redo.size() - 1);
		redo.remove(redo.get(redo.size() - 1));
		this.startingPoints.add(p);
		undo.push(p);
		redraw();
		return true;
	}
	
	public void remove(int x, int y, Point lastPoint) {
		List<Point> points = get(x, y);
		points.remove(lastPoint);
	}

	public void clear(){
		super.clear();
		startingPoints = new HashSet<Point>();
		endingPoints = new HashSet<Point>();
		undo = new Stack<Point>();
		redo = new Stack<Point>();
	}
}
