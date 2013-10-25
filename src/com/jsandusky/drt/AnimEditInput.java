package com.jsandusky.drt;
import com.badlogic.gdx.input.*;
import com.badlogic.gdx.math.*;

public class AnimEditInput implements GestureDetector.GestureListener
{
	AnimEditApp app;
	public AnimEditInput(AnimEditApp app) {
		this.app = app;
	}
	public boolean touchDown(float p1, float p2, int p3, int p4)
	{
		return false;
	}

	public boolean tap(float p1, float p2, int p3, int p4)
	{
		return false;
	}

	public boolean longPress(float p1, float p2)
	{
		return false;
	}

	public boolean fling(float p1, float p2, int p3)
	{
		return false;
	}

	public boolean pan(float p1, float p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	public boolean zoom(float p1, float p2)
	{
		return false;
	}

	public boolean pinch(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4)
	{
		return false;
	}
	
}
