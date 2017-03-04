package com.goskincare.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.Preference.UserPreference;
import com.goskincare.R;
import com.goskincare.activity.LoginActivity;
import com.goskincare.activity.MainActivity;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class FastOrderFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	MainActivity mContext;
	LinearLayout mLyContainer;
	LayoutInflater mInflater;
	View mView;
	ImageView mImgvPreview;
	ImageView mImgvCover;
	Animator mCurrentAnimator;
	int mShortAnimationDuration = 200;

	ArrayList<JSONObject> arrListProducts;

	private final int LOGIN_REQUEST = 3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_fast_order, null);
		mContext = (MainActivity)getActivity();
		mInflater = inflater;

		setUI();
		loadProducts();

		GoSkinCareApplication.getInstance().trackingScreenView(Constant.GA_SCREENNAME_FAST_ORDER);

		return mView;
	}

	void setUI() {
		mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainerFastOrder);
		mImgvPreview = (ImageView)mView.findViewById(R.id.imgvPreview);
		mImgvCover = (ImageView)mView.findViewById(R.id.imgvCover);
		((Button)mView.findViewById(R.id.btnNext)).setOnClickListener(this);

		mImgvCover.setVisibility(View.INVISIBLE);
		mImgvPreview.setVisibility(View.GONE);

		mView.findViewById(R.id.btnNext).setOnClickListener(this);
		mView.findViewById(R.id.imgvMenu).setOnClickListener(this);
	}

	void loadProducts() {
		Common.getInstance().showProgressDialog(mContext, "Loading...");

		APIManager.getInstance().getProductList(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				try {
					Common.getInstance().hideProgressDialog();
					JSONObject jsonResponse = new JSONObject(strJson);
					JSONArray jsonArrayData = jsonResponse.getJSONArray(Constant.key_products);

					arrListProducts = new ArrayList<JSONObject>();

					for (int i = 0; i < jsonArrayData.length(); i++) {
						JSONObject jsonObject = jsonArrayData.getJSONObject(i);
						jsonObject.put(Constant.key_amount, 0);

						arrListProducts.add(jsonObject);
					}

					presentData();
				} catch (JSONException e) {
					e.printStackTrace();
					Common.getInstance().showAlert("Error", "country data is not correct", mContext, new Common.OnOkListener() {
						@Override
						public void onOk() {

						}
					});
				}
			}
		}, new APIManager.OnFailListener() {
			@Override
			public void onFail(String strErr) {
				Common.getInstance().hideProgressDialog();
				Common.getInstance().showAlert("GoSkinCare", strErr, mContext, new Common.OnOkListener() {
					@Override
					public void onOk() {

					}
				});
			}
		});
	}

	void presentData() {
		mLyContainer.removeAllViews();

		for (int i = 0 ; i < arrListProducts.size(); i ++) {
			try {
				final JSONObject jsonObject = arrListProducts.get(i);
				View cell = mInflater.inflate(R.layout.cell_item_fast_order, null);

				final double itemPrice = jsonObject.getDouble(Constant.key_price);

				String formattedItemPrice = GoSkinCareApplication.getInstance().onFormattedPrice(itemPrice);

				((TextView)cell.findViewById(R.id.tvTitle)).setText(jsonObject.getString(Constant.key_productName));
//				((TextView)cell.findViewById(R.id.tvItemPrice)).setText(jsonObject.getString(Constant.key_priceCurrencySymbol) + itemPrice);
				((TextView)cell.findViewById(R.id.tvItemPrice)).setText(String.format("%s%s", jsonObject.getString(Constant.key_priceCurrencySymbol), formattedItemPrice));
				((TextView)cell.findViewById(R.id.tvDescription)).setText(jsonObject.getString(Constant.key_detail));

				final int pos = i;
				final TextView tvNum = (TextView)cell.findViewById(R.id.tvNum);
				final TextView tvTotalPrice = (TextView)cell.findViewById(R.id.tvTotalPrice);

				tvNum.setText("0");
				tvTotalPrice.setText("= $0.00");

				final ImageView imgvProduct = (ImageView)cell.findViewById(R.id.imgvProduct);
				imgvProduct.setImageResource(R.drawable.ic_launcher);

				final ImageView imgvSpinner = (ImageView)cell.findViewById(R.id.imgvSpinner);
				AnimationDrawable spinner = (AnimationDrawable) imgvSpinner.getBackground();
				spinner.start();

				imgvSpinner.setVisibility(View.INVISIBLE);

				String strImageUrl = jsonObject.getString(Constant.key_imageUrlSmall);

				if(strImageUrl.length() > 0) {
					ImageLoader.getInstance().loadImage(strImageUrl, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							imgvSpinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							imgvSpinner.setVisibility(View.INVISIBLE);
							imgvProduct.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									zoomImageFromThumb(v, null);
								}
							});
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
							imgvProduct.setImageBitmap(loadedImage);
							imgvSpinner.setVisibility(View.INVISIBLE);
							imgvProduct.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									zoomImageFromThumb(v, loadedImage);
								}
							});
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {
						}
					});
				}

				cell.findViewById(R.id.imgvMinus).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int num = Integer.valueOf(tvNum.getText().toString());

						if (num > 0) {
							num--;
							double nTotalPrice =(double)(num * itemPrice);

							String nFormattedTotalPrice = GoSkinCareApplication.getInstance().onFormattedPrice(nTotalPrice);

//							tvTotalPrice.setText("= $" + nFormattedTotalPrice);
							tvTotalPrice.setText(String.format("= $%s", nFormattedTotalPrice));

							tvNum.setText(num + "");

							try {
								jsonObject.put(Constant.key_amount, num);
								arrListProducts.remove(pos);
								arrListProducts.add(pos, jsonObject);
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				});

				cell.findViewById(R.id.imgvPlus).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int num = Integer.valueOf(tvNum.getText().toString());

						num++;
						double nTotalPrice = (double) (num * itemPrice);

						String nFormattedTotalPrice = GoSkinCareApplication.getInstance().onFormattedPrice(nTotalPrice);

