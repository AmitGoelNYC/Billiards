package com.example.helloworldall;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.Typeface;

public class ResourceManager {
	// ResourceManager Singleton instance
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	// We include these objects in the resource manager for
	// easy accessibility across our project.
	public MainActivity activity;
	public Engine engine;
	public Context context;
	public float cameraWidth;
	public float cameraHeight;
	
	/* The variables listed should be kept public, allowing us easy access
	   to them when creating new Sprites, Text objects and to play sound files */
	public static ITextureRegion mMenuBackgroundTextureRegion;
	public static ITextureRegion mMenuButtonPlay;
	public static ITextureRegion mMenuButtonOptions;
	public static ITextureRegion mMenuBilliardsTextureRegion;
	public static ITextureRegion mPocketTextureRegion;
	public static ITextureRegion mCornerEdgeLBTextureRegion;
	public static ITextureRegion mCornerEdgeLTTextureRegion;
	public static ITextureRegion mWoodPanelTextureRegion;
	
	/*****Game TextureRegions for Balls and Cues*****/
	public static ITextureRegion mSliderTextureRegion;
	public static ITextureRegion mPoolCueTextureRegion;
	public static TiledTextureRegion mBallYellowTextureRegion;
	public static TiledTextureRegion mBallRedTextureRegion;
	public static TiledTextureRegion mBallBlackTextureRegion;
	public static TiledTextureRegion mBallBlueTextureRegion;
	public static TiledTextureRegion mBallGreenTextureRegion;
	public static TiledTextureRegion mBallOrangeTextureRegion;
	public static TiledTextureRegion mBallPinkTextureRegion;
	public static TiledTextureRegion mBallPurpleTextureRegion;
	public static TiledTextureRegion mBallYellowStripeTextureRegion;
	public static TiledTextureRegion mBallRedStripeTextureRegion;
	public static TiledTextureRegion mBallBlueStripeTextureRegion;
	public static TiledTextureRegion mBallGreenStripeTextureRegion;
	public static TiledTextureRegion mBallOrangeStripeTextureRegion;
	public static TiledTextureRegion mBallPinkStripeTextureRegion;
	public static TiledTextureRegion mBallPurpleStripeTextureRegion;
	public static TiledTextureRegion mBallWhiteTextureRegion;
		
	public static ITiledTextureRegion buttonTiledTextureRegion;
	public static ITiledTextureRegion muteTiledTextureRegion;
	
	public static Font mMenuFont;
	public static Font mGameFont;
	public static Font mGameFontLarge;
	public static StrokeFont mStrokeFont;
	
	private ResourceManager(){}

	public static ResourceManager getInstance(){
		return INSTANCE;
	}
	
	// Setup the ResourceManager
	public void setup(final MainActivity pActivity,final Engine pEngine, final Context pContext, final float pCameraWidth, final float pCameraHeight, final float pCameraScaleX, final float pCameraScaleY){
		activity = pActivity;
		engine = pEngine;
		context = pContext;
		cameraWidth = pCameraWidth;
		cameraHeight = pCameraHeight;
	}
	
	public static Engine getEngine(){
		return getInstance().engine;
	}
	
	public static Context getContext(){
		return getInstance().context;
	}
	
	public static MainActivity getActivity(){
		return getInstance().activity;
	}
	
	// Loads all game resources.
	public static void loadGameResources() {
		getInstance().loadGameTextures();
		getInstance().loadSharedResources();
	}
	
	// Loads all menu resources
	public static void loadMenuResources() {
		getInstance().loadMenuTextures();		
		getInstance().loadSharedResources();
	}
	
	// Unloads all shared resources
	private void loadSharedResources() {
		//getInstance().loadSharedTextures();
		getInstance().loadFonts();
	}
	
	// Unloads all game resources.
	public static void unloadGameResources() {
		getInstance().unloadGameTextures();
	}

	// Unloads all menu resources
	public static void unloadMenuResources() {
		getInstance().unloadMenuTextures();
	}
	
	// Unloads all shared resources
	public static void unloadSharedResources() {
		//getInstance().unloadSharedTextures();
		getInstance().unloadFonts();
	}

