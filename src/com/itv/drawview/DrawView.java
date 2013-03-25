package com.itv.drawview;

import java.util.Set;

import com.itv.drawview.Point.Type;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class DrawView extends SurfaceView implements Runnable{

	private Type type = Type.FreeHand;
	
	private PointGrid grid = new PointGrid();
	
	private int color = Color.RED;
	private float thickness = 6;
	private int maxThickness = 15;
	private int minThickness = 3;
	private Paint paint;
	private Bitmap bitmap;

	private boolean erase = false;
	private Point lastPoint;
	
	private boolean running;
	private SurfaceHolder holder;
	private Thread thread;	
	
	
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(thickness);
		paint.setStyle(Paint.Style.STROKE);
		holder = getHolder();
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(erase){
			erase(event);
		}else{
			if(type == Type.FreeHand){
				draw(event);
			}else{				
				drawShape(event, type);
			}
		}
		return true;
	}

	private void erase(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			grid.remove((int)event.getX(), (int)event.getY());
			break;
		}
	}
	
	private Point firstPoint;

	private boolean clear;
	private void drawShape(MotionEvent event, Type type) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			thickness = (maxThickness - minThickness)/2;
			
			if(firstPoint == null){
				firstPoint = new Point(color, thickness, x, y, type);
				firstPoint.setStartingPoint(true);
				grid.put(x, y, firstPoint);
				break;
			}else if(lastPoint == null){
				lastPoint = new Point(color, thickness, x, y, type);
				lastPoint.setPrev(firstPoint);
				grid.put(x, y, lastPoint);
			}else{
				grid.remove(lastPoint.getX(), lastPoint.getY(), lastPoint);
				lastPoint.setX(x);
				lastPoint.setY(y);
				grid.put(x, y, lastPoint);
			}
			
			
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if(lastPoint != null) lastPoint.setEndingPoint(true);
			firstPoint = null;
			lastPoint = null;
			break;
		}
	}

	private void draw(MotionEvent event){
		Point point = null;
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			thickness = (maxThickness - minThickness)/2;
			break;
		case MotionEvent.ACTION_MOVE:
			thickness = calculateThickness();
			point = new Point(color, thickness, x, y);
			if(lastPoint == null)
				point.setStartingPoint(true);
			else
				point.setPrev(lastPoint);
			grid.put(x, y, point);
			lastPoint = point;
			
			break;
		case MotionEvent.ACTION_CANCEL:
			lastPoint = null;
			break;
		case MotionEvent.ACTION_UP:
			point = new Point(color, thickness, x, y);
			point.setEndingPoint(true);
			grid.put(x, y, point);
			point.setPrev(lastPoint);
			lastPoint = null;
			break;
		}
	}

	private float calculateThickness() {
		if(lastPoint != null && lastPoint.getPrev() != null){
			double distance = distance(lastPoint.getX(), lastPoint.getY(), lastPoint.getPrev().getX(), lastPoint.getPrev().getY());
			distance -= 20;
			distance /= 40;
			
			Log.e("Distance", "Distance: " + distance);
			
			thickness -= distance;
			
			thickness = Math.max(minThickness, thickness);
			thickness = Math.min(maxThickness, thickness);
		}
		return thickness;
	}

	private double distance(int x, int y, int x2, int y2) {
		
		return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
	}

	@Override
	public void run() {
		while (running) {
			
				if (!holder.getSurface().isValid())
					continue;
				
				Canvas c = holder.lockCanvas();
				
				if (c == null)
					continue;
				
				if(clear){
					bitmap.recycle();
					bitmap = null;
					clear = false;
				}
				
				Canvas canvas;
				if(bitmap == null){
					bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bitmap);
					canvas.drawColor(Color.WHITE);
				}else{
					canvas = new Canvas(bitmap);
				}
				
				Set<Point> startingPoints = grid.getStartingPoints();
				for(Point p : startingPoints){
					if(p.getType() == Type.FreeHand){
						if(p.getLastPoint().isEndingPoint()){
							drawFreeHand(canvas, p);
							p.setDrawn(true);
						}
					}else if(p.getType() == Type.Circle){
						if(p.getNext() != null && p.getNext().isEndingPoint()){
							drawCircle(canvas, p);
							p.setDrawn(true);
						}
					}else if(p.getType() == Type.Rectangle){
						if(p.getNext() != null && p.getNext().isEndingPoint()){
							drawSquare(canvas, p);
							p.setDrawn(true);
						}
					}else if(p.getType() == Type.X){
						if(p.getNext() != null && p.getNext().isEndingPoint()){
							drawX(canvas, p);
							p.setDrawn(true);
						}
					}else if(p.getType() == Type.Arrow){
						if(p.getNext() != null && p.getNext().isEndingPoint()){
							drawArrow(canvas, p);
							p.setDrawn(true);
						}
					}else if(p.getType() == Type.Line){
						if(p.getNext() != null && p.getNext().isEndingPoint()){
							drawLine(canvas, p);
							p.setDrawn(true);
						}
					}
					
					
				}
				
				
				c.drawBitmap(bitmap, 0, 0, paint);
				
				for(Point p : startingPoints){
					if(p.getType() == Type.FreeHand){
						if(lastPoint != null && !lastPoint.isEndingPoint()){
							drawFreeHand(canvas, p);
						}
					}else if(p.getType() == Type.Circle){
						if(p.getNext() != null && !p.getNext().isEndingPoint()){
							drawCircle(c, p);
						}
					}else if(p.getType() == Type.Rectangle){
						if(p.getNext() != null && !p.getNext().isEndingPoint()){
							drawSquare(c, p);
						}
					}else if(p.getType() == Type.X){
						if(p.getNext() != null && !p.getNext().isEndingPoint()){
							drawX(c, p);
						}
					}else if(p.getType() == Type.Arrow){
						if(p.getNext() != null && !p.getNext().isEndingPoint()){
							drawArrow(c, p);
						}
					}else if(p.getType() == Type.Line){
						if(p.getNext() != null && !p.getNext().isEndingPoint()){
							drawLine(c, p);
						}
					}
					
				}
				
				holder.unlockCanvasAndPost(c);
				
			
		}
	}

	private void drawCircle(Canvas canvas, Point start) {
		if(start.isDrawn())
			return;
		Point end = start.getNext();
		int centerX = (start.getX() + end.getX())/2;
		int centerY = (start.getY() + end.getY())/2;
		int radius = (int) distance(centerX, centerY, start.getX(), start.getY());
		paint.setColor(end.getColor());
		paint.setStrokeWidth(end.getThikness());
		canvas.drawCircle(centerX, centerY, radius, paint);
	}
	
	private void drawArrow(Canvas canvas, Point start) {
		if(start.isDrawn())
			return;
		Point end = start.getNext();
		paint.setColor(end.getColor());
		paint.setStrokeWidth(end.getThikness());
		
		
		Path path = new Path();
		path.moveTo(start.getX(), start.getY());
		path.lineTo(end.getX(), end.getY());
		
		canvas.drawPath(path, paint);
	}
	
	private void drawLine(Canvas canvas, Point start) {
		if(start.isDrawn())
			return;
		Point end = start.getNext();
		paint.setColor(end.getColor());
		paint.setStrokeWidth(end.getThikness());
		
		
		Path path = new Path();
		path.moveTo(start.getX(), start.getY());
		path.lineTo(end.getX(), end.getY());
		
		canvas.drawPath(path, paint);
	}
	
	private void drawX(Canvas canvas, Point start) {
		if(start.isDrawn())
			return;
		Point end = start.getNext();
		paint.setColor(end.getColor());
		paint.setStrokeWidth(end.getThikness());
		
		Path path = new Path();
		path.moveTo(start.getX(), start.getY());
		path.lineTo(end.getX(), end.getY());
		path.moveTo(start.getX(), end.getY());
		path.lineTo(end.getX(), start.getY());
		
		canvas.drawPath(path, paint);
	}
	
	private void drawSquare(Canvas canvas, Point start) {
		if(start.isDrawn())
			return;
		Point end = start.getNext();
		paint.setColor(end.getColor());
		paint.setStrokeWidth(end.getThikness());
		canvas.drawRect(start.getX(), start.getY(), end.getX(), end.getY(), paint);
	}

	private void drawFreeHand(Canvas canvas, Point p) {
		if(p.isDrawn())
			return;
		Point point = p;
		while(point.hasNext()){
			Point next = point.getNext();
			Path path = new Path();
			path.moveTo(point.getX(), point.getY());
			path.quadTo((point.getX() + next.getX()) / 2, (point.getY() + next.getY()) / 2, next.getX(), next.getY());
			paint.setColor(next.getColor());
			paint.setStrokeWidth(next.getThikness());
			canvas.drawPath(path, paint);
			point = next;
		}
	}
	
	public void clear(){
		grid.clear();
		clear = true;
	}
	
	public void undo(){
		grid.undo();
		clear = true;
	}
	
	public void redo(){
		grid.redo();
		clear = true;
	}
	
	
	public void pause() {
		running = false;
		//AsyncRequest.cancelForContext(this.getContext());
	}

	public void resume() {
		if (running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void setMaxThickness(int thickness){
		this.maxThickness = thickness;
	}
	
	public void setType(Type type){
		this.type = type;
	}
	
	public Type getType(){
		return type;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public int getColor(){
		return color;
	}
	
}
