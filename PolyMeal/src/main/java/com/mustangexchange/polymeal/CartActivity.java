package com.mustangexchange.polymeal;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CartActivity extends Activity {

    private static TextView moneyView;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        moneyView = (TextView)findViewById(R.id.moneyView);
        moneyView.setText("$"+MoneyTime.calcTotalMoney());
        if(MoneyTime.calcTotalMoney()<0)
        {
            moneyView.setTextColor(Color.RED);
        }
        else
        {
            moneyView.setTextColor(Color.GREEN);
        }
        lv = (ListView)findViewById(R.id.listView);
        ListViewArrayAdapter adapter = new ListViewArrayAdapter(this,Cart.get());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> a, View v,int index, long id)
            {

            }

        });
    }
    public static void setTextMoney()
    {
        moneyView.setText("$"+MoneyTime.calcTotalMoney());
        if(MoneyTime.calcTotalMoney()<0)
        {
            moneyView.setTextColor(Color.RED);
        }
        else
        {
            moneyView.setTextColor(Color.GREEN);
        }
    }
    public void onResume()
    {
        super.onResume();
        moneyView.setText("$"+MoneyTime.calcTotalMoney());
        if(MoneyTime.calcTotalMoney()<0)
        {
            moneyView.setTextColor(Color.RED);
        }
        else
        {
            moneyView.setTextColor(Color.GREEN);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent home = new Intent(this, MainActivity.class);
                startActivity(home);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
