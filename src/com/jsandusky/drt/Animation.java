package com.jsandusky.drt;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.io.Serializable;

public class Animation implements Serializable
{
	public String Version;

	public int FrameRate;

	public boolean Loop;
	public int LoopFrame;
	public float LoopTime;

	public ArrayList<TextureEntry> Textures;
	public ArrayList<Keyframe> Keyframes;

	public void GetBoneTransformations(ArrayList<BoneTransformation> transforms, ArrayList<BoneTransitionState> transitions, int keyframeIndex, float time)
	{
		Keyframe currentKeyframe = Keyframes.get(keyframeIndex);
		Keyframe nextKeyframe;
		float t;

		if (keyframeIndex == Keyframes.size() - 1)
		{
			nextKeyframe = Keyframes.get(0);

			if (Loop)
				t = time / (LoopTime - currentKeyframe.FrameTime);
			else
				t = 0;
		}
		else
		{
			nextKeyframe = Keyframes.get(keyframeIndex + 1);
			t = time / (nextKeyframe.FrameTime - currentKeyframe.FrameTime);
		}

		for (int boneIndex = 0; boneIndex < Keyframes.get(0).UpdateOrderBones.size(); boneIndex++)
		{
			Vector2 position = currentKeyframe.UpdateOrderBones.get(boneIndex).Position.cpy().lerp(nextKeyframe.UpdateOrderBones.get(boneIndex).Position, t);
			Vector2 scale = currentKeyframe.UpdateOrderBones.get(boneIndex).Scale.cpy().lerp(nextKeyframe.UpdateOrderBones.get(boneIndex).Scale, t);
			float rotation = Interpolation.linear.apply(currentKeyframe.UpdateOrderBones.get(boneIndex).Rotation, nextKeyframe.UpdateOrderBones.get(boneIndex).Rotation, t);

			transitions.get(boneIndex).Position = position;
			transitions.get(boneIndex).Rotation = rotation;

			Matrix4 identity = new Matrix4();
			identity.idt();
			Matrix4 parentTransform = currentKeyframe.UpdateOrderBones.get(boneIndex).ParentIndex == -1 ? null : transforms.get(currentKeyframe.UpdateOrderBones.get(boneIndex).ParentIndex).Transform;

			int drawIndex = currentKeyframe.UpdateOrderBones.get(boneIndex).SelfIndex;

			Matrix4 trans = new Matrix4();
			trans.idt();
			trans.setToTranslation(position.x,position.y,0);			
			
			if (parentTransform != null) {
				Matrix4 mat = transforms.get(drawIndex).Transform;
				if (mat == null)
					mat = new Matrix4();
				mat.set(trans);
				mat.rotate(Vector3.Z, MathUtils.radiansToDegrees * rotation);
				mat = parentTransform.cpy().mul(mat);
				//mat.mul(parentTransform);
				transforms.get(drawIndex).Transform = mat;
			} else {
				Matrix4 mat = transforms.get(drawIndex).Transform;
				if (mat == null)
					mat = new Matrix4();
				mat.set(trans);
				mat.rotate(Vector3.Z, MathUtils.radiansToDegrees * rotation);
				transforms.get(drawIndex).Transform = mat;
			}

			Vector3 position3 = new Vector3(), scale3 = new Vector3();
			Vector3 direction = Vector3.X.cpy();
			Quaternion rotationQ = new Quaternion();

			transforms.get(drawIndex).Transform.getTranslation(position3);
			transforms.get(drawIndex).Transform.getRotation(rotationQ);
			transforms.get(drawIndex).Transform.getScale(scale3);
			
			
			direction = rotationQ.transform(direction);

			transforms.get(drawIndex).Position = new Vector2(position3.x, position3.y);
			transforms.get(drawIndex).Scale = new Vector2(scale3.x, scale3.y);
			transforms.get(drawIndex).Rotation = MathUtils.radiansToDegrees * (float)Math.atan2(direction.y, direction.x);
		}
	}

	public static void GetBoneTransformationsTransition(ArrayList<BoneTransformation> transforms, ArrayList<BoneTransitionState> transitionState, Animation currentAnimation, Animation stopAnimation, float transitionPosition)
	{
		for (int boneIndex = 0; boneIndex < currentAnimation.Keyframes.get(0).UpdateOrderBones.size(); boneIndex++)
		{
			Bone currentBone = currentAnimation.Keyframes.get(0).UpdateOrderBones.get(boneIndex);
			Bone transitionBone = null;

			for (Bone b : stopAnimation.Keyframes.get(0).UpdateOrderBones)
			{
				if (currentBone.Name == b.Name)
				{
					transitionBone = b;
					break;
				}
			}

			if (transitionBone == null)
				continue;

			Vector2 position = transitionState.get(boneIndex).Position.cpy().lerp(transitionBone.Position, transitionPosition);
			Vector2 scale = new Vector2(1, 1);
			float rotation = Interpolation.linear.apply(transitionState.get(boneIndex).Rotation, transitionBone.Rotation, transitionPosition);

			Matrix4 ident = new Matrix4();
			ident.idt();
			Matrix4 parentTransform = currentBone.ParentIndex == -1 ? ident : transforms.get(currentBone.ParentIndex).Transform;

			int drawIndex = currentBone.SelfIndex;

			Matrix4 scl = new Matrix4();
			Matrix4 trn = new Matrix4();
			Matrix4 rot = new Matrix4();
			scl.setToScaling(scale.x,scale.y,1);
			rot.setToRotation(Vector3.Z,MathUtils.radiansToDegrees * rotation);
			trn.setToTranslation(position.x,position.y,0);
			
			transforms.get(drawIndex).Transform = scl.mul(trn).mul(rot).mul(parentTransform);

			Vector3 position3 = new Vector3(), scale3 = new Vector3();
			Vector3 direction = Vector3.X.cpy();
			Quaternion rotationQ = new Quaternion();

			transforms.get(drawIndex).Transform.getScale(scale3);
			transforms.get(drawIndex).Transform.getTranslation(position3);
			transforms.get(drawIndex).Transform.getRotation(rotationQ);
			
			direction = rotationQ.transform(direction);

			transforms.get(drawIndex).Position = new Vector2(position3.x, position3.y);
			transforms.get(drawIndex).Rotation = (float)Math.atan2(direction.y, direction.x);
			transforms.get(drawIndex).Scale = new Vector2(scale3.x, scale3.y);
		}
	}

	public static void UpdateBoneTransitions(ArrayList<BoneTransitionState> transitionState, Animation currentAnimation, Animation stopAnimation, float transitionPosition)
	{
		for (int boneIndex = 0; boneIndex < currentAnimation.Keyframes.get(0).UpdateOrderBones.size(); boneIndex++)
		{
			Bone currentBone = currentAnimation.Keyframes.get(0).UpdateOrderBones.get(boneIndex);
			Bone transitionBone = null;

			for (Bone b : stopAnimation.Keyframes.get(0).UpdateOrderBones)
			{
				if (currentBone.Name == b.Name)
				{
					transitionBone = b;
					break;
				}
			}

			if (transitionBone == null)
				continue;

			transitionState.get(boneIndex).Position = transitionState.get(boneIndex).Position.cpy().lerp(transitionBone.Position, transitionPosition);
			transitionState.get(boneIndex).Rotation = Interpolation.linear.apply(transitionState.get(boneIndex).Rotation, transitionBone.Rotation, transitionPosition);
		}
	}
}