//						tvTotalPrice.setText("= $" + nTotalPrice);
						tvTotalPrice.setText(String.format("= $%s", nFormattedTotalPrice));
						tvNum.setText(num + "");

						try {
							jsonObject.put(Constant.key_amount, num);
							arrListProducts.remove(pos);
							arrListProducts.add(pos, jsonObject);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

				mLyContainer.addView(cell);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void zoomImageFromThumb(final View thumbView, Bitmap bmp) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
//		mImgvPreview.setImageResource(imageResId);
		if(bmp == null) {
			mImgvPreview.setImageResource(R.drawable.ic_launcher);
		}else{
			mImgvPreview.setImageBitmap(bmp);
		}

		mImgvPreview.setVisibility(View.VISIBLE);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		mView.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);

		startBounds.offset(0, 0);
		finalBounds.offset(startBounds.left, 0);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height()
				> (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		mImgvPreview.setVisibility(View.VISIBLE);
		mImgvCover.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		mImgvPreview.setPivotX(0f);
		mImgvPreview.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set
				.play(ObjectAnimator.ofFloat(mImgvPreview, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(mImgvPreview, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(mImgvPreview, View.SCALE_X,
						startScale, 1f)).with(ObjectAnimator.ofFloat(mImgvPreview,
				View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		mImgvPreview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator
						.ofFloat(mImgvPreview, View.X, startBounds.left))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.Y,startBounds.top))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						mImgvPreview.setVisibility(View.GONE);
						mCurrentAnimator = null;
						mImgvCover.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						mImgvPreview.setVisibility(View.GONE);
						mCurrentAnimator = null;
						mImgvCover.setVisibility(View.INVISIBLE);
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});

		mImgvCover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator
						.ofFloat(mImgvPreview, View.X, startBounds.left))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.Y,startBounds.top))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator
								.ofFloat(mImgvPreview,
										View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						mImgvPreview.setVisibility(View.GONE);
						mCurrentAnimator = null;
						mImgvCover.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						mImgvPreview.setVisibility(View.GONE);
						mCurrentAnimator = null;
						mImgvCover.setVisibility(View.INVISIBLE);
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btnNext) {
			onNext();
		}

		if(v.getId() == R.id.imgvMenu) {
			mContext.toggleMenu();
		}
	}

	void onNext() {
		boolean isMemberLogin = UserPreference.getInstance().getSharedPreference(Constant.gsc_user_is_guest_login, true);

		if(isMemberLogin) {
			Intent intent = new Intent(getActivity(), LoginActivity.class).putExtra(Constant.isPopUp, true);
			startActivityForResult(intent, LOGIN_REQUEST);
			getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
			return;
		}

		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < arrListProducts.size(); i ++) {
			JSONObject jsonObject = arrListProducts.get(i);

			try {
				int nAmount = jsonObject.getInt(Constant.key_amount);

				if(nAmount > 0) jsonArray.put(jsonObject);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if(jsonArray.length() > 0) {
			OrderDetailFragment f = new OrderDetailFragment();
			f.jsonArrayData = jsonArray;
			String title = Constant.FRAGMENT_ORDER_DETAIL;

			FragmentTransaction transaction = mContext.getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.left_in,
					R.anim.left_out, R.anim.right_in,
					R.anim.right_out);

			transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
		} else {
			Common.getInstance().showAlert("Error", "Please select at least one product first!", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {

				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == LOGIN_REQUEST){
				boolean isLoginSuccess = data.getBooleanExtra(Constant.key_success, false);
				if(isLoginSuccess) onNext();
			}
		}
	}
}
