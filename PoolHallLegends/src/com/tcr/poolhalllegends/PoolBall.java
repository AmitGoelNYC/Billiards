package com.tcr.poolhalllegends;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


public class PoolBall extends AnimatedSprite{

	public boolean isPocketed = false;
	public int ballNumber = 0;
	public float timeGreater = 0;  
	public Body body;
	public float timeBallPocketed = -1;
	public final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, .8f, 0.2f);
	
	public PoolBall(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager vertexBufferObjectManager,
			PhysicsWorld pWorld) {
		super(pX, pY, pTiledTextureRegion,	vertexBufferObjectManager);
		body = PhysicsFactory.createCircleBody(pWorld,this, BodyType.DynamicBody, objectFixtureDef);
		body.setLinearDamping(.28f);
		body.setAngularDamping(.28f);
		body.setUserData(PoolBall.this);
	    pWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
	} 
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		Vector2 getLinVelocity = getLinVelocityPoolBall();
		float linVelX = Math.abs(getLinVelocity.x);
		float linVelY = Math.abs(getLinVelocity.y);
		if(this.ballNumber == 16 && linVelX >0 && linVelY >0){
			Log.d("yeman","linear velocity x = "+ linVelX +" linear velocity y = "+ linVelY);
		}
		
		if(linVelX < 0.15f && linVelY < 0.15f){
			setLinVelocityPoolBall(0.0f, 0.0f);
		}
		
		//if(faceBody.getLinearVelocity())
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public Vector2 getLinVelocityPoolBall(){
		return body.getLinearVelocity();
	}
	
	public void setLinVelocityPoolBall(float x, float y){
		body.setLinearVelocity(x,y);
	}
}