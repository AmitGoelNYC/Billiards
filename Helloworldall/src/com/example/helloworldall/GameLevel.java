package com.example.helloworldall;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.Rectangle;
//import org.andengine.entity.primitive.TexturedPolygon;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
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

import android.os.Looper;
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
    private Rectangle r,line2;

	private float mGravityX=50f;
	private float mGravityY=50f;
	private boolean mLocked = false;
	private Line noPassBreakLine;
	private Entity stickTouchArea, LineHolder;
	private MoveYModifier shoot;
	@Override
	public void onLoadScene() {
		super.onLoadScene();
		int newColor = MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR);
		float[] colors = GrowToggleRectangle.getColors(newColor);
		this.setBackground(new Background(colors[0], colors[1], colors[2],colors[3]));
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		//final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,0.5f, 0.5f);
		this.registerUpdateHandler(this.mPhysicsWorld);
		final FixtureDef uniqueBodyFixture = PhysicsFactory.createFixtureDef(1f, .6f, .25f);
		
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
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		final VertexBufferObjectManager vertexBufferObjectManager = ResourceManager.getEngine().getVertexBufferObjectManager();
		noPassBreakLine = new Line(ResourceManager.getInstance().cameraWidth/4, 40, 
				ResourceManager.getInstance().cameraWidth/4, ResourceManager.getInstance().cameraHeight-40, vertexBufferObjectManager);
		noPassBreakLine.setLineWidth(3);
		noPassBreakLine.setColor(Color.WHITE);
		this.attachChild(noPassBreakLine);
		setupPockets(vertexBufferObjectManager);
		
		PoolBall redBall = new PoolBall(ResourceManager.getInstance().cameraWidth * 0.65f, 
				ResourceManager.getInstance().cameraHeight / 2,
				ResourceManager.mBallRedTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		float nextCalcX = redBall.getX()+redBall.getWidth()-8;
		float nextCalcY = redBall.getY();
		PoolBall yellowBallStripe = new PoolBall(nextCalcX, nextCalcY+14,
				ResourceManager.mBallYellowStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall blueBallStripe = new PoolBall(nextCalcX, nextCalcY-14,
				ResourceManager.mBallBlueStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += redBall.getWidth()-8;
		PoolBall blackBall = new PoolBall(nextCalcX, nextCalcY,
				ResourceManager.mBallBlackTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcY = blackBall.getY(); 
		PoolBall orangeBall = new PoolBall(nextCalcX, nextCalcY+30,
				ResourceManager.mBallOrangeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall pinkBall = new PoolBall(nextCalcX, nextCalcY-30,
				ResourceManager.mBallPinkTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += redBall.getWidth()-8;
		PoolBall purpleBall = new PoolBall(nextCalcX, yellowBallStripe.getY(),
				ResourceManager.mBallPurpleTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall greenBallStripe = new PoolBall(nextCalcX, blueBallStripe.getY(),
				ResourceManager.mBallGreenStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall redBallStripe = new PoolBall(nextCalcX, purpleBall.getY()+30,
				ResourceManager.mBallRedStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall orangeBallStripe = new PoolBall(nextCalcX, greenBallStripe.getY()-30,
				ResourceManager.mBallOrangeStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		nextCalcX += redBall.getWidth()-8;
		PoolBall greenBall = new PoolBall(nextCalcX, blackBall.getY(),
				ResourceManager.mBallGreenTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall pinkBallStripe = new PoolBall(nextCalcX, greenBall.getY()+30,
				ResourceManager.mBallPinkStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall purpleBallStripe = new PoolBall(nextCalcX, greenBall.getY()-30,
				ResourceManager.mBallPurpleStripeTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall yellowBall = new PoolBall(nextCalcX, purpleBallStripe.getY()-30,
				ResourceManager.mBallYellowTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall blueBall = new PoolBall(nextCalcX, pinkBallStripe.getY()+30,
				ResourceManager.mBallBlueTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		PoolBall whiteBall = new PoolBall(ResourceManager.getInstance().cameraWidth/4 ,
				ResourceManager.getInstance().cameraHeight/2,
				ResourceManager.mBallWhiteTextureRegion,vertexBufferObjectManager,this.mPhysicsWorld);
		whiteBall.setX(whiteBall.getX()-whiteBall.getWidth()/2);
		
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
		
		balls[0] = yellowBall;
		balls[1] = blueBall;
		balls[2] = redBall;
		balls[3] = purpleBall;
		balls[4] = orangeBall;
		balls[5] = greenBall;
		balls[6] = pinkBall; 
		balls[7] = blackBall;
		balls[8] = yellowBallStripe;
		balls[9] = blueBallStripe;
		balls[10] = redBallStripe; 
		balls[11] = purpleBallStripe;
		balls[12] = orangeBallStripe;
		balls[13] = greenBallStripe;
		balls[14] = pinkBallStripe;
		balls[15] = whiteBall; 
		
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
						final Body body = balls[15].body;
		                mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(balls[15]));
		                mPhysicsWorld.destroyBody(body);
		                
						GameLevel.this.detachChild(balls[15]);
						balls[15] =  new PoolBall(ResourceManager.getInstance().cameraWidth/4 - balls[15].getWidth()/2 ,
								ResourceManager.getInstance().cameraHeight/2,
								ResourceManager.mBallWhiteTextureRegion,vertexBufferObjectManager, mPhysicsWorld);
						
						GameLevel.this.attachChild(balls[15]);
						GameLevel.this.registerTouchArea(balls[15]);
						
						balls[15].setVisible(true);
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
		//this.attachChild(line);

		mPoolCue = new Sprite(-55, -174, ResourceManager.mPoolCueTextureRegion, vertexBufferObjectManager);
		mPoolCue.setScaleCenter(0, 0);
		mPoolCue.setScaleX(4);
		mPoolCue.setScaleY(2.5f);
		mPoolCue.setAnchorCenterY(1);
		mPoolCue.setRotation(270);
		this.registerTouchArea(mPoolCue);
		//line.attachChild(mPoolCue);
		
		shoot = new MoveYModifier(1, 0, 0, EaseExponentialIn.getInstance());
		
		stickTouchArea = new Entity(0, 0){
			float originY; 
			float recordedDistance;
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				Log.d("yeman2", "touching stickarea");
				if (pSceneTouchEvent.isActionDown()) {
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
					/*Log.d("fire", "fire");*/
					shoot.reset(.37f, mPoolCue.getY(), -174);
					mPoolCue.registerEntityModifier(shoot);
					shoot.addModifierListener(new IModifierListener<IEntity>() {
						@Override
						public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
							if(recordedDistance>8){
								SFXManager.playClick(1, 1,"cueHitBall");
								jumpFace(recordedDistance);
							}
							else{
								ResourceManager.getActivity().runOnUiThread(new Runnable() {
									  public void run() {
										  Toast.makeText(ResourceManager.getContext(), "Shot Canceled", Toast.LENGTH_LONG).show();
									  }
									}
								);
							}
						}						
						@Override
						public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {}
					});
					mLocked = false;
				}
				return true;
			}
		};
		Line newlinetest = new Line(20, 20, mPoolCue.getWidth(), mPoolCue.getHeight(), ResourceManager.getEngine().getVertexBufferObjectManager());
		this.attachChild(newlinetest);
		Log.d("yeman2","get width poolcue = "+  mPoolCue.getWidth() + " mPoolcue height = " + mPoolCue.getHeight());
		
		stickTouchArea.setSize(mPoolCue.getWidth() * 4, mPoolCue.getHeight() * 2.5f);
		stickTouchArea.setAnchorCenterY(1);
		this.registerTouchArea(stickTouchArea);
		
		this.attachChild(LineHolder);
		LineHolder.attachChild(line2);
		LineHolder.attachChild(mPoolCue);
		LineHolder.attachChild(stickTouchArea);
		LineHolder.setRotation(90);
		
		for (int i = 0; i < balls.length; i++) {
			/*Body body = PhysicsFactory.createCircleBody(this.mPhysicsWorld,balls[i], BodyType.DynamicBody, objectFixtureDef);
			body.setLinearDamping(.21f);
			body.setAngularDamping(.21f);
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(balls[i], body, true, true));*/
			balls[i].animate(new long[] { 200, 200 }, 0, 1, true);
			//balls[i].setUserData(body);
			balls[i].ballNumber = i+1;
			if(i==15)
				this.registerTouchArea(balls[i]);
			this.attachChild(balls[i]);
		}
	}

	private void setupPockets(VertexBufferObjectManager vertexBufferObjectManager) {
		final Sprite pocketLeftBottom =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketLeftBottom.setPosition(5,0 );
		pocketLeftBottom.setScaleCenter(0,0);
		pocketLeftBottom.setScale(1.8f);
		pocketLeftBottom.setAnchorCenter(0, 0);
		this.attachChild(pocketLeftBottom);
		
		final Sprite pocketLeftTop =new Sprite(0, 0,ResourceManager.mPocketTextureRegion, vertexBufferObjectManager);
		pocketLeftTop.setPosition(6, ResourceManager.getInstance().cameraHeight);
		pocketLeftTop.setScaleCenter(0,0);
		pocketLeftTop.setScale(1.8f);
		pocketLeftTop.setAnchorCenter(0, 1);
		this.attachChild(pocketLeftTop);
		
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
			
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				for(int i=0;i<balls.length;i++){
					//** calculate for bottom left pocket **//
					fX = Math.abs(balls[i].getX() - plbX);
					fY = Math.abs(balls[i].getY() - plbY);
					if((fX < 23.5f && fY <23.5) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketLeftBottom)) {
						/*	Log.d("yeman","collision with " + i);*/
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						}
					}
					
					//** calculate for top left pocket **//
					fX = Math.abs(balls[i].getX() - pltX);
					fY = Math.abs(balls[i].getY() - pltY);
					if((fX < 23.5f && fY <23.5f) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketLeftTop)) {
							//Log.d("yeman","collision with " + i);
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						} 
					}
					
					//** calculate for bottom right pocket **//
					fX = Math.abs(balls[i].getX() - prbX);
					fY = Math.abs(balls[i].getY() - prbY);
					if((fX < 23.5f || fY <23.5f) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketRightBottom)) {
							//Log.d("yeman","collision with " + i);
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						}
					}
					
					//** calculate for top Right pocket **//
					fX = Math.abs(balls[i].getX() - prtX);
					fY = Math.abs(balls[i].getY() - prtY);
					if((fX < 23.5f && fY <23.5f) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketRightTop)) {
							//Log.d("yeman","collision with " + i);
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						} 
					}
					
					//** calculate for top middle pocket **//
					fX = Math.abs(balls[i].getX() - ptmX);
					fY = Math.abs(balls[i].getY() - ptmY);
					if((fX < 23.5f && fY <23.5f) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketTopMiddle)) {
							//Log.d("yeman","collision with " + i);
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						} 
					}
					
					//** calculate for bottom middle pocket **//
					fX = Math.abs(balls[i].getX() - pbmX);
					fY = Math.abs(balls[i].getY() - pbmY);
					if((fX < 23.5f && fY <23.5f) && !balls[i].isPocketed ){
						if (balls[i].collidesWith(pocketBottomMiddle)) {
							Log.d("yeman", "fX = " + fX +" fY = "+ fY+ " for i = "+i);
							Log.d("yeman", "pbmX = " + pbmX +" pbmY = "+ pbmY);
							Log.d("yeman", "whiteballx = " + balls[i].getX() +" whitebally = "+ balls[i].getY());
							Log.d("yeman","collision with " + i);
							balls[i].setVisible(false);
							SFXManager.playClick(1.2f, 1f,"ballPocket");
							balls[i].isPocketed = true;
						} 
					}
					
				}
				
			}
		});
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
					
					/*float directionX = pSceneTouchEvent.getX() - line.getX1();
	                float directionY = pSceneTouchEvent.getY() - line.getY1();
	                float rotationAngle = (float) Math.atan2(-directionY,directionX);
	                line.setRotationCenter(0, 0);
	                line.setRotation(MathUtils.radToDeg(rotationAngle));*/
	                //line.setPosition(line.getX1(), line.getY1(), pSceneTouchEvent.getX(), line.getY2());
				}
                return true;
			}
		}
		return false;
	}
	
	/*private void destroyBall(final PoolBall ball) {
		ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable(){
            @Override
            public void run() {
                final Body body = ball.body;
                //mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(ball));
                //mPhysicsWorld.destroyBody(body);
                GameLevel.this.detachChild(ball);
                GameLevel.this.attachChild(ball);
                //ballsList.remove(ball);
            }});

    }*/

	private void jumpFace(float dist) {
		float Vx = FloatMath.cos(MathUtils.degToRad(LineHolder.getRotation()-90));
	    float Vy = FloatMath.sin(MathUtils.degToRad(LineHolder.getRotation()-90));

		Log.d("yeman2","ditsance = " + dist );
		//final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * Vx, this.mGravityY * -Vy);
		final Vector2 velocity = Vector2Pool.obtain(dist * Vx, dist * -Vy);
		balls[15].setLinVelocityPoolBall(velocity.x,velocity.y);
		Vector2Pool.recycle(velocity);
	}
}