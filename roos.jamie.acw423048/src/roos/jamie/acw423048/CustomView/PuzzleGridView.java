package roos.jamie.acw423048.CustomView;

import java.util.Random;
import java.util.Vector;

import roos.jamie.acw423048.PuzzleActivity;
import roos.jamie.acw423048.R;
import roos.jamie.acw423048.Puzzle.OnWordFoundListener;
import roos.jamie.acw423048.Puzzle.SolutionWord;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleGridView extends GridView implements OnTouchListener{
	Paint drawPaint = new Paint();
	Paint fixedPaint = new Paint();
	float startX;
	float startY;
	float stopX;
	float stopY;
	int startGridPos;
	int currentGridPos;
	
	Random rnd = new Random();
	
	Point startGridSquare = new Point();
	Point endGridSquare = new Point();
	
	int lineDirection;
	public Vector<SolutionWord> solutionWords = new Vector<SolutionWord>();
	
	private OnWordFoundListener wordFoundListener;
	
	public Bundle getSavedState()
	{
		Bundle saveBundle = new Bundle();
		saveBundle.putSerializable("solutionWords", solutionWords);
		return saveBundle;
	}
	
	@SuppressWarnings("unchecked")
	public void restoreSavedState(Bundle savedInstanceState) {
		this.solutionWords =  (Vector<SolutionWord>)savedInstanceState.getSerializable("solutionWords");
	}
	
	private void resetCoords()
	{
		startX = -100;
		startY = -100;
		stopX = -100; 
		stopY = -100;

		startGridSquare.x = 0;
		startGridSquare.y = 0;
		endGridSquare.x = 0;
		endGridSquare.y = 0;
		
		lineDirection = -1;
	}
	
	public PuzzleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public PuzzleGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public PuzzleGridView(Context context) {
		super(context);
		init(context);
	}
	
	private void setOnWordChangedListener(OnWordFoundListener listener)
	{
		this.wordFoundListener = listener;
	}
	
	private void init(Context context)
	{
		this.setOnTouchListener(this);
		this.setOnWordChangedListener((PuzzleActivity)context);
		resetCoords();
		
		// setup draw paint
		drawPaint.setColor(Color.RED);
		drawPaint.setStrokeWidth(15);
		drawPaint.setAlpha(50);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		
		// fixed paint
		fixedPaint.setColor(Color.YELLOW);
		fixedPaint.setStrokeWidth(80);
		fixedPaint.setAlpha(50);
		fixedPaint.setStyle(Paint.Style.STROKE);
		fixedPaint.setStrokeJoin(Paint.Join.ROUND);
		fixedPaint.setStrokeCap(Paint.Cap.ROUND);
		
		
		// check that all elements are visible
		
		
//		for(int i = 0; i < this.getChildCount(); i++)
//		{
//			TextView thisChildView = (TextView)this.getChildAt(i);
//			Rect scrollBounds = new Rect();
//			this.getHitRect(scrollBounds);
//			if (thisChildView.getLocalVisibleRect(scrollBounds)) {
//			    // thisChildView is within the visible window
//			} else {
//			    // thisChildView is not within the visible window
//			}
//		}
	}
	
	
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawLine(startX, startY, stopX, stopY, drawPaint);
		if(solutionWords.size() > 0)
		{
			for(int i = 0; i < solutionWords.size(); i++)
			{
				SolutionWord word = solutionWords.get(i); 
				fixedPaint.setColor(word.colorToDraw);
				fixedPaint.setAlpha(160);
				drawLineOnGrid(word.Row, word.Column, word.Direction, word.Word.length(), fixedPaint, canvas);
			}
		}
	}

	public void drawLineOnGrid(int startLineX, int startLineY, int direction, int length, Paint paint, Canvas canvas)
	{
		int numCols = this.getNumColumns();
		int numRows = (int) Math.ceil(this.getCount()/numCols);
		
		int position = startLineX + (numRows-1-startLineY) * numCols;
		int[] positions = new int[length];
		positions[0] = position;
		int offset = 0;
		offset = getOffset(this.getNumColumns(), offset, direction);
		for(int i = 1; i < length; i++)
		{
			positions[i] = positions[i-1]+offset;
		}
		
		TextView firstView = (TextView) this.getChildAt(position);
		TextView lastView = (TextView) this.getChildAt(positions[positions.length-1]);
		
		Point startPoint = new Point((int)(firstView.getX() + firstView.getWidth()/2), (int)(firstView.getY() + firstView.getHeight()/2));
		Point endPoint = new Point((int)lastView.getX() + lastView.getWidth()/2, (int)lastView.getY() + lastView.getHeight()/2);
		
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent me) {
		GridView gridView = (GridView)v;
		int action = me.getActionMasked();
		float currentXPosition = 0;
		float currentYPosition = 0;
		try
		{
			currentXPosition = me.getX();
			currentYPosition = me.getY();
			int position = (gridView.pointToPosition((int) currentXPosition, (int) currentYPosition));
	        TextView childView = (TextView) gridView.getChildAt(position);
	        float x = childView.getX() + childView.getWidth()/2;
	        float y = childView.getY() + childView.getHeight()/2;
	        
	        int numCols = gridView.getNumColumns();
	        int numRows = (int) Math.ceil(gridView.getCount()/numCols);
	        int rowForCol = ((int) Math.ceil((position)/numCols))-numRows;
	        int row = Math.abs(((int) Math.ceil((position)/numCols))-numRows)-1;
	        int col = Math.abs((rowForCol * numCols) - position)-gridView.getCount();
	        
	        switch(action)
	        {
	            case MotionEvent.ACTION_DOWN:
	            	startGridPos = position;
	            	startGridSquare.x = row;
	            	startGridSquare.y = col;
	            	endGridSquare.x = row;
	            	endGridSquare.y = col;
	            	startX = x;
	            	startY = y;
	            	stopX = x;
	            	stopY = y;
	            	break;
	            case MotionEvent.ACTION_MOVE:
	            	currentGridPos = position;
	            	endGridSquare.x = row;
	            	endGridSquare.y = col;
	            	stopX = x;
	            	stopY = y;
	            	// work out the angle between the two points
	            	float angle = (float) Math.toDegrees(Math.atan2(endGridSquare.y - startGridSquare.y, endGridSquare.x - startGridSquare.x));
	            	
	            	getDirection(angle);
	            	//Log.i("Info", "Direction = "+lineDirection+"; X = "+endGridSquare.x + "; Y = "+endGridSquare.y);
	            	break;
	        	case MotionEvent.ACTION_UP:
	        		if(lineDirection != -1 && !((startX == stopX) && (startY == stopY)))
	            	{
	        			// we have a valid point
	        			SolutionWord newWord = new SolutionWord();
	        			newWord.Column = startGridSquare.x;
	        			newWord.Row = startGridSquare.y;
	        			
	        			newWord.Direction = lineDirection;
	        			int color = Color.argb(fixedPaint.getAlpha(), rnd.nextInt(256), rnd.nextInt(256),rnd.nextInt(256));
	        			newWord.colorToDraw = color;
	        			
	        			StringBuilder word = new StringBuilder();
	        			// start letter
	        			int startPos = pointToPosition((int)startX, (int)startY);
	        			TextView viewStart = (TextView)gridView.getChildAt(startPos);
	        			word.append(viewStart.getText());
	        			int offset = 0;
	        			offset = getOffset(numCols, offset, lineDirection);
	        			// end letter
	        			int endPos = pointToPosition((int)stopX, (int)stopY);
	        			int currentPos = startPos + offset;
	        			while(currentPos != endPos)
	        			{
	        				word.append(((TextView)gridView.getChildAt(currentPos)).getText());
	        				currentPos += offset;
	        			}
	        			word.append(((TextView)gridView.getChildAt(endPos)).getText());
	        			
	        			newWord.Word = word.toString();
	        			//Toast.makeText(v.getContext(), word.toString(),Toast.LENGTH_SHORT).show();
	        			
	        			LinearLayout wordsLayout = (LinearLayout)((LinearLayout)this.getParent()).findViewById(R.id.puzzleWordList);
	        			
	        			boolean wordFound = false;
	        			for(int c = 0; c < wordsLayout.getChildCount(); c++)
	        			{
	        				TextView thisTextArea = (TextView)wordsLayout.getChildAt(c);
	        				if(thisTextArea.getText().toString().toUpperCase().equals(word.toString().toUpperCase()))
	        				{
	        					wordFound = true;
	        					thisTextArea.setPaintFlags(thisTextArea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	        				}
	        			}
	        			if(wordFound)
	        			{
	        				solutionWords.add(newWord);
	        				wordFoundListener.onWordFound(this, newWord);
	        			}
	        			else
	        			{
	        				Toast.makeText(v.getContext(), "Word \""+word.toString()+"\" not found in list ", Toast.LENGTH_SHORT).show();
	        			}
        			}
	        		resetCoords();
	        		break;
	        }
		}
		catch (NullPointerException e)
		{
			// we left the area of the wordsearch
			// leaving this empty gives the desired behaviour for the moment
		}
        invalidate();
		return true;
	}

	private int getOffset(int numCols, int offset, int direction) {
		switch(direction)
		{
			case 0: // down left
				offset = numCols - 1;
				break;
			case 1: // down
				offset = numCols;
				break;
			case 2: // down right
				offset = numCols+1;
				break;
			case 3: // left
				offset = -1;
				break;
			case 4: // right
				offset = 1;
				break;
			case 5: // up left
				offset = -numCols - 1;
				break;
			case 6: // up
				offset = -numCols;
				break;
			case 7: // up right
				offset = -numCols + 1;
				break;
		}
		return offset;
	}

	private void getDirection(float angle) {
		switch((int)angle)
		{
			case 0:
				lineDirection = 6;
				SetValidPaint();
				break;
			case 45:
				lineDirection = 7;
				SetValidPaint();
				break;
			case 90:
				lineDirection = 4;
				SetValidPaint();
				break;
			case 135:
				lineDirection = 2;
				SetValidPaint();
				break;
			case 180:
			case -180:
				lineDirection = 1;
				SetValidPaint();
				break;
			case -135:
				lineDirection = 0;
				SetValidPaint();
				break;
			case -90:
				lineDirection = 3;
				SetValidPaint();
				break;
			case -45:
				lineDirection = 5;
				SetValidPaint();
				break;
			default:
				lineDirection = -1;
				SetInvalidPaint();
				break;
		}
	}
	
	@Override
	public boolean performClick()
	{
		return super.performClick();
	}
	
	private void SetValidPaint()
	{
		drawPaint.setColor(Color.BLUE);
		drawPaint.setAlpha(60);
		drawPaint.setStrokeWidth(60);
	}
	
	private void SetInvalidPaint()
	{
		drawPaint.setColor(Color.RED);
		drawPaint.setAlpha(50);
		drawPaint.setStrokeWidth(15);
	}
}