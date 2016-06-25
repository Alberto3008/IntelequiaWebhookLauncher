package com.intelequia.intelequiawebhooklauncher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.intelequia.intelequiawebhooklauncher.model.Webhook;
import com.intelequia.intelequiawebhooklauncher.model.WebhookAdapter;
import com.intelequia.intelequiawebhooklauncher.sqlite.DatabaseHelper;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public final static String ID = "com.intelequia.intelequiawebhooklauncher.ID";
    DatabaseHelper databaseHelper;
    ListView table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = DatabaseHelper.getInstance(this);
        table_layout = (ListView) findViewById(R.id.table);

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        String newKey = UUID.randomUUID().toString();

        String key = prefs.getString("key", newKey);

        if(key.equals(newKey)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key", key);
            editor.apply();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newWebHook();
            }
        });

        BuildTable();
    }

    public void newWebHook() {
        Intent intent = new Intent(this, WebhookActivity.class);
        intent.putExtra(ID,-1);
        finish();
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void BuildTable() {
        List<Webhook> webhooks = databaseHelper.getAllWebhooks();

        if(webhooks.size() < 1){
            newWebHook();
            return;
        }

/*        int i = 1;
        // outer for loop
        for (Webhook web: webhooks) {
            LinearLayout row = new LinearLayout(this);
            View v = LayoutInflater.from(this).inflate(R.layout.table_row, row,false);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            TextView numberText = (TextView) v.findViewById(R.id.textId);
            numberText.setText(String.valueOf(i));
            TextView nameText = (TextView) v.findViewById(R.id.NameId);
            nameText.setText(web.name);
            final CircularProgressButton urlButton = (CircularProgressButton) v.findViewById(R.id.btnWithText);
            urlButton.setText(getResources().getString(R.string.launch));
            i++;
            final String id = web.id;
            final MainActivity that = this;
            v.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(that ,WebhookActivity.class);
                            intent.putExtra(ID, id);
                            finish();
                            startActivity(intent);
                        }
                    }
            );
            final String url = web.url;
            urlButton.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                           launchWebhook(url, urlButton);
                        }
                    }
            );


            table_layout.addView(v);


        }*/

        Webhook[] webhookArray = webhooks.toArray(new Webhook[webhooks.size()]);

        WebhookAdapter adapter = new WebhookAdapter(this, R.layout.table_row, webhookArray);

 /*       View header = (View)getLayoutInflater().inflate(R.layout.header, null);
        table_layout.addHeaderView(header);*/

        table_layout.setAdapter(adapter);
    }

public static void editWebhook(String id){

}
}
