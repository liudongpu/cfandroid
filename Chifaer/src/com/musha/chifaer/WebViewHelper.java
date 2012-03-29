package com.musha.chifaer;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ViewFlipper;

public class WebViewHelper extends WebView {
	
	private ViewFlipper flipper;
	public WebViewHelper(Context context, ViewFlipper flipper) {
		
		
		super(context);
		 this.flipper = flipper;
		    }

}
