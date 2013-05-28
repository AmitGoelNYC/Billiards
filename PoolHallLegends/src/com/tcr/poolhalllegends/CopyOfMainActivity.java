package com.tcr.poolhalllegends;
/*package com.example.helloworldall;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBounceIn;

import android.util.DisplayMetrics;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class CopyOfMainActivity extends BaseGameActivity implements 
	IOnSceneTouchListener , IOnAreaTouchListener{
	
	private Camera mCamera;
	private AnimatedSprite[] balls;

	private PhysicsWorld mPhysicsWorld;
	private Line line;
	private float mGravityX=5;
	private float mGravityY=5;
	private Scene mScene;
	private Scene splashScene;
	private PhysicsHandler ph;
	private SceneType currentScene = SceneType.SPLASH;
	
	private final int mFaceCount = 0;
	final int CAMERA_WIDTH = 720;
	private final int CAMERA_HEIGHT = 480;
    private Sprite splash;
    private Sprite sliderbar;
    private Sprite mPoolCue;
    private Rectangle r;
    private Text mText;
    private boolean mSplashFinished = false;
    
	@Override
	public EngineOptions onCreateEngineOptions() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,ScreenOrientation.LANDSCAPE_FIXED, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true);
//		eo.getAudioOptions().setNeedsSound(true);
		return engineOptions;
	}
	
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		ResourceManager.getInstance().setup(this.getEngine(), this.getApplicationContext(), 
				CAMERA_WIDTH,CAMERA_HEIGHT,0,0);
		ResourceManager.loadSplashResources();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		initSplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);
	}
	
	private void initSplashScene(){
		ResourceManager.mSplashMusic.play();
		splashScene = new Scene();
    	splash = new Sprite(0, 0, ResourceManager.splashTextureRegion, mEngine.getVertexBufferObjectManager())	{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera){
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
		};
    	ParallelEntityModifier parallelModifier = new ParallelEntityModifier(
    			new FadeInModifier(1f), new ScaleModifier(1f, 0.5f, 1.5f),
				new RotationModifier(1f, 0, 720f),
    			new MoveModifier(1, 0, (CAMERA_WIDTH - splash.getWidth()) * 0.5f, 
    				0, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f, EaseBounceIn.getInstance()));
    	splash.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	splash.registerEntityModifier(parallelModifier);

    	Sprite BackgroundSprite = new Sprite(0,0,
    			ResourceManager.mGameBackgroundTextureRegion, mEngine.getVertexBufferObjectManager());
		BackgroundSprite.setScaleX(CAMERA_WIDTH);
		//BackgroundSprite.setScaleY(CAMERA_HEIGHT/480f);
		BackgroundSprite.setZIndex(-5000);
		splashScene.attachChild(BackgroundSprite);
    	splashScene.attachChild(splash);
	}
	
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback(){
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.unloadSplashResources();
                loadResources();
                loadScenes();
            	setupBalls();
				mSplashFinished = true;
				onResumeGame();
                splash.detachSelf();
                mEngine.setScene(mScene);
            }		
		}));  
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public synchronized void onResumeGame(){
		super.onResumeGame();
		if(ResourceManager.mMusic != null && !ResourceManager.mMusic.isPlaying() && mSplashFinished){
			ResourceManager.mMusic.play();
		}
	}
	public synchronized void onPauseGame(){
		super.onPauseGame();
		if(ResourceManager.mMusic != null && ResourceManager.mMusic.isPlaying()){
			ResourceManager.mMusic.pause();
		}
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
			final ITouchArea pTouchArea, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		if (pSceneTouchEvent.isActionDown()) {
			if(pTouchArea.toString().equals("AnimatedSprite")){
				final AnimatedSprite face = (AnimatedSprite) pTouchArea;
				this.jumpFace(face);
				return true;
			}
		}
		if (pTouchArea.toString().equals("Sprite")){
			
			
			*//*******METHOD TO CAPTURE MOVE AND TOUCH********//*
			float originalStickPosX = balls[8].getX();
			if(pSceneTouchEvent.isActionMove()) {
				if(pSceneTouchEvent.getX() < originalStickPosX){
					Log.d("yeman", "moving cue");
					mPoolCue.setPosition(pSceneTouchEvent.getX(), mPoolCue.getY());
				}
			}else if(pSceneTouchEvent.isActionUp()){
				this.jumpFace(balls[8]);
				Log.d("yeman", "cue released");
			}
			
			
			float MaxValue = 3f;
			float TouchY = pSceneTouchEvent.getY();
			if (TouchY  > sliderbar.getY() + sliderbar.getHeight()) TouchY = sliderbar.getY() + sliderbar.getWidth();
			if (TouchY  < sliderbar.getY()) TouchY = sliderbar.getY();
			r.setPosition(r.getX(),  TouchY - r.getHeight() / 2 );
			
			float MaxX = sliderbar.getX() + sliderbar.getWidth();
		    float MinX = sliderbar.getX();
			float newVal = Math.round(
					(MaxX - (r.getY()+r.getHeight() / 2)) * MaxValue
					/ sliderbar.getHeight() );
			Log.d("yeman", "newVal = "+newVal);
			String pText = "";
			if(newVal>6){
				pText = "H";
			}else if (newVal<5){
				pText = "L";
			}else{
				pText = "M";
			}
			mText.setText(pText);
			mText.setPosition(r.getX()+(r.getWidth()/2)-6, r.getY());
			return true;
		}
		if(pTouchArea.toString().equals("Line")){
			Log.d("yeman", "touching line");
		}	
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionDown()) {
				// this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}
	
	public void loadResources()	{
		ResourceManager.loadGameResources();
		mBackgroundTexture = new Texture(512, 1024,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBackgroundTextureRegion = TextureRegionFactory.createFromAsset(
				mBackgroundTexture, this, "table_bkg.png", 0, 0);

		this.enableAccelerationSensor(this);
	}

	private void loadScenes(){
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0.8784f));
        this.mScene.setOnSceneTouchListener(this);
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
	    final IAreaShape ground = new Rectangle(0, CAMERA_HEIGHT-30, CAMERA_WIDTH, 30,vertexBufferObjectManager);
	    ground.setColor(Color.CYAN);
		final IAreaShape roof = new Rectangle(0, 0, CAMERA_WIDTH, 30,vertexBufferObjectManager);
		roof.setColor(Color.PINK);
		final IAreaShape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT,vertexBufferObjectManager);
		left.setColor(Color.YELLOW);
		final IAreaShape right = new Rectangle(CAMERA_WIDTH-2, 0, 2, CAMERA_HEIGHT,vertexBufferObjectManager);
		right.setColor(Color.YELLOW);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld,  ground,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld,  right,BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.setOnAreaTouchListener(this);
		//return this.mScene;
	}
	
	private void setupBalls() {
		balls = new AnimatedSprite[9];
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		Line noPassBreakLine = new Line(CAMERA_WIDTH/4, 30, 
				CAMERA_WIDTH/4, CAMERA_HEIGHT-30, vertexBufferObjectManager);
		noPassBreakLine.setLineWidth(3);
		noPassBreakLine.setColor(Color.WHITE);
		this.mScene.attachChild(noPassBreakLine);
		
		AnimatedSprite redBall = new AnimatedSprite(mCamera.getWidth() * 0.6f, mCamera.getHeight()/2 -5,ResourceManager.mBallRedTextureRegion,vertexBufferObjectManager);
		float nextCalcX = redBall.getX()+redBall.getWidth()-8;
		float nextCalcY = redBall.getY();
		AnimatedSprite yellowBall = new AnimatedSprite(nextCalcX, nextCalcY+16,ResourceManager.mBallYellowTextureRegion,vertexBufferObjectManager);
		AnimatedSprite blueBall = new AnimatedSprite(nextCalcX, nextCalcY-16,ResourceManager.mBallBlueTextureRegion,vertexBufferObjectManager);
		nextCalcX += redBall.getWidth();
		AnimatedSprite greenBall = new AnimatedSprite(nextCalcX, nextCalcY,ResourceManager.mBallGreenTextureRegion,vertexBufferObjectManager);
		nextCalcY = greenBall.getY(); 
		AnimatedSprite orangeBall = new AnimatedSprite(nextCalcX, nextCalcY+32,ResourceManager.mBallOrangeTextureRegion,vertexBufferObjectManager);
		AnimatedSprite pinkBall = new AnimatedSprite(nextCalcX, nextCalcY-32,ResourceManager.mBallPinkTextureRegion,vertexBufferObjectManager);
		AnimatedSprite purpleBall = new AnimatedSprite(nextCalcX+32, yellowBall.getY(),ResourceManager.mBallPurpleTextureRegion,vertexBufferObjectManager);
		AnimatedSprite blackBall = new AnimatedSprite(nextCalcX+32, blueBall.getY(),ResourceManager.mBallBlackTextureRegion,vertexBufferObjectManager);
		AnimatedSprite whiteBall = new AnimatedSprite(CAMERA_WIDTH/4 , CAMERA_HEIGHT/2-5,ResourceManager.mBallWhiteTextureRegion,vertexBufferObjectManager);
		whiteBall.setX(whiteBall.getX()-whiteBall.getWidth());
		
		Rectangle r = new Rectangle(mCamera.getWidth()-22, mCamera.getHeight()-30, 20, pBarMaxheight/2,vertexBufferObjectManager);
		r.setColor(Color.RED);
		r.setAlpha(0.5f);
		Line powerBarLine = new Line(mCamera.getWidth()-30,mCamera.getHeight()-r.getHeight(),
				mCamera.getWidth()-20,	r.getHeight()-50,vertexBufferObjectManager);
		Log.d("yeman","rect height = "+ pBarMaxheight/2 +" and powerBarLine y1 ="+powerBarLine.getY1()+
				" and powerBarLine y2 ="+powerBarLine.getY2());
		powerBarLine.setColor(Color.WHITE);
		this.mScene.attachChild(r);
		this.mScene.registerTouchArea(powerBarLine);
		this.mScene.attachChild(powerBarLine);
		float pX = CAMERA_WIDTH - CAMERA_WIDTH / 12;
		float pY = CAMERA_HEIGHT / 4;
		sliderbar = new Sprite(pX, pY, ResourceManager.mSliderTextureRegion, vertexBufferObjectManager);
		sliderbar.setWidth(CAMERA_WIDTH / 20);
		sliderbar.setHeight(CAMERA_HEIGHT / 2);
		sliderbar.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		sliderbar.setAlpha(0.2f);
		
		r = new Rectangle(pX-20, pY, 80, 20,vertexBufferObjectManager);
		r.setColor(Color.RED);
		r.setAlpha(0.5f);
		mScene.registerTouchArea(sliderbar);
		mScene.attachChild(r);
		mScene.attachChild(sliderbar);
		
		// Create our text object
		mText = new Text(r.getX()+(r.getWidth()/2)-6, r.getY(), 
					ResourceManager.mGameFont, "H", 3,getVertexBufferObjectManager());
		// Set the text color value to pure blue with full alpha (R,G,B,A)
		mText.setColor(Color.WHITE);
		mScene.attachChild(mText);
		
		balls[0] = redBall;
		balls[1] = yellowBall;
		balls[2] = blueBall;
		balls[3] = greenBall;
		balls[4] = orangeBall;
		balls[5] = pinkBall;
		balls[6] = purpleBall;
		balls[7] = blackBall;
		balls[8] = whiteBall;
		
		line = new Line(whiteBall.getX(), whiteBall.getY(), 40, 40, vertexBufferObjectManager){
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				float lineCenterX = balls[8].getX() + balls[8].getWidth()/2;
				float lineCenterY = balls[8].getY() + balls[8].getHeight()/2;
				line.setPosition(lineCenterX, lineCenterY, lineCenterX +80,lineCenterY);
						//balls[8].getX()-30,balls[8].getY()-30);
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		line.setLineWidth(3);
		line.setColor(0, 0.9342f, 0.1443f);
		mScene.attachChild(line);

		mPoolCue = new Sprite(balls[8].getX(), balls[8].getY(), ResourceManager.mPoolCueTextureRegion, vertexBufferObjectManager);
		mPoolCue.setScaleX(4);
		mPoolCue.setScaleY(2);
		mPoolCue.setScaleCenter(43, 22);
		mPoolCue.setX(mPoolCue.getX()-mPoolCue.getWidth()-4);
		mPoolCue.setY(mPoolCue.getY()-4);
		mScene.registerTouchArea(mPoolCue);
		mScene.attachChild(mPoolCue);
		
		for (int i = 0; i < 9; i++) {
			Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld,balls[i], BodyType.DynamicBody, objectFixtureDef);
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(balls[i], body, true, true));
			balls[i].animate(new long[] { 200, 200 }, 0, 1, true);
			balls[i].setUserData(body);
			if(i==8){
				this.mScene.registerTouchArea(balls[i]);
			}
			this.mScene.attachChild(balls[i]);
		}
	}

	private void jumpFace(final AnimatedSprite face) {
		Log.d("yeman","facejump");
		final Body faceBody = (Body) face.getUserData();
		final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
		faceBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}
	
	public void onAccelerationChanged(final AccelerationData pAccelerometerData) {
		this.mGravityX = pAccelerometerData.getX();
		this.mGravityY = pAccelerometerData.getY();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		line.setPosition(balls[8].getX()+5, balls[8].getY()+5, balls[8].getX()-30,balls[8].getY()-30);
		Vector2Pool.recycle(gravity);
	}
}*/