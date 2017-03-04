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
import com.goskincare.custom.CustomFragment;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class MagicOrderHelpFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_magic_order_help, null);

		setUI();

		return mView;
	}

	void setUI() {
		mView.findViewById(R.id.lytBack).setOnClickListener(this);

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

		webView.loadUrl(Constant.MagicOrderHelpPageUrl);
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.lytBack){
			((MainActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
		}
	}
}
