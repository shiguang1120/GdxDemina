package com.jsandusky.drt;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.math.*;

public class TestApp implements ApplicationListener
{
	TextureAtlas atlas;
	SpriteBatch batch;
	Camera camera;
	AnimationPlayer player;
	Vector2 pos;
	int mod = 0;
	float runningTime = 0f;
	
	public void create()
	{
		atlas = new TextureAtlas(Gdx.files.internal("data/texatlas.pack"),false);
		batch = new SpriteBatch();
		pos = new Vector2(200,200);
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w, h);
		//((OrthographicCamera)camera).setToOrtho(true,w,h);
		//camera.position.x += w/2;
		//camera.position.y += h/2;
		camera.update();
		
		AnimationReader rdr = new AnimationReader(new AnimationReader.ImageResolver() {
			public Sprite getImage(String name)
			{
				String n = name.substring(0,name.indexOf("."));
				return new TextureAtlas.AtlasSprite(TestApp.this.atlas.findRegion(n)); 
			}
		});
		Animation walk = rdr.read(Gdx.files.internal("data/guy_walk.anim"));
		
		player = new AnimationPlayer();
		player.add("walk",walk);
		
		walk.Loop = true;
		//walk.LoopTime = 10f;
		player.startAnimation("walk");
	}

	public void resize(int p1, int p2)
	{
	}

	public void render()
	{
		mod += 1;
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
			player.update(Gdx.graphics.getDeltaTime());
		batch.begin();
			player.draw(batch,pos);
		batch.end();
	}

	public void pause()
	{
	}

	public void resume()
	{
	}

	public void dispose()
	{
		atlas.dispose();
		batch.dispose();
	}

}
