package com.intelequia.intelequiawebhooklauncher.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.intelequia.intelequiawebhooklauncher.MainActivity;
import com.intelequia.intelequiawebhooklauncher.R;
import com.intelequia.intelequiawebhooklauncher.WebhookActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alberto on 25/06/2016.
 */
public class WebhookAdapter extends ArrayAdapter<Webhook> {

    Context context;
    int layoutResourceId;
    Webhook data[] = null;

    public WebhookAdapter(Context context, int layoutResourceId, Webhook[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WebhookHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WebhookHolder();
            holder.number = (TextView)row.findViewById(R.id.numberText);
            holder.name = (TextView)row.findViewById(R.id.nameText);
            holder.button = (CircularProgressButton) row.findViewById(R.id.btnWithText);
            row.setTag(holder);
        }
        else
        {
            holder = (WebhookHolder)row.getTag();
        }

        Webhook web = data[position];
        holder.number.setText(String.valueOf(position+1));
        holder.name.setText(web.name);
        final String url = web.url;
        final CircularProgressButton button = holder.button;
        holder.button.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        launchWebhook(url,button);
                    }
                }
        );
        final String id = web.id;

        row.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context ,WebhookActivity.class);
                        intent.putExtra(MainActivity.ID, id);
                        ((MainActivity) context).finish();
                        context.startActivity(intent);
                    }
                }
        );
        return row;
    }

    static class WebhookHolder
    {
        TextView number;
        TextView name;
        CircularProgressButton button;
    }

    private void launchWebhook(String url, final CircularProgressButton urlButton){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                urlButton.setIndeterminateProgressMode(true);
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                urlButton.setProgress(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        urlButton.setProgress(0);
                    }
                }, 5000);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                urlButton.setProgress(-1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        urlButton.setProgress(0);
                    }
                }, 5000);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }
}