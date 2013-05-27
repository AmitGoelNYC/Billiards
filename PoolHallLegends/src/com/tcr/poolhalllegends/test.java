package com.tcr.poolhalllegends;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class test extends BaseGameActivity {

	private static final int mCameraWidth = 1280;
	private static final int mCameraHeight = 720;
	public Scene mScene;
	private static final float DEMO_VELOCITY = 150.0f;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		Camera mCamera = new Camera(0, 0, test.mCameraWidth, test.mCameraHeight);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
				new RatioResolutionPolicy(test.mCameraWidth,test.mCameraHeight), mCamera);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mScene = new Scene();
		mScene.setBackground(new Background(0, 0, 0.8784f));
		final float centerX = (test.mCameraWidth - this.mFaceTextureRegion
				.getWidth()) / 2;
		final float centerY = (test.mCameraHeight - this.mFaceTextureRegion
				.getHeight()) / 2;
		final Ball ball = new Ball(centerX, centerY, this.mFaceTextureRegion,
				this.getVertexBufferObjectManager());
		final Ball ball2 = new Ball(centerX+150, centerY+150, this.mFaceTextureRegion,
				this.getVertexBufferObjectManager());
		mScene.attachChild(ball);
		mScene.attachChild(ball2);
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	private static class Ball extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Ball(final float pX, final float pY,
				final TiledTextureRegion pTextureRegion,
				final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(test.DEMO_VELOCITY,test.DEMO_VELOCITY);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if (this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(test.DEMO_VELOCITY);
			} else if (this.mX + this.getWidth() > test.mCameraWidth) {
				this.mPhysicsHandler.setVelocityX(-test.DEMO_VELOCITY);
			}

			if (this.mY < 0) {
				this.mPhysicsHandler.setVelocityY(test.DEMO_VELOCITY);
			} else if (this.mY + this.getHeight() > test.mCameraHeight) {
				this.mPhysicsHandler.setVelocityY(-test.DEMO_VELOCITY);
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
	}

}