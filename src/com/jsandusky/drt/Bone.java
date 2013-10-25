package com.jsandusky.drt;
import com.badlogic.gdx.math.*;

public class Bone
{
	public String Name;
	public boolean Hidden;
	public int ParentIndex;
	public int TextureIndex;
	public transient int SelfIndex;
	
	public transient int UpdateIndex;
	public Vector2 Position;
	public float Rotation;
	public Vector2 Scale;
	public boolean TextureFlipHorizontal;
	public boolean TextureFlipVertical;
	
	public Bone cpy() {
		Bone b = new Bone();
		b.Name = Name;
		b.Hidden = Hidden;
		b.ParentIndex = ParentIndex;
		b.TextureIndex = TextureIndex;
		b.Position = Position.cpy();
		b.Rotation = Rotation;
		b.TextureFlipHorizontal = TextureFlipHorizontal;
		b.TextureFlipVertical = TextureFlipVertical;
		b.SelfIndex = SelfIndex;
		b.UpdateIndex = UpdateIndex;
		return b;
	}
}