	/* Each scene within a game should have a loadTextures method as well
	 * as an accompanying unloadTextures method. This way, we can display
	 * a loading image during scene swapping, unload the first scene's textures
	 * then load the next scenes textures.
	 */
	private void loadGameTextures(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		if(mBallBlackTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallBlackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_black.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallBlueTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallBlueTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_blue.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallYellowTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallYellowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_yellow.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallRedTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallRedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_red2.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallGreenTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallGreenTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_green.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallOrangeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallOrangeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_orange.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallPinkTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallPinkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_pink.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallPurpleTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallPurpleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_purple.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallBlueStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallBlueStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_blue_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallYellowStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallYellowStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_yellow_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallRedStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallRedStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_red2_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallGreenStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallGreenStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_green_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallOrangeStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallOrangeStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_orange_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallPinkStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallPinkStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_pink_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallPurpleStripeTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallPurpleStripeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_purple_stripe.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mBallWhiteTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 64, 32,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mBallWhiteTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, "ball_white.png",2, 1); // 64x32
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mPocketTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 52, 52);
				mPocketTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "pockets.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mCornerEdgeLBTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 52, 52);
				mCornerEdgeLBTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "cornerEdge.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mCornerEdgeLTTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 52, 52);
				mCornerEdgeLTTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "pocketslefttop.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mWoodPanelTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 320, 30);
				mWoodPanelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "woodPanel.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mPoolCueTextureRegion==null){
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(),43, 43,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mPoolCueTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "poolcue.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mSliderTextureRegion==null){
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(),64, 320,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				mSliderTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "sliderbar2.png"); 
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
	}
	
	/* All textures should have a method call for unloading once
	 * they're no longer needed; ie. a level transition. */
	private void unloadGameTextures(){
		if(mSliderTextureRegion!=null){
			if(mSliderTextureRegion.getTexture().isLoadedToHardware()){
				mSliderTextureRegion.getTexture().unload();
				mSliderTextureRegion = null;
			}
		}
		if(mBallBlackTextureRegion!=null){
			if(mBallBlackTextureRegion.getTexture().isLoadedToHardware()){
				mBallBlackTextureRegion.getTexture().unload();
				mBallBlackTextureRegion = null;
			}
		}
		if(mBallBlueTextureRegion!=null){
			if(mBallBlueTextureRegion.getTexture().isLoadedToHardware()){
				mBallBlueTextureRegion.getTexture().unload();
				mBallBlueTextureRegion = null;
			}
		}
		if(mBallGreenTextureRegion!=null){
			if(mBallGreenTextureRegion.getTexture().isLoadedToHardware()){
				mBallGreenTextureRegion.getTexture().unload();
				mBallGreenTextureRegion = null;
			}
		}
		if(mBallOrangeTextureRegion!=null){
			if(mBallOrangeTextureRegion.getTexture().isLoadedToHardware()){
				mBallOrangeTextureRegion.getTexture().unload();
				mBallOrangeTextureRegion = null;
			}
		}
		if(mBallPinkTextureRegion!=null){
			if(mBallPinkTextureRegion.getTexture().isLoadedToHardware()){
				mBallPinkTextureRegion.getTexture().unload();
				mBallPinkTextureRegion = null;
			}
		}
		if(mBallPurpleTextureRegion!=null){
			if(mBallPurpleTextureRegion.getTexture().isLoadedToHardware()){
				mBallPurpleTextureRegion.getTexture().unload();
				mBallPurpleTextureRegion = null;
			}
		}
		if(mBallRedTextureRegion!=null){
			if(mBallRedTextureRegion.getTexture().isLoadedToHardware()){
				mBallRedTextureRegion.getTexture().unload();
				mBallRedTextureRegion = null;
			}
		}
		if(mBallYellowTextureRegion!=null){
			if(mBallYellowTextureRegion.getTexture().isLoadedToHardware()){
				mBallYellowTextureRegion.getTexture().unload();
				mBallYellowTextureRegion = null;
			}
		}
		if(mBallBlueStripeTextureRegion!=null){
			if(mBallBlueStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallBlueStripeTextureRegion.getTexture().unload();
				mBallBlueStripeTextureRegion = null;
			}
		}
		if(mBallGreenStripeTextureRegion!=null){
			if(mBallGreenStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallGreenStripeTextureRegion.getTexture().unload();
				mBallGreenStripeTextureRegion = null;
			}
		}
		if(mBallOrangeStripeTextureRegion!=null){
			if(mBallOrangeStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallOrangeStripeTextureRegion.getTexture().unload();
				mBallOrangeStripeTextureRegion = null;
			}
		}
		if(mBallPinkStripeTextureRegion!=null){
			if(mBallPinkStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallPinkStripeTextureRegion.getTexture().unload();
				mBallPinkStripeTextureRegion = null;
			}
		}
		if(mBallPurpleStripeTextureRegion!=null){
			if(mBallPurpleStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallPurpleStripeTextureRegion.getTexture().unload();
				mBallPurpleStripeTextureRegion = null;
			}
		}
		if(mBallRedStripeTextureRegion!=null){
			if(mBallRedStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallRedStripeTextureRegion.getTexture().unload();
				mBallRedStripeTextureRegion = null;
			}
		}
		if(mBallYellowStripeTextureRegion!=null){
			if(mBallYellowStripeTextureRegion.getTexture().isLoadedToHardware()){
				mBallYellowStripeTextureRegion.getTexture().unload();
				mBallYellowStripeTextureRegion = null;
			}
		}
		if(mBallWhiteTextureRegion!=null){
			if(mBallWhiteTextureRegion.getTexture().isLoadedToHardware()){
				mBallWhiteTextureRegion.getTexture().unload();
				mBallWhiteTextureRegion = null;
			}
		}
		if(mPocketTextureRegion!=null){
			if(mPocketTextureRegion.getTexture().isLoadedToHardware()){
				mPocketTextureRegion.getTexture().unload();
				mPocketTextureRegion = null;
			}
		}
		if(mCornerEdgeLBTextureRegion!=null){
			if(mCornerEdgeLBTextureRegion.getTexture().isLoadedToHardware()){
				mCornerEdgeLBTextureRegion.getTexture().unload();
				mCornerEdgeLBTextureRegion = null;
			}
		}
		if(mCornerEdgeLTTextureRegion!=null){
			if(mCornerEdgeLTTextureRegion.getTexture().isLoadedToHardware()){
				mCornerEdgeLTTextureRegion.getTexture().unload();
				mCornerEdgeLTTextureRegion = null;
			}
		}
		if(mWoodPanelTextureRegion!=null){
			if(mWoodPanelTextureRegion.getTexture().isLoadedToHardware()){
				mWoodPanelTextureRegion.getTexture().unload();
				mWoodPanelTextureRegion = null;
			}
		}
		if(mPoolCueTextureRegion!=null){
			if(mPoolCueTextureRegion.getTexture().isLoadedToHardware()){
				mPoolCueTextureRegion.getTexture().unload();
				mPoolCueTextureRegion = null;
			}
		}
		System.gc();
	}
	
	/* Similar to the loadGameTextures(...) method, except this method will be
	 * used to load a different scene's textures
	 */
	private void loadMenuTextures(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		/*BuildableBitmapTextureAtlas buttonTexture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 1024,
				128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuButtonPlay = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(buttonTexture, context, "play.png");
		mMenuButtonOptions = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(buttonTexture, context, "options.png");*/
		if(buttonTiledTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture2 = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 530, 80);
				buttonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture2, context, "button02.png", 2, 1);
				texture2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture2.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(muteTiledTextureRegion==null) {
			try {
				BuildableBitmapTextureAtlas texture2 = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 150, 80);
				muteTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture2, context, "muted.png", 2, 1);
				texture2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture2.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		if(mMenuBackgroundTextureRegion==null){
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager() ,16 , 512);
				mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "menuBackground2.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}	
		
		if(mMenuBilliardsTextureRegion==null){
			try {
				BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager() ,256 , 256);
				mMenuBilliardsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "Menuballs.png");
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}	
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
	}
	
	// Once again, this method is similar to the 'Game' scene's for unloading
	private void unloadMenuTextures(){
		/*// call unload to remove the corresponding texture atlas from memory
		
		mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mMenuButtonPlay.getTexture();
		mBitmapTextureAtlas.unload();
		mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mMenuButtonOptions.getTexture();
		mBitmapTextureAtlas.unload();*/
		// ... Continue to unload all textures related to the 'Game' scene
		// button texture:
		if(mMenuBackgroundTextureRegion!=null){
			if(mMenuBackgroundTextureRegion.getTexture().isLoadedToHardware()){
				mMenuBackgroundTextureRegion.getTexture().unload();
				mMenuBackgroundTextureRegion = null;
			}
		}
		if(buttonTiledTextureRegion!=null) {
			if(buttonTiledTextureRegion.getTexture().isLoadedToHardware()) {
				buttonTiledTextureRegion.getTexture().unload();
				buttonTiledTextureRegion = null;
			}
		}
		if(mMenuBilliardsTextureRegion!=null) {
			if(mMenuBilliardsTextureRegion.getTexture().isLoadedToHardware()) {
				mMenuBilliardsTextureRegion.getTexture().unload();
				mMenuBilliardsTextureRegion = null;
			}
		}
		// Once all textures have been unloaded, attempt to invoke the Garbage Collector
		System.gc();
	}
	
	/* Lastly, we've got the loadFonts method which, once again,
	 * tends to only need to be loaded once as Font's are generally 
	 * used across an entire game, from menu to shop to game-play.
	 */
	private void loadFonts(){
		FontFactory.setAssetBasePath("font/");
		if(mMenuFont==null){
			final ITexture fontTexture = new BitmapTextureAtlas(engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
			mMenuFont = FontFactory.createFromAsset(engine.getFontManager(), fontTexture, context.getAssets(), "Plok.ttf", 36, true, android.graphics.Color.WHITE);
			mMenuFont.load();
		}
		if(mGameFont==null){
			mGameFont = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, 
					Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  20f, true, Color.WHITE_ARGB_PACKED_INT);
			mGameFont.load();
		}
		if(mGameFontLarge==null){
			mGameFontLarge = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, 
					Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  32f, true, Color.WHITE_ARGB_PACKED_INT);
			mGameFontLarge.load();
		}
		if(mStrokeFont==null){
			final ITexture strokeFontTexture = new BitmapTextureAtlas(engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
			mStrokeFont = new StrokeFont(engine.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.WHITE, 2, Color.BLUE);
			mStrokeFont.load();
		}
	}
	
	/*** If an unloadFonts() method is necessary, we can provide one ***/
	private void unloadFonts(){
		// Similar to textures, we can call unload() to destroy font resources
		if(mMenuFont!=null){
			mMenuFont.unload();
			mMenuFont = null;
		}
		if(mGameFont!=null){
			mGameFont.unload();
			mGameFont = null;
		}
		if(mGameFontLarge!=null){
			mGameFontLarge.unload();
			mGameFontLarge = null;
		}
		if(mStrokeFont!=null){
			mStrokeFont.unload();
			mStrokeFont = null;
		}
	}
}