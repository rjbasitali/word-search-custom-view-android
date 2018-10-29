package com.rjbasitali.wordssearch;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.rjbasitali.wordssearch.model.Word;
import com.rjbasitali.wordssearch.views.WordsGrid;

public class MainActivity extends AppCompatActivity {

    private WordsGrid wordsGrid;

    private char[][] letters  = {
        {'A','S','C','D','E','F','G','H','I','J'},
        {'A','E','C','D','E','F','G','H','I','J'},
        {'A','A','C','D','E','F','G','H','I','J'},
        {'A','R','C','W','E','F','G','H','I','J'},
        {'A','C','C','D','O','F','G','H','I','J'},
        {'A','H','C','G','E','R','G','H','I','J'},
        {'A','I','C','D','E','F','D','H','I','J'},
        {'A','N','C','D','E','F','G','H','I','J'},
        {'A','G','C','S','O','M','E','H','I','J'},
        {'A','B','C','D','E','F','G','H','I','J'}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvStrikethrough = (TextView) findViewById(R.id.tv_strikethrough);
        tvStrikethrough.setPaintFlags(tvStrikethrough.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        wordsGrid = (WordsGrid) findViewById(R.id.wordsGrid);
        wordsGrid.setLetters(letters);
        wordsGrid.setWords(
                new Word("WORD", false, 3, 3, 6, 6),
                new Word("SOME", false, 8, 3, 8, 6),
                new Word("SEARCHING", false, 0, 1, 8, 1),
                new Word("FOG", false, 3, 5, 5, 3));
        wordsGrid.setOnWordSearchedListener(new WordsGrid.OnWordSearchedListener() {
            @Override
            public void wordFound(String word) {
                Toast.makeText(MainActivity.this, word + " found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
