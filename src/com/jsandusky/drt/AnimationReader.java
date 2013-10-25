package com.jsandusky.drt;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.*;
import java.util.*;
import com.badlogic.gdx.math.*;

/*
 <Animation>
 	<FrameRate>50</FrameRate>
 	<LoopFrame>80</LoopFrame>
 	<Texture>guy_torso.png</Texture>
 	<Texture>guy_head.png</Texture>
 	<Texture>guy_upper_arm.png</Texture>
 	<Texture>guy_lower_arm.png</Texture>
 	<Texture>guy_hand.png</Texture>
 	<Texture>guy_upper_leg.png</Texture>
 	<Texture>guy_lower_leg.png</Texture>
 	<Texture>guy_foot.png</Texture>	
 <Keyframe frame="0" trigger="" vflip="False" hflip="False">
 	<Bone name="back_hand">
 		<Hidden>False</Hidden>
 		<TextureFlipHorizontal>False</TextureFlipHorizontal>
 		<TextureFlipVertical>False</TextureFlipVertical>
 		<ParentIndex>1</ParentIndex>
 		<TextureIndex>4</TextureIndex>
 		<Position>
 			<X>-0.2998418</X>
 			<Y>35.013</Y>
 		</Position>
 		<Rotation>0.19</Rotation>
 		<Scale>
 			<X>1</X>
 			<Y>1</Y>
 		</Scale>
 	</Bone>
 </KeyFrame>
</Animation>
*/
public class AnimationReader
{
	public interface ImageResolver {
		public Sprite getImage(String name);
	}
	
	ImageResolver imgSrc;
	public AnimationReader(ImageResolver resolver) {
		imgSrc = resolver;
	}
	
	public Animation read(FileHandle handle) {
		XmlReader rdr = new XmlReader();
		try {
			
			Element root = rdr.parse(handle);
			int frameRate = Integer.parseInt(root.getChildByName("FrameRate").getText());
			int loopFrame = Integer.parseInt(root.getChildByName("LoopFrame").getText());
			
			Animation anim = new Animation();
			anim.FrameRate = frameRate;
			anim.LoopFrame = loopFrame;
			anim.Textures = new ArrayList<TextureEntry>();
			anim.Loop = loopFrame != -1;
			anim.Keyframes = new ArrayList<Keyframe>();
			
			for (int i = 0; i < root.getChildCount(); ++i) {
				Element ch = root.getChild(i);
				if (ch.getName().equals("Texture")) {
					TextureAtlas.AtlasSprite reg = (TextureAtlas.AtlasSprite)imgSrc.getImage(ch.getText());
					if (reg != null) {
						anim.Textures.add(new TextureEntry(reg,
								new TextureBounds(
										new Rectangle(0,0,reg.getWidth(),reg.getHeight()),
										new Vector2(reg.getAtlasRegion().originalWidth/2,reg.getAtlasRegion().originalHeight/2))));
					} else {
						throw new Exception("Unable to resolve image: " + ch.getText());
					}
				}
			}
			
			for (int i = 0; i < root.getChildCount(); ++i) {
				Element ch = root.getChild(i);
				if (ch.getName().equals("Keyframe")) {					
					Keyframe frame = new Keyframe();
					frame.Bones = new ArrayList<Bone>();
					frame.FrameNumber = ch.getIntAttribute("frame");
					frame.Trigger = ch.getAttribute("trigger","");
					frame.FlipHorizontally = ch.getAttribute("hflip","False").equals("True");
					frame.FlipVertically = ch.getAttribute("vflip","False").equals("True");
					
					for (int j = 0; j < ch.getChildCount(); ++j) {
						Element bone = ch.getChild(j);
						if (bone.getName().equals("Bone")) {							
							Element posElem = bone.getChildByName("Position");
							Element sclElem = bone.getChildByName("Scale");
							Vector2 pos = new Vector2();
							Vector2 scl = new Vector2();
							
							pos.x = Float.parseFloat(posElem.getChildByName("X").getText());
							pos.y = Float.parseFloat(posElem.getChildByName("Y").getText()) * -1;
							
							scl.x = Float.parseFloat(sclElem.getChildByName("X").getText());
							scl.y = Float.parseFloat(sclElem.getChildByName("Y").getText());
							
							Bone b = new Bone();
							b.Hidden = bone.getChildByName("Hidden").getText().equals("True");
							b.Name = bone.getAttribute("name");
							b.TextureFlipHorizontal = bone.getChildByName("TextureFlipHorizontal").getText().equals("True");
							b.TextureFlipVertical = bone.getChildByName("TextureFlipVertical").getText().equals("True");
							b.ParentIndex = Integer.parseInt(bone.getChildByName("ParentIndex").getText());
							b.TextureIndex = Integer.parseInt(bone.getChildByName("TextureIndex").getText());
							b.Rotation = Float.parseFloat(bone.getChildByName("Rotation").getText()) * -1;
							
							b.Position = pos;
							b.Scale = scl;
							b.SelfIndex = j;
							
							frame.Bones.add(b);
						}
					}
					
					frame.SortBones();
					anim.Keyframes.add(frame);
				}
				
				float fr = 1.0f / anim.FrameRate;
				anim.LoopTime = anim.LoopFrame * fr;
				anim.Loop = anim.LoopFrame != -1;
				for (Keyframe kf : anim.Keyframes) {
					kf.FrameTime = fr * kf.FrameNumber;
				}
			}
			
			return anim;
		} catch (Exception ex) {
			Gdx.app.log("AnimationReader","read",ex);
			return null;
		}
	}
}
