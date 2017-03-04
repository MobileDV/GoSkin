package com.goskincare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomFragment;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class HelpFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_terms, null);

		setUI();

		GoSkinCareApplication.getInstance().trackingScreenView(Constant.GA_SCREENNAME_HELP);

		return mView;
	}

	void setUI() {
		mView.findViewById(R.id.imgvMenu).setOnClickListener(this);

		WebView webView = (WebView)mView.findViewById(R.id.webView);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		Common.getInstance().showProgressDialog(getActivity(), "Loading...");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Common.getInstance().hideProgressDialog();
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				Toast.makeText(getActivity(), "Loading Url failed", Toast.LENGTH_LONG).show();
			}
		});

		webView.loadUrl(Constant.HelpPageUrl);
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.imgvMenu){
			((MainActivity)getActivity()).toggleMenu();
		}
	}
}
