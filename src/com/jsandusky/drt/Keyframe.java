package com.jsandusky.drt;
import java.util.ArrayList;
import java.io.Serializable;

public class Keyframe implements Serializable
{
	public int FrameNumber;
	public ArrayList<Bone> Bones;
	public String Trigger = "";
	public boolean FlipVertically;
	public boolean FlipHorizontally;

	public transient float FrameTime;
	public transient ArrayList<Bone> UpdateOrderBones;
	
	public Keyframe cpy() {
		Keyframe ret = new Keyframe();
		ret.FrameNumber = FrameNumber+1;
		for (Bone b : Bones) {
			ret.Bones.add(b.cpy());
		}		
		ret.Trigger = "";
		ret.FlipVertically = FlipVertically;
		ret.FlipHorizontally = FlipHorizontally;
		ret.SortBones();
		return ret;
	}

	public Keyframe()
	{
		UpdateOrderBones = new ArrayList<Bone>();
	}

	public void SortBones()
	{
		UpdateOrderBones.clear();

		for (Bone bone : Bones)
		{
			BoneSortAdd(bone);
		}
	}

	protected void BoneSortAdd(Bone b)
	{
		if (UpdateOrderBones.contains(b))
			return;

		if (b.ParentIndex != -1)
			BoneSortAdd(Bones.get(b.ParentIndex));

		UpdateOrderBones.add(b);
		b.UpdateIndex = UpdateOrderBones.size() - 1;
	}
}
