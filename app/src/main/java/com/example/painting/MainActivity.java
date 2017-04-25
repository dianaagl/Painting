package com.example.painting;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

    private PaintingView mPaintingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaintingView = (PaintingView) findViewById(R.id.painting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear:
                mPaintingView.clear();
                break;
            case R.id.draw_rects:
                mPaintingView.setDrawMode(1);
                break;
            case R.id.drawShape:
                //mPaintingView.setDrawMode(2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
