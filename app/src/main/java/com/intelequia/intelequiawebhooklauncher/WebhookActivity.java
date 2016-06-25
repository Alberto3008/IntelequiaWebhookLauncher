package com.intelequia.intelequiawebhooklauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.intelequia.intelequiawebhooklauncher.model.Webhook;
import com.intelequia.intelequiawebhooklauncher.sqlite.DatabaseHelper;
import com.rengwuxian.materialedittext.MaterialEditText;


public class WebhookActivity extends AppCompatActivity {
    MaterialEditText expireText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webhook);

        final MaterialEditText nameText = (MaterialEditText) findViewById(R.id.nameText);
        final MaterialEditText urlText = (MaterialEditText) findViewById(R.id.urlText);
        expireText = (MaterialEditText) findViewById(R.id.expireText);

        // Get singleton instance of database
        final DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        final String  webhookId = intent.getStringExtra(MainActivity.ID);

        Webhook webhook = new Webhook();
        if(webhookId != "-1"){
            webhook = databaseHelper.getWebhook(webhookId);
            nameText.setText(webhook.name);
            urlText.setText(webhook.url);
            expireText.setText(webhook.expire);
        }


        expireText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Webhook webhook = new Webhook();
                webhook.id = webhookId;
                webhook.name = nameText.getText().toString();
                webhook.url = urlText.getText().toString();
                webhook.expire = expireText.getText().toString();
                databaseHelper.addOrUpdateWebhook(webhook);
                onBackPressed();
            }
        });

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.deleteWebhook(webhookId);
                onBackPressed();
            }
        });


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }


}
