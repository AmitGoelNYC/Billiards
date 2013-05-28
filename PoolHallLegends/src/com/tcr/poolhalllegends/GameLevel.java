package com.tcr.poolhalllegends;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.extension.physics.box2d.util.triangulation.EarClippingTriangulator;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.list.ListUtils;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseExponentialIn;

import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameLevel extends ManagedGameScene implements IOnSceneTouchListener {
	private PhysicsWorld mPhysicsWorld;
	private PoolBall[] balls;

    private Sprite mPoolCue;
    private Rectangle line2;

	private boolean mLocked = false, canMoveCueBall = true, ballTypeDecided = false;
	private Line noPassBreakLine;
	private Entity stickTouchArea, LineHolder;
	private MoveYModifier shoot;
	private Sprite leftBottomPocket, leftTopPocket;
	private int playerBallType = -1;
	private String[] ballTypes = {"Solids","Stripes"};
	
	@Override
	public void onLoadScene() {
		super.onLoadScene();
		int newColor = MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR);
		float[] colors = GrowToggleRectangle.getColors(newColor);
		this.setBackground(new Background(colors[0], colors[1], colors[2],colors[3]));
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		//final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,0.5f, 0.5f);
		this.registerUpdateHandler(this.mPhysicsWorld);
		final FixtureDef uniqueBodyFixture = PhysicsFactory.createFixtureDef(1f, .8f, .2f);
		
		/** Bottom Left Portion of Table Fixture **/
		List<Vector2> UniqueBodyVerticesTopLeft =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesTopLeft,50f,0f,90f,40f,
				ResourceManager.getInstance().cameraWidth/2-33,40, ResourceManager.getInstance().cameraWidth/2-30,0f);
		List<Vector2> UniqueBodyVerticesTriangulatedTopLeft = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesTopLeft);
		float[] MeshTrianglesTopLeft = new float[UniqueBodyVerticesTriangulatedTopLeft.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedTopLeft,MeshTrianglesTopLeft);		
		Mesh UniqueBodyMeshTopLeft = new Mesh(0f, 0f, MeshTrianglesTopLeft, UniqueBodyVerticesTriangulatedTopLeft.size(), 
				org.andengine.entity.primitive.DrawMode.TRIANGLES, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		Body UniqueBodyTopLeft = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshTopLeft, 
				UniqueBodyVerticesTriangulatedTopLeft, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshTopLeft, UniqueBodyTopLeft));
		UniqueBodyMeshTopLeft.setColor(1f, 0f, 0f);
		this.attachChild(UniqueBodyMeshTopLeft);
		
		Sprite woodPanelBottomLeft = new Sprite( 0, 0, 
				ResourceManager.mWoodPanelTextureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelBottomLeft.setPosition(70, 0);
		woodPanelBottomLeft.setAnchorCenter(0, 0);
		//woodPanelBottomLeft.setScaleCenterX(.7f);
		this.attachChild(woodPanelBottomLeft);
		
		//** Bottom Right Portion of Table Fixture **/
		List<Vector2> UniqueBodyVerticesTopRight =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesTopRight, ResourceManager.getInstance().cameraWidth/2f+30f,0f,
				ResourceManager.getInstance().cameraWidth/2f+33f,40f,ResourceManager.getInstance().cameraWidth-90f,
				40f,ResourceManager.getInstance().cameraWidth-50f,0f);
		List<Vector2> UniqueBodyVerticesTriangulatedTopRight = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesTopRight);
		float[] MeshTrianglesTopRight = new float[UniqueBodyVerticesTriangulatedTopRight.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedTopRight, MeshTrianglesTopRight);
		Mesh UniqueBodyMeshTopRight = new Mesh(0f, 0f, MeshTrianglesTopRight, UniqueBodyVerticesTriangulatedTopRight.size(), 
				org.andengine.entity.primitive.DrawMode.TRIANGLES, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		Body UniqueBodyTopRight = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshTopRight, 
				UniqueBodyVerticesTriangulatedTopRight, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshTopRight, UniqueBodyTopRight));
		UniqueBodyMeshTopRight.setColor(1f, 0f, 0f);
		this.attachChild(UniqueBodyMeshTopRight);
		
		Sprite woodPanelBottomRight = new Sprite( 0, 0, 
		ResourceManager.mWoodPanelTextureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelBottomRight.setPosition(ResourceManager.getInstance().cameraWidth/2+30f, 
				0);
		woodPanelBottomRight.setAnchorCenter(0,0);
		this.attachChild(woodPanelBottomRight);

		//** Top Left Portion of Table Fixture **//
		List<Vector2> UniqueBodyVerticesBottomLeft =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesBottomLeft,50f,ResourceManager.getInstance().cameraHeight,
				90f,ResourceManager.getInstance().cameraHeight-40,ResourceManager.getInstance().cameraWidth/2-33f,
				ResourceManager.getInstance().cameraHeight-40f,ResourceManager.getInstance().cameraWidth/2-30f,
				ResourceManager.getInstance().cameraHeight);
		List<Vector2> UniqueBodyVerticesTriangulatedBottomLeft = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesBottomLeft);
		float[] MeshTrianglesBottomLeft = new float[UniqueBodyVerticesTriangulatedBottomLeft.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedBottomLeft, MeshTrianglesBottomLeft);
		Mesh UniqueBodyMeshBottomLeft = new Mesh(0f, 0f, MeshTrianglesBottomLeft, 
				UniqueBodyVerticesTriangulatedBottomLeft.size(), org.andengine.entity.primitive.DrawMode.TRIANGLES, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		UniqueBodyMeshBottomLeft.setColor(1f, 0f, 0f);
		Body UniqueBodyBottomLeft = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshBottomLeft, 
				UniqueBodyVerticesTriangulatedBottomLeft, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshBottomLeft, UniqueBodyBottomLeft));
		this.attachChild(UniqueBodyMeshBottomLeft);
		
		Sprite woodPanelTopLeft = new Sprite(0, 0, ResourceManager.mWoodPanelTextureRegion, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelTopLeft.setPosition(70,ResourceManager.getInstance().cameraHeight);
		woodPanelTopLeft.setAnchorCenter(0,1);
		this.attachChild(woodPanelTopLeft);
		
		//** Top Right Portion of Table Fixture **/
		List<Vector2> UniqueBodyVerticesBottomRight =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesBottomRight,ResourceManager.getInstance().cameraWidth/2+30f,
				ResourceManager.getInstance().cameraHeight,	ResourceManager.getInstance().cameraWidth/2+33f,
				ResourceManager.getInstance().cameraHeight-40f,ResourceManager.getInstance().cameraWidth-90f,
				ResourceManager.getInstance().cameraHeight-40f,ResourceManager.getInstance().cameraWidth-50f,
				ResourceManager.getInstance().cameraHeight);
		List<Vector2> UniqueBodyVerticesTriangulatedBottomRight = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesBottomRight);
		float[] MeshTrianglesBottomRight = new float[UniqueBodyVerticesTriangulatedBottomRight.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedBottomRight, MeshTrianglesBottomRight);
		Mesh UniqueBodyMeshBottomRight = new Mesh(0f, 0f, MeshTrianglesBottomRight, 
				UniqueBodyVerticesTriangulatedBottomRight.size(), org.andengine.entity.primitive.DrawMode.TRIANGLES, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		UniqueBodyMeshBottomRight.setColor(1f, 0f, 0f);
		Body UniqueBodyBottomRight = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshBottomRight, 
				UniqueBodyVerticesTriangulatedBottomRight, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshBottomRight, UniqueBodyBottomRight));
		this.attachChild(UniqueBodyMeshBottomRight);
		
		Sprite woodPanelTopRight = new Sprite( 0, 0,ResourceManager.mWoodPanelTextureRegion, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelTopRight.setPosition(ResourceManager.getInstance().cameraWidth/2f+30f, ResourceManager.getInstance().cameraHeight);
		woodPanelTopRight.setAnchorCenter(0, 1);
		this.attachChild(woodPanelTopRight);
		
		//** Left Portion of Table Fixture **/
		List<Vector2> UniqueBodyVerticesLeft =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesLeft, 0f,40f,50f,90f,50f,ResourceManager.getInstance().cameraHeight-90,
				0,ResourceManager.getInstance().cameraHeight-40);
		List<Vector2> UniqueBodyVerticesTriangulatedLeft = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesLeft);
		float[] MeshTrianglesLeft = new float[UniqueBodyVerticesTriangulatedLeft.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedLeft, MeshTrianglesLeft);
		Mesh UniqueBodyMeshLeft = new Mesh(0f, 0f, MeshTrianglesLeft, UniqueBodyVerticesTriangulatedLeft.size(), 
				org.andengine.entity.primitive.DrawMode.TRIANGLES, ResourceManager.getEngine().getVertexBufferObjectManager());
		UniqueBodyMeshLeft.setColor(1f, 0f, 0f);
		Body UniqueBodyLeft = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshLeft, 
				UniqueBodyVerticesTriangulatedLeft, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshLeft, UniqueBodyLeft));
		this.attachChild(UniqueBodyMeshLeft);
		
		Sprite woodPanelLeft = new Sprite( 0, 0, 
				ResourceManager.mWoodPanelTextureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelLeft.setRotationCenterX(0);
		woodPanelLeft.setRotation(90);
		woodPanelLeft.setAnchorCenter(0, 0);
		woodPanelLeft.setPosition(0, ResourceManager.getInstance().cameraHeight-62);
		woodPanelLeft.setScaleX(1.18f);
		woodPanelLeft.setScaleY(1.3f);
		this.attachChild(woodPanelLeft);
		
		//** Right Portion of Table Fixture **/
		List<Vector2> UniqueBodyVerticesRight =new ArrayList<Vector2>();
		addVertices(UniqueBodyVerticesRight, ResourceManager.getInstance().cameraWidth,40f,
				ResourceManager.getInstance().cameraWidth-50f,90f,ResourceManager.getInstance().cameraWidth-50f,
				ResourceManager.getInstance().cameraHeight-90f, ResourceManager.getInstance().cameraWidth,
				ResourceManager.getInstance().cameraHeight-40f);
		List<Vector2> UniqueBodyVerticesTriangulatedRight = new EarClippingTriangulator().computeTriangles(UniqueBodyVerticesRight);
		float[] MeshTrianglesRight = new float[UniqueBodyVerticesTriangulatedRight.size()*3];
		createTriangles(UniqueBodyVerticesTriangulatedRight, MeshTrianglesRight);
		Mesh UniqueBodyMeshRight = new Mesh(0f, 0f,
				MeshTrianglesRight, UniqueBodyVerticesTriangulatedRight.size(), 
				org.andengine.entity.primitive.DrawMode.TRIANGLES, 
				ResourceManager.getEngine().getVertexBufferObjectManager());
		UniqueBodyMeshRight.setColor(1f, 0f, 0f);
		Body UniqueBodyRight = PhysicsFactory.createTrianglulatedBody(mPhysicsWorld, UniqueBodyMeshRight, 
				UniqueBodyVerticesTriangulatedRight, BodyType.StaticBody, uniqueBodyFixture);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector( UniqueBodyMeshRight, UniqueBodyRight));
		this.attachChild(UniqueBodyMeshRight);
		
		Sprite woodPanelRight = new Sprite( 0, 0, 
				ResourceManager.mWoodPanelTextureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
		woodPanelRight.setRotationCenterX(0);
		woodPanelRight.setRotation(90);
		woodPanelRight.setAnchorCenter(0, 1);
		woodPanelRight.setPosition(ResourceManager.getInstance().cameraWidth, ResourceManager.getInstance().cameraHeight-62);
		woodPanelRight.setScaleX(1.18f);
		woodPanelRight.setScaleY(1.3f);
		this.attachChild(woodPanelRight);
		
		this.setOnSceneTouchListener(this);
		setupBalls();
	}
	
	private void createTriangles(List<Vector2> uniqueBodyVerticesTriangulated, float[] meshTriangles) {
		for(int i = 0; i<uniqueBodyVerticesTriangulated.size();i++){
			meshTriangles[i*3]   =   uniqueBodyVerticesTriangulated.get(i).x;
			meshTriangles[i*3+1] =   uniqueBodyVerticesTriangulated.get(i).y;
			uniqueBodyVerticesTriangulated.get(i).mul(1/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		}
	}

	@SuppressWarnings("unchecked")
	private void addVertices(List<Vector2> uniqueBodyVertices, float x1,
			float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		uniqueBodyVertices.addAll((List<Vector2>)
			ListUtils.toList(
				new Vector2[]{
					new Vector2(x1,y1),	new Vector2(x2,y2),
					new Vector2(x3,y3),	new Vector2(x4,y4)
				}
			)
		);	
	}

	private void setupBalls() {
		balls = new PoolBall[16];
		final VertexBufferObjectManager vertexBufferObjectManager = ResourceManager.getEngine().getVertexBufferObjectManager();
		noPassBreakLine = new Line(ResourceManager.getInstance().cameraWidth/4, 40, 
				ResourceManager.getInstance().cameraWidth/4, ResourceManager.getInstance().cameraHeight-40, vertexBufferObjectManager);
		noPassBreakLine.setLineWidth(3);
		noPassBreakLine.setColor(Color.WHITE);
		this.attachChild(noPassBreakLine);
				
		balls[2] = new PoolBall(ResourceManager.getInstance().cameraWidth * 0.65f, 
				ResourceManager.getInstance().cameraHeight / 2,
				ResourceManager.mBallRedTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		float nextCalcX = balls[2].getX()+balls[2].getWidth()-8;
		float nextCalcY = balls[2].getY();
		balls[8] = new PoolBall(nextCalcX, nextCalcY+14,
				ResourceManager.mBallYellowStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[9] = new PoolBall(nextCalcX, nextCalcY-14,
				ResourceManager.mBallBlueStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += balls[2].getWidth()-8;
		balls[7] = new PoolBall(nextCalcX, nextCalcY,
				ResourceManager.mBallBlackTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcY = balls[7].getY(); 
		balls[4] = new PoolBall(nextCalcX, nextCalcY+30,
				ResourceManager.mBallOrangeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[6] = new PoolBall(nextCalcX, nextCalcY-30,
				ResourceManager.mBallPinkTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += balls[2].getWidth()-8;
		balls[3] = new PoolBall(nextCalcX, balls[8].getY(),
				ResourceManager.mBallPurpleTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[13] = new PoolBall(nextCalcX, balls[9].getY(),
				ResourceManager.mBallGreenStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[10] = new PoolBall(nextCalcX, balls[3].getY()+30,
				ResourceManager.mBallRedStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[12] = new PoolBall(nextCalcX, balls[13].getY()-30,
				ResourceManager.mBallOrangeStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += balls[2].getWidth()-8;
		balls[5] = new PoolBall(nextCalcX, balls[7].getY(),
				ResourceManager.mBallGreenTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[14] = new PoolBall(nextCalcX, balls[5].getY()+30,
				ResourceManager.mBallPinkStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[11] = new PoolBall(nextCalcX, balls[5].getY()-30,
				ResourceManager.mBallPurpleStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[0] = new PoolBall(nextCalcX, balls[11].getY()-30,
				ResourceManager.mBallYellowTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[1] = new PoolBall(nextCalcX, balls[14].getY()+30,
				ResourceManager.mBallBlueTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		balls[15] = new PoolBall(ResourceManager.getInstance().cameraWidth/4 -balls[1].getWidth()/2,
				ResourceManager.getInstance().cameraHeight/2,
				ResourceManager.mBallWhiteTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.d("yeman2","moving cueball");
				float x = pSceneTouchEvent.getX();
				float y = pSceneTouchEvent.getY();
				float plbX = leftBottomPocket.getWidth()*(leftBottomPocket.getScaleX()/2)+6F;
				float plbY = leftBottomPocket.getHeight()*(leftBottomPocket.getScaleY()/2);
				float pltX = leftTopPocket.getWidth()*(leftTopPocket.getScaleX()/2)+6f;
				float pltY = leftTopPocket.getY()-(leftTopPocket.getHeight()*(leftTopPocket.getScaleY()/2));
				float fX1 = Math.abs(x - plbX);
				float fY1 = Math.abs(y - plbY);
				float fX2 = Math.abs(x - pltX);
				float fY2 = Math.abs(y - pltY);
				if(canMoveCueBall){
					if(y > 40 && y< ResourceManager.getInstance().cameraHeight-40 && x> 50
							&& x<ResourceManager.getInstance().cameraWidth/4-getWidth()/2 &&
							(fX1 > 25f || fY1 >25f) && (fX2 > 25f || fY2 >25f) ){
						final float angle = body.getAngle(); // keeps the body angle
						final Vector2 v2 = Vector2Pool.obtain(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
								y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
						body.setTransform(v2, angle);
						Vector2Pool.recycle(v2);
					}
				}
				return true;
			}
		};
				
		/*float pX = ResourceManager.getInstance().cameraWidth - ResourceManager.getInstance().cameraWidth / 12;
		float pY = ResourceManager.getInstance().cameraHeight / 4;
		sliderbar = new Sprite(pX, pY, ResourceManager.mSliderTextureRegion, vertexBufferObjectManager);
		sliderbar.setWidth(ResourceManager.getInstance().cameraWidth / 20);
		sliderbar.setHeight(ResourceManager.getInstance().cameraHeight / 2);
		sliderbar.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		sliderbar.setAlpha(0.2f);
		
		r = new Rectangle(pX-20, pY, 80, 20,vertexBufferObjectManager);
		r.setColor(Color.RED);
		r.setAlpha(0.5f);
		this.registerTouchArea(sliderbar);
		this.attachChild(r);
		this.attachChild(sliderbar);
		
		// Create our text object
		mText = new Text(r.getX()+(r.getWidth()/2)-6, r.getY(),ResourceManager.mGameFont, "H", 3,vertexBufferObjectManager);
		mText.setColor(Color.WHITE);
		this.attachChild(mText);*/
		
		LineHolder = new Entity(balls[15].getX(), balls[15].getY()){
			boolean isMoving;
			Vector2 v2;
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				for(int i=0;i<balls.length;i++){
					v2 = balls[i].getLinVelocityPoolBall();
					if(Math.abs(v2.x) < .01f  && Math.abs(v2.y) < .01f) {
						isMoving = false;
						LineHolder.setVisible(true);
					}
				    else{
				    	isMoving = true;
				    	LineHolder.setVisible(false);
				    	break;
				    }
				}
				
				if(!isMoving){
			    	float lineCenterX = balls[15].getX();
					float lineCenterY = balls[15].getY();
					LineHolder.setPosition(lineCenterX, lineCenterY);
					super.onManagedUpdate(pSecondsElapsed);
			    }
				
				if(balls[15].isPocketed&& balls[15].timeGreater >.7f){
					balls[15].setLinVelocityPoolBall(0,0);
					if(!isMoving){
						balls[15].isPocketed = false;
						float x = ResourceManager.getInstance().cameraWidth/4 -balls[15].getWidth()/2;
						float y = ResourceManager.getInstance().cameraHeight/2;
						final Vector2 v2 = Vector2Pool.obtain(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
								y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
						balls[15].body.setTransform(v2, balls[15].body.getAngle());
						Vector2Pool.recycle(v2);
						balls[15].setVisible(true);
						canMoveCueBall = true;
						Log.d("yeman2", "tg =  " +balls[15].timeGreater);
					}
				}else if (balls[15].isPocketed){
					balls[15].timeGreater += pSecondsElapsed;
				}
			}
		};
		
		line2 = new Rectangle(0, 0, 3, 100, vertexBufferObjectManager);
		line2.setColor(0, 0.9342f, 0.1443f);
		line2.setAnchorCenter(0, 0);
		
		mPoolCue = new Sprite(-55, -174, ResourceManager.mPoolCueTextureRegion, vertexBufferObjectManager);
		mPoolCue.setScaleCenter(0, 0);
		mPoolCue.setScaleX(4);
		mPoolCue.setScaleY(2.5f);
		mPoolCue.setAnchorCenterY(1);
		mPoolCue.setRotation(270);
		
		shoot = new MoveYModifier(1, 0, 0, EaseExponentialIn.getInstance());
		
		stickTouchArea = new Entity(0, -15){
			float originY; 
			float recordedDistance;
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.d("yeman2", "touching stickarea");
				if (pSceneTouchEvent.isActionDown()) {
					mLocked = true;
					originY = pTouchAreaLocalY;		// Save original Y touch
				}
				if (pSceneTouchEvent.isActionMove()) {
					float distance = originY - pTouchAreaLocalY;	// Calculate offset from old to new Y touch
					if (distance < 90 && distance > 0) {			// Set a limit to movement
						mPoolCue.setY(-(distance + 174));				// Move stick relative to offset
						recordedDistance = distance;
					}
				}
				if (pSceneTouchEvent.isActionUp()) {
					shoot.reset(.25f, mPoolCue.getY(), -174);
					mPoolCue.registerEntityModifier(shoot);
					mLocked = false;
					shoot.addModifierListener(new IModifierListener<IEntity>() {
						@Override
						public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
							if(recordedDistance>8){
								SFXManager.playClick(1, 1,"cueHitBall");
								jumpFace(recordedDistance);
							}
							else	
								makeToast( "Shot Canceled", "");	
						}						
						@Override
						public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {}
					});
				}
				return true;
			}
		};
		
		stickTouchArea.setSize( mPoolCue.getHeight() /1.2f,mPoolCue.getWidth() * 4);
		stickTouchArea.setAnchorCenterY(1);
		this.registerTouchArea(stickTouchArea);
		
		/*Rectangle r = new Rectangle(0, -15, mPoolCue.getHeight() /1.2f,mPoolCue.getWidth() * 4,   
				ResourceManager.getEngine().getVertexBufferObjectManager());
		r.setAnchorCenterY(1);*/
		
		this.attachChild(LineHolder);
		LineHolder.attachChild(line2);
		LineHolder.attachChild(mPoolCue);
		LineHolder.attachChild(stickTouchArea);
		//LineHolder.attachChild(r);
		LineHolder.setRotation(90);
		LineHolder.setZIndex(400);
				
		for (int i = 0; i < balls.length; i++) {
			balls[i].animate(new long[] { 200, 200 }, 0, 1, true);
			balls[i].ballNumber = i+1;
			if(i==15)	this.registerTouchArea(balls[i]);
			this.attachChild(balls[i]);
			balls[i].setZIndex(5000);
		}
		setupPockets(vertexBufferObjectManager);
	}

	private void setupPockets(VertexBufferObjectManager vertexBufferObjectManager) {
		final Sprite pocketLeftBottom =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketLeftBottom.setPosition(5,0 );
		pocketLeftBottom.setScaleCenter(0,0);
		pocketLeftBottom.setScale(1.8f);
		pocketLeftBottom.setAnchorCenter(0, 0);
		this.attachChild(pocketLeftBottom);
		leftBottomPocket = pocketLeftBottom;
		
		final Sprite pocketLeftTop =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketLeftTop.setPosition(6, ResourceManager.getInstance().cameraHeight);
		pocketLeftTop.setScaleCenter(0,0);
		pocketLeftTop.setScale(1.8f);
		pocketLeftTop.setAnchorCenter(0, 1);
		this.attachChild(pocketLeftTop);
		leftTopPocket = pocketLeftTop;
		
		final Sprite pocketRightTop =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketRightTop.setScaleCenter(0,0);
		pocketRightTop.setScale(1.8f);
		pocketRightTop.setPosition(ResourceManager.getInstance().cameraWidth-6, 
				ResourceManager.getInstance().cameraHeight);
		pocketRightTop.setAnchorCenter(1, 1);
		this.attachChild(pocketRightTop);
		
		final Sprite pocketRightBottom =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketRightBottom.setScaleCenter(0,0);
		pocketRightBottom.setScale(1.8f);
		pocketRightBottom.setPosition(ResourceManager.getInstance().cameraWidth-5, 0);
		pocketRightBottom.setAnchorCenter(1, 0);
		this.attachChild(pocketRightBottom);
		
		final Sprite pocketTopMiddle =new Sprite(ResourceManager.getInstance().cameraWidth/2-33f,
				ResourceManager.getInstance().cameraHeight+20f,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketTopMiddle.setScaleCenter(0,0);
		pocketTopMiddle.setScale(1.65f);
		pocketTopMiddle.setAnchorCenter(0,1);
		this.attachChild(pocketTopMiddle);
		
		final Sprite pocketBottomMiddle =new Sprite(ResourceManager.getInstance().cameraWidth/2-33f,
				-20f,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketBottomMiddle.setScaleCenter(0,0);
		pocketBottomMiddle.setScale(1.65f);
		pocketBottomMiddle.setAnchorCenter(0, 0);
		this.attachChild(pocketBottomMiddle);
		
		Sprite cornerEdgeBottomLeft = new Sprite(0,0,ResourceManager.mCornerEdgeLBTextureRegion, vertexBufferObjectManager);
		cornerEdgeBottomLeft.setPosition(5,0);
		cornerEdgeBottomLeft.setScaleCenter(0,0);
		cornerEdgeBottomLeft.setScale(1.8f);
		cornerEdgeBottomLeft.setAnchorCenter(0, 0);
		this.attachChild(cornerEdgeBottomLeft);
		
		Rectangle fillerCornerBottomLeft = new Rectangle(0, 0, 6, cornerEdgeBottomLeft.getHeight()+24f, vertexBufferObjectManager);
		fillerCornerBottomLeft.setPosition(0, 0);
		fillerCornerBottomLeft.setColor(41f/255f,41f/255f,41f/255f);
		fillerCornerBottomLeft.setAnchorCenter(0, 0);
		this.attachChild(fillerCornerBottomLeft);
		
		Sprite cornerEdgeTopLeft = new Sprite(0,0,ResourceManager.mCornerEdgeLTTextureRegion, vertexBufferObjectManager);
		cornerEdgeTopLeft.setPosition(6, ResourceManager.getInstance().cameraHeight);
		cornerEdgeTopLeft.setScaleCenter(0,0);
		cornerEdgeTopLeft.setScale(1.8f);
		cornerEdgeTopLeft.setAnchorCenter(0, 1);
		this.attachChild(cornerEdgeTopLeft);
		 
		Rectangle fillerCornerTopLeft = new Rectangle(0, 0, 6, cornerEdgeTopLeft.getHeight()+24f, vertexBufferObjectManager);
		fillerCornerTopLeft.setPosition(0, ResourceManager.getInstance().cameraHeight);
		fillerCornerTopLeft.setAnchorCenter(0, 1);
		fillerCornerTopLeft.setColor(41f/255f,41f/255f,41f/255f);
		this.attachChild(fillerCornerTopLeft);
		
		Sprite cornerEdgeBottomRight = new Sprite(0,0,ResourceManager.mCornerEdgeLBTextureRegion, vertexBufferObjectManager);
		cornerEdgeBottomRight.setScaleCenter(0,0);
		cornerEdgeBottomRight.setScale(1.8f);
		cornerEdgeBottomRight.setRotation(270f);
		cornerEdgeBottomRight.setPosition(ResourceManager.getInstance().cameraWidth-7f,0f);
		cornerEdgeBottomRight.setAnchorCenter(0,0);
		this.attachChild(cornerEdgeBottomRight);
		
		Rectangle fillerCornerBottomRight = new Rectangle(0, 0, 7, cornerEdgeBottomRight.getHeight()+26f, vertexBufferObjectManager);
		fillerCornerBottomRight.setPosition(ResourceManager.getInstance().cameraWidth, 0);
		fillerCornerBottomRight.setAnchorCenter(1, 0);
		fillerCornerBottomRight.setColor(41f/255f,41f/255f,41f/255f);
		this.attachChild(fillerCornerBottomRight);
		
		Sprite cornerEdgeTopRight = new Sprite(0,0,ResourceManager.mCornerEdgeLTTextureRegion, vertexBufferObjectManager);
		cornerEdgeTopRight.setPosition(ResourceManager.getInstance().cameraWidth-6,ResourceManager.getInstance().cameraHeight-2);
		cornerEdgeTopRight.setScaleCenter(0,0);
		cornerEdgeTopRight.setScale(1.8f);
		cornerEdgeTopRight.setRotation(90);
		cornerEdgeTopRight.setAnchorCenter(0, 1);
		this.attachChild(cornerEdgeTopRight);
		 
		Rectangle fillerCornerTopRight = new Rectangle(0, 0, 6, cornerEdgeTopRight.getHeight()*1.8f-6f, vertexBufferObjectManager);
		fillerCornerTopRight.setPosition(ResourceManager.getInstance().cameraWidth-6, 
				ResourceManager.getInstance().cameraHeight-2);
		fillerCornerTopRight.setAnchorCenter(0, 1);
		fillerCornerTopRight.setColor(41f/255f,41f/255f,41f/255f);
		this.attachChild(fillerCornerTopRight);
		
		Rectangle fillerCornerTopRight2 = new Rectangle(0, 0, pocketRightBottom.getWidth()*1.8f, 
				2f, vertexBufferObjectManager);
		fillerCornerTopRight2.setPosition(ResourceManager.getInstance().cameraWidth, ResourceManager.getInstance().cameraHeight);
		fillerCornerTopRight2.setAnchorCenter(1, 1);
		fillerCornerTopRight2.setColor(41f/255f,41f/255f,41f/255f);
		this.attachChild(fillerCornerTopRight2);
		
		this.sortChildren();
		
		/* The actual collision-checking. */
		this.registerUpdateHandler(new IUpdateHandler() {
			float fX, fY;
			float plbX = pocketLeftBottom.getWidth()*(pocketLeftBottom.getScaleX()/2)+6F;
			float plbY = pocketLeftBottom.getHeight()*(pocketLeftBottom.getScaleY()/2);
			float pltX = pocketLeftTop.getWidth()*(pocketLeftBottom.getScaleX()/2)+6f;
			float pltY = pocketLeftTop.getY()-(pocketLeftTop.getHeight()*(pocketLeftTop.getScaleY()/2));
			float prbX = pocketRightBottom.getX()-pocketRightBottom.getWidth()*(pocketRightBottom.getScaleX()/2);
			float prbY = pocketRightBottom.getHeight()*(pocketRightBottom.getScaleY()/2);
			float prtX = pocketRightTop.getX()-pocketRightTop.getWidth()*(pocketRightTop.getScaleX()/2f);
			float prtY = pocketRightTop.getY()-pocketRightTop.getHeight()*(pocketRightTop.getScaleY()/2);
			float ptmX = pocketTopMiddle.getX()+pocketTopMiddle.getWidth()*pocketTopMiddle.getScaleX()/2;
			float ptmY = pocketTopMiddle.getY()-pocketTopMiddle.getHeight()*(pocketTopMiddle.getScaleY()/2);
			float pbmX = pocketBottomMiddle.getX()+pocketBottomMiddle.getWidth()*pocketBottomMiddle.getScaleX()/2;
			float pbmY = pocketBottomMiddle.getHeight()*(pocketBottomMiddle.getScaleY()/2)-20;
			float radiusBall = balls[15].getWidth()/2;
			float radiusPocket = (pocketLeftBottom.getWidth()*pocketLeftBottom.getScaleX())/2;
			float floatDistCover =0; 
			float radius = ((radiusPocket + radiusBall) * (radiusBall + radiusPocket))/3;
			@Override
			public void reset() { }
			
			@Override
			public void onUpdate(final float pSecondsElapsed) {
				for(int i=0;i<balls.length;i++){
					if(!balls[i].isPocketed){
						//** calculate for bottom left pocket **//
						fX = Math.abs(balls[i].getX() - plbX)*Math.abs(balls[i].getX() - plbX);
						fY = Math.abs(balls[i].getY() - plbY)*Math.abs(balls[i].getY() - plbY);
						floatDistCover = fX + fY;
						if((floatDistCover < radius) ){
							Log.d("yeman2","radius = " + radius);
							Log.d("yeman2","floatDistCover = " + floatDistCover);
							handleball(i,pSecondsElapsed);
						}
						
						//** calculate for top left pocket **//
						fX = Math.abs(balls[i].getX() - pltX)*Math.abs(balls[i].getX() - pltX);
						fY = Math.abs(balls[i].getY() - pltY)*Math.abs(balls[i].getY() - pltY);
						floatDistCover = fX + fY;
						if(floatDistCover < radius )	handleball(i,pSecondsElapsed); 
											
						//** calculate for bottom right pocket **//
						fX = Math.abs(balls[i].getX() - prbX)*Math.abs(balls[i].getX() - prbX);
						fY = Math.abs(balls[i].getY() - prbY)*Math.abs(balls[i].getY() - prbY);
						floatDistCover = fX + fY;
						if(floatDistCover < radius) handleball(i,pSecondsElapsed);
												
						//** calculate for top Right pocket **//
						fX = Math.abs(balls[i].getX() - prtX)*Math.abs(balls[i].getX() - prtX);
						fY = Math.abs(balls[i].getY() - prtY)*Math.abs(balls[i].getY() - prtY);
						floatDistCover = fX + fY;
						if(floatDistCover < radius) handleball(i,pSecondsElapsed);
						
						//** calculate for top middle pocket **//
						fX = Math.abs(balls[i].getX() - ptmX)*Math.abs(balls[i].getX() - ptmX);
						fY = Math.abs(balls[i].getY() - ptmY)*Math.abs(balls[i].getY() - ptmY);
						floatDistCover = fX + fY;
						if(floatDistCover < radius) handleball(i,pSecondsElapsed);
						
						//** calculate for bottom middle pocket **//
						fX = Math.abs(balls[i].getX() - pbmX)*Math.abs(balls[i].getX() - pbmX);
						fY = Math.abs(balls[i].getY() - pbmY)*Math.abs(balls[i].getY() - pbmY);
						floatDistCover = fX + fY;
						if(floatDistCover < radius)	handleball(i,pSecondsElapsed);
					}
				}
			}
		});
	}
	
	protected void handleball (int i, float time) {
		balls[i].setVisible(false);
		SFXManager.playClick(1.2f, 1f,"ballPocket");
		balls[i].isPocketed = true;
		if(i!=15){
			destroyBall(balls[i]);
		    balls[i].timeBallPocketed = time;
		}
		if(!ballTypeDecided){
			if(i<8) {
				playerBallType  = 0;
				makeToast("you are ", ballTypes[playerBallType]);
				ballTypeDecided = true;
			}
			else if(i>8 && i<15){    playerBallType  = 1;
				makeToast("you are ", ballTypes[playerBallType]);
				ballTypeDecided = true;
			}else{
				makeToast("unlucky, you shot in the  "," cue Ball");
			}
		}else{
			if(playerBallType ==0){
				if(i<8)			makeToast("congrats you shot in a ",ballTypes[playerBallType]);
				else if (i>8 && i < 15)  {
					makeToast("unlucky, you shot in a ",ballTypes[playerBallType]);
					playerBallType = 1;
					makeToast("now shooting at ",ballTypes[playerBallType]);
				}
			}
			if(playerBallType ==1){
				if(i>8 && i <15)  makeToast("congrats you shot in a ",ballTypes[playerBallType]);
				else if (i<8)  {
					makeToast("unlucky, you shot in a ",ballTypes[playerBallType]);
					playerBallType = 0;
					makeToast("now shooting at ",ballTypes[playerBallType]);
				}
			}
			if(i==15){
				if (playerBallType == 0) playerBallType = 1;
				if (playerBallType == 1) playerBallType = 0;
				makeToast("Unlucky you shot in the  ", "Cueball");
				makeToast("now shooting at ", ballTypes[playerBallType]);
			}
		}
	}

	private void makeToast(final String string, final String ballTypes2) {
		ResourceManager.getActivity().runOnUiThread(new Runnable() {
			  public void run() {
				  Toast.makeText(ResourceManager.getContext(), string + ballTypes2, Toast.LENGTH_SHORT).show();
			  }
		});
	}

	protected void destroyBall(PoolBall poolBall) {
		poolBall.setLinVelocityPoolBall(0,0);
        mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(poolBall));
        mPhysicsWorld.destroyBody( poolBall.body);
        GameLevel.this.detachChild(poolBall);
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		Log.d("yeman", "mlocked ="+mLocked);
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionMove()) {
				//Rotate Cue and line
				if(!mLocked){
					float dx = pSceneTouchEvent.getX() - LineHolder.getX();
	                float dy = pSceneTouchEvent.getY() - LineHolder.getY();
	                float angle = (float) Math.atan2(dx,dy);
	                float distance = (float) Math.sqrt(dx * dx + dy * dy);
	                // Apply rotation to line-holder
	                LineHolder.setRotation(MathUtils.radToDeg(angle));
	                // Adjust line's height/length
	                line2.setHeight(distance);
	                return true;
				}
                return true;
			}
		}
		return false;
	}

	private void jumpFace(float dist) {
		if(canMoveCueBall)	canMoveCueBall = false;
		float Vx = FloatMath.cos(MathUtils.degToRad(LineHolder.getRotation()-90));
	    float Vy = FloatMath.sin(MathUtils.degToRad(LineHolder.getRotation()-90));
		final Vector2 velocity = Vector2Pool.obtain(dist * Vx * 5, dist * -Vy * 5);
		Log.d("yeman2","ditsance = " + dist );
		Log.d("yeman2","velx  = " + (dist *Vx * 5)+ " velY = " + + (dist *Vy * 5));
		balls[15].setLinVelocityPoolBall(velocity.x,velocity.y);
		Vector2Pool.recycle(velocity);
	}
}