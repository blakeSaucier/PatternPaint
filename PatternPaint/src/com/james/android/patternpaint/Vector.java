package com.james.android.patternpaint;

import android.graphics.PointF;

public class Vector {
	private float mX;
	private float mY;
	
	public Vector() {
		this.mX = 0f;
		this.mY = 0f;
	}
	
	public Vector(float x, float y) {
		this.mX = x;
		this.mY = y;
	}
	
	public Vector(PointF point) {
		this.mX = point.x;
		this.mY = point.y;
	}
	
	public void add(Vector vector) {
		this.mX = this.mX + vector.getX();
		this.mY = this.mY + vector.getY();
	}
	
	public static Vector subtract(Vector vector1, Vector vector2) {
		 float xResult = vector1.getX() - vector2.getX();
		 float yResult = vector1.getY() - vector2.getY();
		 return new Vector(xResult, yResult);
	}
	
	public void subtract(Vector vector) {
		this.mX = this.mX - vector.getX();
		this.mY = this.mY - vector.getY();		
	}
	
	public void multiply(float multiplier) {
		this.mX = this.mX * multiplier;
		this.mY = this.mY * multiplier;
	}
	
	public void multiplyY(float multiplier) {
		this.mY = this.mY * multiplier;
	}
	
	public void multiplyX(float multiplier) {
		this.mX = this.mX * multiplier;
	}
	
	public void divide(float denominator) {
		this.mX = this.mX / denominator;
		this.mY = this.mY / denominator;
	}
	
	public float magnitude() {
		return (float) Math.sqrt((this.mX * this.mX) + (this.mY * this.mY));
	}
	
	public void normalize() {
		float magnitude = this.magnitude();
		if (magnitude != 0) {
			this.divide(magnitude);
		}
	}
	
	public void limit(float max) {
		if (this.magnitude() > max ) {
			this.normalize();
			this.multiply(max);
		}
	}
	
	public Vector get() {
		Vector copy = new Vector(this.mX, this.mY);
		return copy;
	}
	
	public void set(float x, float y) {
		this.mX = x;
		this.mY = y;
	}
	
	public PointF getPointFromVector() {
		return new PointF(getX(), getY());
	}
	
	public float getX() {
		return this.mX;
	}
	
	public float getY() {
		return this.mY;
	}
	
	public void invertX() {
		this.mX = this.mX * -1f;
	}
	
	public void invertY() {
		this.mY = this.mY * -1f;
	}
}
