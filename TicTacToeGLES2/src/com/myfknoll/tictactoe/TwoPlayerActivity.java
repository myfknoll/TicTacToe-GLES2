package com.myfknoll.tictactoe;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;

/**
 * TicTacToe Applikation
 *
 * @author Florian Knoll -  myfknoll(at)gmail.com
 *
 * @version 1.0.0
 */
public class TwoPlayerActivity extends OnePlayerActivity{
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private int currentplayer = 1;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        // TODO Auto-generated method stub

        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

            int x = (int) pSceneTouchEvent.getX();
            int y = (int) pSceneTouchEvent.getY();

            switch (status()) {
            case WIN:
			  case LOSE:
			  case STALEMATE:
			  //  play(getCodeBase(), "audio/return.au");
				  TwoPlayerActivity.this.mReturnSound.play();

				  runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							/* Now it is save to remove the entity! */
							  mScene.detachChildren();
							LoadNewGame();

							white = black = 0;
						}
					});

			    return true;
			}
			// Figure out the row/column
			int c = (x * 3) / CAMERA_WIDTH;
			int r = (y * 3) / CAMERA_HEIGHT;

			if(currentplayer==1) {
                if (yourMove(c + r * 3)) {

					Sprite cross = new Sprite(
							c*(CAMERA_WIDTH/3)+(CAMERA_WIDTH/3-mCrossTextureRegion.getWidth())/2,
							r*(CAMERA_HEIGHT/3)+(CAMERA_HEIGHT/3-mCrossTextureRegion.getHeight())/2,
							mCrossTextureRegion,getVertexBufferObjectManager());
					mScene.attachChild(cross);

                    switch (status()) {
                    case WIN:
                        TwoPlayerActivity.this.mYahoo1Sound.play();
                        break;
                    case LOSE:
                        TwoPlayerActivity.this.mYahoo2Sound.play();
                        break;
                    case STALEMATE:
                        break;
                    default:
                        TwoPlayerActivity.this.mDingSound.play();
                        currentplayer = 2;
                        break;
                    }

                } else {
                    TwoPlayerActivity.this.mBeepSound.play();
                }
            } else if (yourMove2(c + r * 3)) {

				Sprite circle = new Sprite(
						c*(CAMERA_WIDTH/3)+(CAMERA_WIDTH/3-mCircleTextureRegion.getWidth())/2,
						r*(CAMERA_HEIGHT/3)+(CAMERA_HEIGHT/3-mCircleTextureRegion.getHeight())/2,
						mCircleTextureRegion,getVertexBufferObjectManager());
                mScene.attachChild(circle);

                switch (status()) {
                case WIN:
                    TwoPlayerActivity.this.mYahoo1Sound.play();
                    break;
                case LOSE:
                    TwoPlayerActivity.this.mYahoo2Sound.play();
                    Log.w("status", "yahoo2");
                    break;
                case STALEMATE:
                    break;
                default:
                    TwoPlayerActivity.this.mDingSound.play();
                    currentplayer = 1;
                    break;
                }
            } else {
                TwoPlayerActivity.this.mBeepSound.play();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * User2 move.
     *
     * @return true if legal
     */
    boolean yourMove2(final int m) {
        if ((m < 0) || (m > 8)) {
            return false;
        }
        if (((black | white) & (1 << m)) != 0) {
            return false;
        }
        white |= 1 << m;
        return true;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}