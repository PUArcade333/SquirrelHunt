package com.squirrel;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * TetrisView: implementation of a simple game of Tetris
 */
public class SquirrelView extends TileView {

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int GREEN = 1;
	private static final int WALL = 2;

	private static double  currX;
	private static double currY;
	private static int range;
	
	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;
	
	/**
	 * mLastMove: tracks the absolute time when the last Squirrel
	 * spawned, and tells whether or not a new one should spawn.
	 */
	private long mLastMove;

	/** Maximum delay between each Squirrel **/
	private int mDelay = 1000;
	/** Keeps track of when the game starts **/
	private long mFirst;
	/** Game length: 30 seconds **/
	private int mTotalTime = 30000;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SquirrelView.this.update();
			SquirrelView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};	
	/**
	 * Constructs a SquirrelView based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public SquirrelView(Context context)
	{
		super(context);
	}
	public SquirrelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquirrelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(3);
		loadTile(GREEN, r.getDrawable(R.drawable.greenunit));
		loadTile(WALL, r.getDrawable(R.drawable.wall));
		
		updateWalls();
		drawNew();
		
		mFirst = System.currentTimeMillis();
		range = mTileSize;
//		mLastMove = System.currentTimeMillis();
	}

	/**
	 * Sets the TextView that will be used to give information (such as "Game
	 * Over" to the user.
	 * 
	 * @param newView
	 */
	public void setTextView(TextView newView) {
		mStatusText = newView;
	}

	public boolean checkTouched(double x, double y)
	{
//		double xSum = x - xOffset; 
//		double ySum = y - yOffset - barOffset; 
		Log.d("checkX+Y", "x: " + x + " + y: " + y);
		Log.d("checkOffset", "x: " + xOffset + " + y: " + yOffset);
		Log.d("checkTile", "" + mTileSize + " + " + barOffset + " + " + range);
		
//		int xCounter = -1;
//		int yCounter = -1;
//		
//		while (xSum > 0)
//		{
//			xCounter++;
//			xSum -= mTileSize;
//		}
//		
//		while (ySum > 0)
//		{
//			yCounter++;
//			ySum -= mTileSize;
//		}
		
		Log.d("checkTouchedX", "" + currX + " + " + x);
		Log.d("checkTouchedY", "" + currY + " + " + y);

		//if the point touched is within (2 * range), return true and create a new point
		if ((Math.abs(x - currX) < (range * 2.0)) && (Math.abs(y - currY) < (range * 2.0)))
		{
			return true;
		}
		
//		if ((Math.abs(currX - xCounter) < 2) && (Math.abs(currY - yCounter) < 2))
//		{
//			return true;
//		}
		
		return false;
	}
	
	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the Tetris's
	 * location.
	 */
	public void update() {
		long now = System.currentTimeMillis();

		if (now - mLastMove > mDelay) {
			clearTiles();
			updateWalls();
			drawNew();
			mLastMove = now;
		}
		
		//end game
//		if (now > mFirst + mTotalTime)
//			System.exit(0);
		
		mRedrawHandler.sleep(0);
	}

	public void updateNew() {
		clearTiles();
		updateWalls();
		drawNew();
		mLastMove = System.currentTimeMillis();
	}
	
	/**
	 * Draws some walls.
	 */
	private void updateWalls() {
		for (int x = 0; x < xTileNum; x++) {
			setTile(WALL, x, 0);
			setTile(WALL, x, yTileNum - 1);
		}
		for (int y = 1; y < xTileNum - 1; y++) {
			setTile(WALL, 0, y);
			setTile(WALL, yTileNum - 1, y);
		}
	}

	/**
	 * Create and draw the next block.
	 */
	public void drawNew() {
		
		Random randomGen = new Random();
		
		double oldX = currX;
		double oldY = currY;

		int xRandom = randomGen.nextInt(xTileNum - 2) + 1;
		int yRandom = randomGen.nextInt(yTileNum - 2) + 1;
		
		currX = xOffset + (xRandom * range) + (0.5 * range);
		currY = barOffset + yOffset + (yRandom * range) + (0.5 * range);

		while ((oldX == currX) && (oldY == currY))
		{
			xRandom = randomGen.nextInt(xTileNum - 2) + 1;
			yRandom = randomGen.nextInt(yTileNum - 2) + 1;
			currX = xOffset + (xRandom * range) + (0.5 * range);			
			currY = barOffset + yOffset + (yRandom * range) + (0.5 * range);
		}
				
		Log.d("xyRandom", xRandom + " " + yRandom);
		
		setTile(GREEN, xRandom, yRandom);
	}
}