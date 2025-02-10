package com.example.asthafood.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class OpenLinks {

    private Context context;

    public OpenLinks(Context context){
        this.context = context;
    }

    public void openGeniusTechnology(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.geniustechnoindia.com/"));
        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(browserIntent);
        }
    }
}
