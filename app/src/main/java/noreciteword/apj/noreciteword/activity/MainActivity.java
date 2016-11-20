package noreciteword.apj.noreciteword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import noreciteword.apj.noreciteword.R;
import noreciteword.apj.noreciteword.view.PanelSwitcher;

public class MainActivity extends AppCompatActivity {
    //private TextView la;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PanelSwitcher mPanelSeitcher = (PanelSwitcher) findViewById(R.id.panelswitch);
//        mPanelSeitcher.setLongClickable(true);
        View mFirstLayout = LayoutInflater.from(this).inflate(R.layout.above, null);
        View mSecondLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.behind, null);
        mPanelSeitcher.setAboveView(mFirstLayout);
        mPanelSeitcher.setBehindView(mSecondLayout);

/*        la = (TextView) findViewById(R.id.la);
        la.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ViewPagerActivity.class));
            }
        });*/

    }
}
