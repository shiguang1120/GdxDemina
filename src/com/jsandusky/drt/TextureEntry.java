package com.jsandusky.drt;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.Serializable;

public class TextureEntry
{
	public transient Sprite sprite;
	public String TextureName;
	public TextureBounds Bounds;
	
	public TextureEntry(Sprite sprite, TextureBounds bounds) {
		this.sprite = sprite;
		Bounds = bounds;
	}
	
	TextureEntry() {
		
	}
}
