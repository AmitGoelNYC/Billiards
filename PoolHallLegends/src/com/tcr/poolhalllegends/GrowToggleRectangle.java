package com.tcr.poolhalllegends;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;

public abstract class GrowToggleRectangle extends Rectangle {
	
	// ====================================================
	// CONSTANTS
	// ====================================================
	private static final float mGROW_DURATION_SECONDS = 0.05f;
	private static final float mNORMAL_SCALE_DEFAULT = 1f;
	private static final float mGROWN_SCALE_DEFAULT = 1.4f;
	private static final float mENABLED_ALPHA = 1f;
	private static final float mDISABLED_ALPHA = 0.5f;
	
	// ====================================================
	// VARIABLES
	// ====================================================
	public boolean mIsEnabled = true;
	private float mNormalScale = mNORMAL_SCALE_DEFAULT;
	private float mGrownScale = mGROWN_SCALE_DEFAULT;
	private boolean mIsTouched = false;
	private boolean mIsLarge = false;
	private boolean mIsClicked = false;
	private boolean mTouchStartedOnThis = false;
	
	private static float[][] colors = {{0, 0, 0.8784f,1} //blue
		,{17/255f,  68/255f, 7/255f, 1}//green
		,{83f/255f, 28f/255f, 8f/255f, 1}//red
		,{200f/255f, 184f/255f, 51/255f, 1} //yellow
		,{227f/255f, 159f/255f, 92/255f, 1} //orange
		,{114f/255f, 127f/255f, 124/255f, 1} //gray
	};
	private static int currentColor;
	
	// ====================================================
	// ABSTRACT METHODS
	// ====================================================
	public abstract void onClick();
	/*public abstract int checkState();*/
	
	// ====================================================
	// CONSTRUCTOR
	// ====================================================
	public GrowToggleRectangle(final float pX, final float pY,final float pWidth, final float pHeight, final int pCurrentColor) {
		super(pX, pY, pWidth, pHeight, ResourceManager.getActivity().getVertexBufferObjectManager());
		currentColor = pCurrentColor;
		this.setColor(colors[currentColor][0],colors[currentColor][1],colors[currentColor][2],colors[currentColor][3]);
	}
	
	// ====================================================
	// METHODS
	// ====================================================
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if(!mIsLarge && mIsTouched) {
			this.registerEntityModifier(new ScaleModifier(mGROW_DURATION_SECONDS, mNormalScale, mGrownScale) {
				@Override
				protected void onModifierFinished(final IEntity pItem) {
					super.onModifierFinished(pItem);
					mIsLarge = true;
				}
			});
		} else if(mIsLarge && !mIsTouched) {
			this.registerEntityModifier(new ScaleModifier(mGROW_DURATION_SECONDS, mGrownScale, mNormalScale) {
				@Override
				protected void onModifierFinished(final IEntity pItem) {
					super.onModifierFinished(pItem);
					mIsLarge = false;
					if(mIsClicked) {
						onClick();
						mIsClicked = false;
					}
				}
			});
			mIsLarge = false;
		}
		if(mIsEnabled) {
			if(this.getAlpha()!=mENABLED_ALPHA)
				this.setAlpha(mENABLED_ALPHA);
		} else {
			if(this.getAlpha()!=mDISABLED_ALPHA)
				this.setAlpha(mDISABLED_ALPHA);
		}
		this.setColor(colors[currentColor][0],colors[currentColor][1],colors[currentColor][2],colors[currentColor][3]);
		
	}
	
	public static int getColorLength(){
		return colors.length;
	}
	
	public static void setCurrentColor(int newColor){
		currentColor = newColor;
	}
	
	public static float[] getColors(int colorIndex){
		return colors[colorIndex];
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			if(pTouchAreaLocalX>this.getWidth() || pTouchAreaLocalX < 0f || pTouchAreaLocalY>this.getHeight() || pTouchAreaLocalY < 0f) {
				mTouchStartedOnThis = false;
			} else {
				mTouchStartedOnThis = true;
			}
			
			if(mIsEnabled)
				mIsTouched = true;
		} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE) {
			if(mTouchStartedOnThis)
				if(pTouchAreaLocalX>this.getWidth() || pTouchAreaLocalX < 0f || pTouchAreaLocalY>this.getHeight() || pTouchAreaLocalY < 0f) {
					if(mIsTouched) {
						mIsTouched = false;
					}
				} else {
					if(!mIsTouched && mTouchStartedOnThis)
						if(mIsEnabled)
							mIsTouched = true;
				}
		} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP && mIsTouched && mTouchStartedOnThis) {
			mIsTouched = false;
			mIsClicked = true;
			mTouchStartedOnThis = false;
			SFXManager.playClick(1f, 1f,"menuClick");
		}
		return true;
	}
}