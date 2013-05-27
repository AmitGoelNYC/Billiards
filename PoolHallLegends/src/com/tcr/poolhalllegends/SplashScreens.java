package com.tcr.poolhalllegends;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.exception.MusicReleasedException;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseBounceIn;

import android.util.Log;

public class SplashScreens extends ManagedSplashScreen {
	private static final BuildableBitmapTextureAtlas splashLogoTexture = 
			new BuildableBitmapTextureAtlas(ResourceManager.getEngine().getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    private static final ITextureRegion splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    		splashLogoTexture, ResourceManager.getContext(), "gfx/logo.png");
    private static final Sprite splashLogo = new Sprite(0, ResourceManager.getInstance().cameraHeight/2, splashTextureRegion, 
    		ResourceManager.getEngine().getVertexBufferObjectManager())	{
		@Override
        protected void preDraw(GLState pGLState, Camera pCamera){
            super.preDraw(pGLState, pCamera);
            pGLState.enableDither();
        }
	};
	static{
		splashLogo.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
    
	private static final BuildableBitmapTextureAtlas splashBackgroundTexture = new BuildableBitmapTextureAtlas(
			ResourceManager.getEngine().getTextureManager(), 16, 512);
	private static final ITextureRegion splashBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			splashBackgroundTexture, ResourceManager.getContext(), "gfx/background.png");
	private static final Sprite splashBackground = new Sprite(0,0,
			splashBackgroundTextureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
	static{
		splashBackground.setScaleX(ResourceManager.getInstance().cameraWidth);
		//BackgroundSprite.setScaleY(CAMERA_HEIGHT/480f);
		splashBackground.setZIndex(-5000);
	}

	private static final ParallelEntityModifier parallelModifier = new ParallelEntityModifier(
			new FadeInModifier(.8f), new ScaleModifier(.8f, 0.5f, 1.5f),
			new RotationModifier(1f, 0, 720f),
			new MoveModifier(.8f, 0, 	ResourceManager.getInstance().cameraHeight/2,(ResourceManager.getInstance().cameraWidth/2),
					(0), 
				EaseBounceIn.getInstance()));

	private static Music mSplashMusic;
	
	// ====================================================
	// METHODS
	// ====================================================
	@Override
	public void onLoadScene() {
		try {
			splashLogoTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			splashLogoTexture.load(); 
			splashBackgroundTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			splashBackgroundTexture.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		this.attachChild(SplashScreens.splashBackground);
		this.attachChild(SplashScreens.splashLogo);
		try{
			mSplashMusic = MusicFactory.createMusicFromAsset(ResourceManager.getEngine().getMusicManager(), 
					ResourceManager.getContext(), "sfx/splash.mp3");
		}catch(IOException e){
			Log.v("Sounds Load","Exception:" + e.getMessage());
		}
		mSplashMusic.play();
		
		SplashScreens.parallelModifier.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SceneManager.getInstance().showMainMenu();
			}
			
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {}
		});
		
		this.registerUpdateHandler(new IUpdateHandler() {
			int counter = 0;
			
			@Override
			public void onUpdate(final float pSecondsElapsed) {
				this.counter++;
				if(this.counter > 2) {
					SplashScreens.splashLogo.registerEntityModifier(SplashScreens.parallelModifier);
					SplashScreens.this.thisManagedSplashScene.unregisterUpdateHandler(this);
				}
			}
			
			@Override
			public void reset() {}
		});
	}
	
	@Override
	public void unloadSplashTextures() {
		splashLogoTexture.unload();
		splashBackgroundTexture.unload();
		try {
			if(!mSplashMusic.isReleased())mSplashMusic.release();
		} catch (MusicReleasedException e) {
			e.printStackTrace();
		}
	}
}