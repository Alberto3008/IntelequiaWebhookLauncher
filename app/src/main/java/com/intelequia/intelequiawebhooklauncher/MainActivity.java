package com.intelequia.intelequiawebhooklauncher;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.intelequia.intelequiawebhooklauncher.model.Webhook;
import com.intelequia.intelequiawebhooklauncher.sqlite.DatabaseHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;
import java.util.UUID;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public final static String ID = "com.intelequia.intelequiawebhooklauncher.ID";
    DatabaseHelper databaseHelper;
    TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = DatabaseHelper.getInstance(this);
        table_layout = (TableLayout) findViewById(R.id.table);

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

        int i = 1;
        // outer for loop
        for (Webhook web: webhooks) {
            TableRow row = new TableRow(this);
            View v = LayoutInflater.from(this).inflate(R.layout.tablerow, row,false);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            TextView numberText = (TextView) v.findViewById(R.id.textId);
            numberText.setText(String.valueOf(i));
            TextView nameText = (TextView) v.findViewById(R.id.NameId);
            nameText.setText(web.name);
            Button urlButton = (Button) v.findViewById(R.id.button);
            urlButton.setText("Ejecutar");
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
                           launchWebhook(url);
                        }
                    }
            );


            table_layout.addView(v);

        }

    }

private void launchWebhook(String url){
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url, new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            Toast toast = Toast.makeText(getApplicationContext(),"EXITO", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            Toast toast = Toast.makeText(getApplicationContext(),"ERRRORR", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
        }
    });

}
}
