package com.example.helloworldall;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;

import android.util.Log;

/** This class handles the playback of music and sounds as well as
*** their muted state.
**/

public class SFXManager extends Object{
	// ====================================================
	// CONSTANTS
	// ====================================================
	private static final SFXManager INSTANCE = new SFXManager();
	
	private static Sound mMenuSound;
	private static Sound mBallPocketed;
	private static Sound mCueBallHit;
	private static Music mMusic;
 
	// ====================================================
	// INSTANCE GETTER
	// ====================================================
	public static SFXManager getInstance(){
		return INSTANCE;
	}
	
	// ====================================================
	// VARIABLES
	// ====================================================
	public boolean mSoundsMuted;
	public boolean mMusicMuted;
	
	// ====================================================
	// CONSTRUCTOR
	// ====================================================
	public SFXManager() {
		MusicFactory.setAssetBasePath("sfx/");
		SoundFactory.setAssetBasePath("sfx/");
		if(mMenuSound==null){
			try{
				mMenuSound = SoundFactory.createSoundFromAsset(ResourceManager.getEngine().getSoundManager(), 
						ResourceManager.getContext(), "click.mp3");
			}catch(final IOException e){
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
		if(mMusic==null){
			try{
				mMusic = MusicFactory.createMusicFromAsset(ResourceManager.getEngine().getMusicManager(), 
						ResourceManager.getContext(), "music.mp3");
				mMusic.setLooping(true);
			}catch(final IOException e){
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
		if(mBallPocketed==null){
			try{
				mBallPocketed = SoundFactory.createSoundFromAsset(ResourceManager.getEngine().getSoundManager(), 
						ResourceManager.getContext(), "ballPocketed.mp3");
			}catch(final IOException e){
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
		if(mCueBallHit==null){
			try{
				mCueBallHit = SoundFactory.createSoundFromAsset(ResourceManager.getEngine().getSoundManager(), 
						ResourceManager.getContext(), "cueHitsBall.mp3");
			}catch(final IOException e){
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
	}
	
	// ====================================================
	// METHODS
	// ====================================================
	private static void setVolumeForAllSounds(final float pVolume) {
		mMenuSound.setVolume(pVolume);
	}
	
	public static boolean isSoundMuted() {
		return getInstance().mSoundsMuted;
	}
	
	public static void setSoundMuted(final boolean pMuted) {
		getInstance().mSoundsMuted = pMuted;
		setVolumeForAllSounds((getInstance().mSoundsMuted? 0f:1f));
		MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_SOUNDS_MUTED, (getInstance().mSoundsMuted? 1:0));
	}
	
	public static boolean toggleSoundMuted() {
		getInstance().mSoundsMuted = !getInstance().mSoundsMuted;
		setVolumeForAllSounds((getInstance().mSoundsMuted? 0f:1f));
		MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_SOUNDS_MUTED, (getInstance().mSoundsMuted? 1:0));
		return getInstance().mSoundsMuted;
	}
	
	public static boolean isMusicMuted() {
		return getInstance().mMusicMuted;
	}
	
	public static void setMusicMuted(final boolean pMuted) {
		getInstance().mMusicMuted = pMuted;
		if(getInstance().mMusicMuted) mMusic.pause(); else mMusic.play();
		MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_MUSIC_MUTED, (getInstance().mMusicMuted? 1:0));
	}
	
	public static boolean toggleMusicMuted() {
		getInstance().mMusicMuted = !getInstance().mMusicMuted;
		if(getInstance().mMusicMuted) mMusic.pause(); else mMusic.play();
		MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_MUSIC_MUTED, (getInstance().mMusicMuted? 1:0));
		return getInstance().mMusicMuted;
	}
	
	
	public static void playMusic() {
		if(!isMusicMuted())
			mMusic.play();
	}
	
	public static void pauseMusic() {
		mMusic.pause();
	}
	
	public static void resumeMusic() {
		if(!isMusicMuted())
			mMusic.resume();
	}
	
	public static float getMusicVolume() {
		return mMusic.getVolume();
	}
	
	public static void setMusicVolume(final float pVolume) {
		mMusic.setVolume(pVolume);
	}
	
	public static void playClick(final float pRate, final float pVolume,String soundString) {
		if(soundString.equals("menuClick")){
			playSound(mMenuSound,pRate,pVolume);
		}else if (soundString.equals("ballPocket")){
			playSound(mBallPocketed,pRate,pVolume);
		}else if (soundString.equals("cueHitBall")){
			playSound(mCueBallHit,pRate,pVolume);
		}
		
	}
	
	private static void playSound(final Sound pSound, final float pRate, final float pVolume) {
		if(SFXManager.isSoundMuted()) return;
		pSound.setRate(pRate);
		pSound.setVolume(pVolume);
		pSound.play();
	}
}