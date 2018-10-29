package com.rjbasitali.wordssearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rjbasitali.wordssearch.model.Word;
import com.rjbasitali.wordssearch.util.FontManager;

/**
 * Created by Basit on 12/10/2016.
 */

public class WordsGrid extends View {

    private int rows, columns, width, height, rectWH;

    private char[][] letters;
    private Word[] words;

    private Cell[][] cells;
    private Cell cellDragFrom, cellDragTo;

    private Paint textPaint;
    private Paint highlighterPaint;
    private Paint gridLinePaint;

    private OnWordSearchedListener onWordSearchedListener;
    private int wordsSearched = 0;
    private int[] highlighterColors = {0x4400649C, 0x44ffd900, 0x447fbb00};

    public WordsGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setSubpixelText(true);
        textPaint.setColor(0xcc000000);
        textPaint.setTextSize(70);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(FontManager.getTypeface(getContext(), FontManager.POYNTER));
//        textPaint.setAlpha(foregroundOpacity);

        highlighterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlighterPaint.setStyle(Paint.Style.STROKE);
        highlighterPaint.setStrokeWidth(110);
        highlighterPaint.setStrokeCap(Paint.Cap.ROUND);
        highlighterPaint.setColor(0x4400649C);

        gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(4);
        gridLinePaint.setStrokeCap(Paint.Cap.SQUARE);
        gridLinePaint.setColor(0x11000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(rows <= 0 || columns <= 0) {
            return;
        }

        // draw grid
        for(int i = 0; i < rows - 1; i++) {
            canvas.drawLine(0, cells[i][0].getRect().bottom, width, cells[i][0].getRect().bottom, gridLinePaint);
        }
        for(int i = 0; i < columns - 1; i++) {
            canvas.drawLine(cells[0][i].getRect().right, cells[0][0].getRect().top, cells[0][i].getRect().right, cells[columns-1][0].getRect().bottom, gridLinePaint);
        }

        // draw letters
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                String letter = String.valueOf(cells[i][j].getLetter());
                Rect textBounds = new Rect();
                textPaint.getTextBounds(letter, 0, 1, textBounds);
                canvas.drawText(letter, cells[i][j].getRect().centerX() - (textPaint.measureText(letter) / 2),
                        cells[i][j].getRect().centerY() + (textBounds.height() / 2), textPaint);
            }
        }

        // draw highlighter
        if(cellDragFrom != null && cellDragTo != null && isFromToValid(cellDragFrom, cellDragTo)) {
//            highlighterPaint.setColor(highlighterColors[wordsSearched]);
            canvas.drawLine(cellDragFrom.getRect().centerX(), cellDragFrom.getRect().centerY(),
                    cellDragTo.getRect().centerX() + 1, cellDragTo.getRect().centerY(), highlighterPaint);
        }

        for(Word word : words) {
            if(word.isHighlighted()) {
                canvas.drawLine(
                        cells[word.getFromRow()][word.getFromColumn()].getRect().centerX(),
                        cells[word.getFromRow()][word.getFromColumn()].getRect().centerY(),
                        cells[word.getToRow()][word.getToColumn()].getRect().centerX() + 1,
                        cells[word.getToRow()][word.getToColumn()].getRect().centerY(), highlighterPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(d, d);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;

        initCells();
    }

    private void initCells() {
        if(rows <= 0 || columns <= 0) {
            return;
        }
        cells = new Cell[rows][columns];
        rectWH = width/rows;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(new Rect(j*rectWH,i*rectWH,(j+1)*rectWH,(i+1)*rectWH),letters[i][j],i,j);
            }
        }
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 100;
        } else {
            result = specSize;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        final float x = MotionEventCompat.getX(event, pointerIndex);
        final float y = MotionEventCompat.getY(event, pointerIndex);

//        Log.d("WordsGrid", "x:" + x + ", y:" + y);

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            cellDragFrom = getCell(x,y);
            cellDragTo = cellDragFrom;
            invalidate();
        } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            Cell cell = getCell(x,y);
            if(cellDragFrom != null && cell != null && isFromToValid(cellDragFrom, cell)) {
                cellDragTo = cell;
                invalidate();
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
//            Log.d("WordsGrid", getWordStr(cellDragFrom, cellDragTo));
            String word = getWordStr(cellDragFrom, cellDragTo);
            highlightIfContain(word);
            cellDragFrom = null;
            cellDragTo = null;
            invalidate();
            return false;
        }
        return true;
    }

    private Cell getCell(float x, float y) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(cells[i][j].getRect().contains((int)x,(int)y)) {
                    return cells[i][j];
                }
            }
        }
        return null;
    }

    public void setLetters(char[][] letters) {
        this.letters = letters;
        rows = letters.length;
        columns = letters[0].length;
        initCells();
        invalidate();
    }

    private boolean isFromToValid(Cell cellDragFrom, Cell cellDragTo) {
        return (Math.abs(cellDragFrom.getRow() - cellDragTo.getRow()) == Math.abs(cellDragFrom.getColumn() - cellDragTo.getColumn()))
                || cellDragFrom.getRow() == cellDragTo.getRow() || cellDragFrom.getColumn() == cellDragTo.getColumn();
    }

    private class Cell {
        private Rect rect;
        private char letter;
        private int rowIndex, columnIndex;

        public Cell(Rect rect, char letter, int rowIndex, int columnIndex) {
            this.rect = rect;
            this.letter = letter;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        public Rect getRect() {
            return rect;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public char getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
        }

        public int getRow() {
            return rowIndex;
        }

        public void setRow(int row) {
            this.rowIndex = row;
        }

        public int getColumn() {
            return columnIndex;
        }

        public void setColumn(int column) {
            this.columnIndex = column;
        }
    }

    public interface OnWordSearchedListener {
        public void wordFound(String word);
    }

    public void setOnWordSearchedListener(OnWordSearchedListener onWordSearchedListener) {
        this.onWordSearchedListener = onWordSearchedListener;
    }

    public void setWords(Word... words) {
        this.words = words;
    }

    private String getWordStr(Cell from, Cell to) {
        String word = "";
        if(from.getRow() == to.getRow()) {
            int c = from.getColumn() < to.getColumn() ? from.getColumn() : to.getColumn();
            for(int i = 0; i < Math.abs(from.getColumn() - to.getColumn()) + 1; i++) {
                word += String.valueOf(cells[from.getRow()][i+c].getLetter());
            }
        } else if(from.getColumn() == to.getColumn()) {
            int r = from.getRow() < to.getRow() ? from.getRow() : to.getRow();
            for(int i = 0; i < Math.abs(from.getRow() - to.getRow()) + 1; i++) {
                word += String.valueOf(cells[i+r][to.getColumn()].getLetter());
            }
        } else {
            if(from.getRow() > to.getRow()) {
                Cell cell = from;
                from = to;
                to = cell;
            }
            for(int i = 0; i < Math.abs(from.getRow() - to.getRow()) + 1; i++) {
                int foo = from.getColumn() < to.getColumn() ? i : -i;
                word += String.valueOf(cells[from.getRow()+i][from.getColumn()+foo].getLetter());
            }
        }
        return word;
    }

    private void highlightIfContain(String str) {
        for(Word word : words) {
            if(word.getWord().equals(str)) {
                if(onWordSearchedListener != null) {
                    onWordSearchedListener.wordFound(str);
                }
                word.setHighlighted(true);
                wordsSearched++;
                break;
            }
        }
    }
}
