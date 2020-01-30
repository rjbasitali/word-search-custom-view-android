# Word Search game custom view

### Summary

Word search custom view for android, includes an interactive board of characters which can be initialised using your custom algorithm to generate a 2D array of characters including hidden words to be found.


![Screenshot](https://github.com/rjbasitali/word-search-custom-view-android/blob/master/screenshot.jpg)


### Download

You can use Gradle:

```
dependencies {
	implementation 'com.rjbasitali:wordsearch:0.1'
}
```

or Maven:

```
<dependency>
  <groupId>com.rjbasitali</groupId>
  <artifactId>wordsearch</artifactId>
  <version>0.1</version>
  <type>pom</type>
</dependency>
```

### Use

```
wordsGrid = findViewById(R.id.wordsGrid);
wordsGrid.setTypeface(FontManager.getTypeface(this, FontManager.POYNTER));

wordsGrid.setLetters(new char[][] {
        "ASCDEFGHIJ".toCharArray(),
        "AECDEFGHIJ".toCharArray(),
        "AACDEFGHIJ".toCharArray(),
        "ARCWEFGHIJ".toCharArray(),
        "ACCDOFGHIJ".toCharArray(),
        "AHCGERGHIJ".toCharArray(),
        "AICDEFDHIJ".toCharArray(),
        "ANCDEFGHIJ".toCharArray(),
        "AGCSOMEHIJ".toCharArray(),
        "ABCDEFGHIJ".toCharArray()
});

wordsGrid.setWords(
        new Word("WORD", false, 3, 3, 6, 6),
        new Word("SOME", false, 8, 3, 8, 6),
        new Word("SEARCHING", false, 0, 1, 8, 1),
        new Word("FOG", false, 3, 5, 5, 3));

wordsGrid.setOnWordSearchedListener(new WordSearchView.OnWordSearchedListener() {
    @Override
    public void wordFound(String word) {
        Toast.makeText(MainActivity.this, word + " found", Toast.LENGTH_SHORT).show();
    }
});
```

