package com.goskincare.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomFragment;
import com.goskincare.custom.pickerview.OptionsPickerView;
import com.goskincare.custom.pickerview.TimePickerView;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class MagicOrderFragment extends CustomFragment
{
	TimePickerView pvTime;
	OptionsPickerView pvOptions;
	String[] mArrayPeriods;

	Context mContext;
	View mView;
	LayoutInflater mInflater;
	LinearLayout mLyContainer;
	TextView tvAddress, tvPay;

	ImageView mImgvPreview;
	ImageView mImgvCover;
	Animator mCurrentAnimator;
	int mShortAnimationDuration = 200;
	int nCurPos = 0;

	ArrayList<JSONObject> arrayListMagicOrders;

	private static final String DATE_NULL = "0000-00-00";

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_magic_order, null);
		mInflater = inflater;
		mContext = getActivity();

		setUI();
		setOptionsPickerView();
		setTimePickerView();
		loadCreditCards();

		GoSkinCareApplication.getInstance().trackingScreenView(Constant.GA_SCREENNAME_MAGIC_ORDER);

		return mView;
	}

	void setUI() {
		mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
		tvAddress = (TextView)mView.findViewById(R.id.tvAddress);
		tvPay = (TextView)mView.findViewById(R.id.tvPay);
		mImgvPreview = (ImageView)mView.findViewById(R.id.imgvPreview);
		mImgvCover = (ImageView)mView.findViewById(R.id.imgvCover);

		mView.findViewById(R.id.imgvMenu).setOnClickListener(this);
		mView.findViewById(R.id.imgvQuestion).setOnClickListener(this);
		mView.findViewById(R.id.btnSave).setOnClickListener(this);
		mView.findViewById(R.id.btnAddressEdit).setOnClickListener(this);
		mView.findViewById(R.id.btnPayEdit).setOnClickListener(this);
	}

	void loadCreditCards() {
		Common.getInstance().showProgressDialog(mContext, "Loading...");
		APIManager.getInstance().getCreditCardList(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				Common.getInstance().hideProgressDialog();

				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					APIManager.getInstance().arrayListCreditCards = new ArrayList<JSONObject>();

					for (int i = 0; i < jsonResponse.getJSONArray(Constant.key_cards).length(); i++) {
						JSONObject jsonCardInfo = jsonResponse.getJSONArray(Constant.key_cards).getJSONObject(i);

						if (jsonCardInfo.has(Constant.key_favourite) && jsonCardInfo.getInt(Constant.key_favourite) == 1) {
							APIManager.getInstance().nFavoriteCardIndex = i;
						}

						APIManager.getInstance().arrayListCreditCards.add(jsonCardInfo);
					}

					if (APIManager.getInstance().arrayListCreditCards.size() == 1)
						APIManager.getInstance().nFavoriteCardIndex = 0;

					loadMagicOrders();

				} catch (JSONException e) {
					e.printStackTrace();
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

	void loadMagicOrders() {
		Common.getInstance().showProgressDialog(mContext, "Loading...");
		APIManager.getInstance().getMagicOrder(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				Common.getInstance().hideProgressDialog();

				arrayListMagicOrders = new ArrayList<JSONObject>();
				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					JSONArray jsonArrayData = jsonResponse.getJSONArray(Constant.key_magicOrders);

					for (int i = 0; i < jsonArrayData.length(); i++) {
						JSONObject jsonMagicOrderInfo = jsonArrayData.getJSONObject(i);

						if (jsonMagicOrderInfo != null) {
							int nFrequencyIndex = jsonMagicOrderInfo.getInt(Constant.key_frequency);

							if (nFrequencyIndex < 0) nFrequencyIndex = 0;

							jsonMagicOrderInfo.put(Constant.key_frequency, nFrequencyIndex);

							arrayListMagicOrders.add(jsonMagicOrderInfo);
						}
					}

					presentData();

				} catch (JSONException e) {
					e.printStackTrace();
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

	public void refreshData() {
		tvAddress.setText(APIManager.getInstance().formattedAddress());

		if(APIManager.getInstance().arrayListCreditCards.size() > 0) {
			try {
				JSONObject jsonCardInfo = APIManager.getInstance().arrayListCreditCards.get(APIManager.getInstance().nFavoriteCardIndex);
				tvPay.setText(jsonCardInfo.getString(Constant.key_pan));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			tvPay.setText("");
		}
	}

	void setTimePickerView() {
		pvTime = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH_DAY);

		pvTime.setTime(new Date());
		pvTime.setCyclic(false);
		pvTime.setCancelable(true);
		pvTime.setCancelTitle("Cancel Order");

		pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

			@Override
			public void onTimeSelect(Date date) {
				JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(nCurPos);

				try {
					View viewItem = mLyContainer.getChildAt(nCurPos);
					((TextView) viewItem.findViewById(R.id.tvNextOrder)).setText("Next Order: " + Common.getInstance().stringFromDate(date, new SimpleDateFormat("dd MMMM")));

					jsonMagicOrderInfo.put(Constant.key_nextorderdate, Common.getInstance().stringFromDate(date, new SimpleDateFormat("yyyy-MM-dd")));

					arrayListMagicOrders.remove(nCurPos);
					arrayListMagicOrders.add(nCurPos, jsonMagicOrderInfo);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		pvTime.setOnTimeCancelListener(new TimePickerView.OnTimeCancelListener() {
			@Override
			public void onTimeCancel() {
				JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(nCurPos);

				try {
					View viewItem = mLyContainer.getChildAt(nCurPos);
					((TextView) viewItem.findViewById(R.id.tvNextOrder)).setText("Next Order: Never");

					jsonMagicOrderInfo.put(Constant.key_nextorderdate, DATE_NULL);

					arrayListMagicOrders.remove(nCurPos);
					arrayListMagicOrders.add(nCurPos, jsonMagicOrderInfo);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void setOptionsPickerView() {

		pvOptions = new OptionsPickerView(getActivity());

		mArrayPeriods = getResources().getStringArray(R.array.arr_periods);

		pvOptions.setPicker(new ArrayList<String>(Arrays.asList(mArrayPeriods)));

		pvOptions.setTitle("");
		pvOptions.setCancelTitle("");

		pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(nCurPos);

				try {
					View viewItem = mLyContainer.getChildAt(nCurPos);
					jsonMagicOrderInfo.put(Constant.key_frequency, options1);

					((TextView) viewItem.findViewById(R.id.tvPeriod)).setText(mArrayPeriods[options1]);

					String strNextOrder = jsonMagicOrderInfo.getString(Constant.key_nextorderdate);
					Date dateNextOrder = null;
					if (!strNextOrder.equals(DATE_NULL))
						dateNextOrder = Common.getInstance().dateFromString(strNextOrder, new SimpleDateFormat("yyyy-MM-dd"));

					if (dateNextOrder != null) return;

					jsonMagicOrderInfo.put(Constant.key_nextorderdate, Common.getInstance().stringFromDate(getDateOfNextDay(), new SimpleDateFormat("yyyy-MM-dd")));

					((TextView) viewItem.findViewById(R.id.tvNextOrder)).setText("Next Order: " + Common.getInstance().stringFromDate(getDateOfNextDay(), new SimpleDateFormat("dd MMMM")));

					arrayListMagicOrders.remove(nCurPos);
					arrayListMagicOrders.add(nCurPos, jsonMagicOrderInfo);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void presentData() {
		refreshData();

		mLyContainer.removeAllViews();

		for (int i = 0 ; i < arrayListMagicOrders.size(); i ++) {
			try {
				JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(i);
				View cell = mInflater.inflate(R.layout.cell_item_magic_order, null);

				((TextView)cell.findViewById(R.id.tvTitle)).setText(jsonMagicOrderInfo.getString(Constant.key_name));

				String strCurrencySymbol = "$";

				if(jsonMagicOrderInfo.has(Constant.key_priceCurrencySymbol)){
					strCurrencySymbol = jsonMagicOrderInfo.getString(Constant.key_priceCurrencySymbol);
				}

				double itemPrice = jsonMagicOrderInfo.getDouble(Constant.key_price);
				String formattedPrice = GoSkinCareApplication.getInstance().onFormattedPrice(itemPrice);

//				((TextView)cell.findViewById(R.id.tvItemPrice)).setText(strCurrencySymbol + jsonMagicOrderInfo.getInt(Constant.key_price));
				((TextView)cell.findViewById(R.id.tvItemPrice)).setText(String.format("= %s%s", strCurrencySymbol, formattedPrice));
				((TextView)cell.findViewById(R.id.tvNextOrder)).setText("Next Order: Never");

				int nFrequencyIndex = jsonMagicOrderInfo.getInt(Constant.key_frequency);
				if(nFrequencyIndex < 0) nFrequencyIndex = 0;
				if(nFrequencyIndex >= mArrayPeriods.length) nFrequencyIndex = mArrayPeriods.length - 1;

				String strFrequencey = mArrayPeriods[nFrequencyIndex];
				final TextView tvPeriod = (TextView)cell.findViewById(R.id.tvPeriod);
				tvPeriod.setText(strFrequencey);

				String strNextOrderDate = jsonMagicOrderInfo.getString(Constant.key_nextorderdate);
				Date dateNextOrder = null;

				if(!strNextOrderDate.equals(DATE_NULL)) dateNextOrder = Common.getInstance().dateFromString(strNextOrderDate, new SimpleDateFormat("yyyy-MM-dd"));

				if(dateNextOrder != null) {
					String strDate = Common.getInstance().stringFromDate(dateNextOrder, new SimpleDateFormat("dd MMMM"));

					if(strDate.length() < 1) strDate = "Never";

					((TextView)cell.findViewById(R.id.tvNextOrder)).setText("Next Order: " + strDate);
				}

				TextView tvEdit = ((TextView) cell.findViewById(R.id.tvEdit));
				tvEdit.setText(Html.fromHtml("<u>Edit</u>"));

				final ImageView imgvProduct = (ImageView)cell.findViewById(R.id.imgvProduct);
				imgvProduct.setImageResource(R.drawable.ic_launcher);

				final ImageView imgvSpinner = (ImageView)cell.findViewById(R.id.imgvSpinner);
				AnimationDrawable spinner = (AnimationDrawable) imgvSpinner.getBackground();
				spinner.start();

				imgvSpinner.setVisibility(View.INVISIBLE);

				String strImageUrl = jsonMagicOrderInfo.getString(Constant.key_imageUrlSmall);

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

				final int nPos = i;

				tvEdit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						nCurPos = nPos;
						JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(nPos);
						String strNextOrderDate = null;
						try {
							strNextOrderDate = jsonMagicOrderInfo.getString(Constant.key_nextorderdate);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Date dateNextOrder = null;

						if(strNextOrderDate!= null && !strNextOrderDate.equals(DATE_NULL)) dateNextOrder = Common.getInstance().dateFromString(strNextOrderDate, new SimpleDateFormat("yyyy-MM-dd"));

						if (dateNextOrder == null)
							pvTime.setTime(new Date());
						else
							pvTime.setTime(dateNextOrder);

						pvTime.show();
					}
				});

				cell.findViewById(R.id.rlySpinner).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						nCurPos = nPos;
						JSONObject jsonMagicOrderInfo = arrayListMagicOrders.get(nPos);
						int nFrequencyIndex = 0;

						try {
							nFrequencyIndex = jsonMagicOrderInfo.getInt(Constant.key_frequency);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if(nFrequencyIndex < 0) nFrequencyIndex = 0;
						if(nFrequencyIndex >= mArrayPeriods.length) nFrequencyIndex = mArrayPeriods.length - 1;

						pvOptions.setSelectOptions(nFrequencyIndex);
						pvOptions.show();
					}
				});


				mLyContainer.addView(cell);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void addMagicOrder() {
		final JSONObject jsonCardInfo = APIManager.getInstance().getFavoriteCreditCard();

		if(jsonCardInfo == null) {
			Common.getInstance().showAlert("GoSkinCare", "Before you save your magic order, we need you to store a credit card on your Goconuts profile.", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					CreditCardFragment f = new CreditCardFragment();
					String title = Constant.FRAGMENT_CREDIT_CARD;

					f.isPopUp = true;

					FragmentTransaction transaction = ((MainActivity) getActivity()).getSupportFragmentManager()
							.beginTransaction();

					transaction.setCustomAnimations(R.anim.abc_slide_in_bottom,
							R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom,
							R.anim.abc_slide_out_bottom);

					transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
				}
			});
			return;
		}

		try {
			String strCardId = jsonCardInfo.getString(Constant.key_cardid);

			final JSONArray jsonArrayOrderDetails = new JSONArray();
			for(JSONObject jsonProductInfo : arrayListMagicOrders) {
				String strDate = DATE_NULL;

				if(jsonProductInfo.has(Constant.key_nextorderdate) && jsonProductInfo.getString(Constant.key_nextorderdate).length() > 0){
					strDate = jsonProductInfo.getString(Constant.key_nextorderdate);
				}

				int nFrequency = 0;
				if(jsonProductInfo.has(Constant.key_frequency)) nFrequency = jsonProductInfo.getInt(Constant.key_frequency);

				JSONObject jsonObject = new JSONObject();

				jsonObject.put(Constant.key_sku, jsonProductInfo.getString(Constant.key_sku));
				jsonObject.put(Constant.key_nextorderdate, strDate);
				jsonObject.put(Constant.key_frequency, nFrequency);

				jsonArrayOrderDetails.put(jsonObject);
			}

			Common.getInstance().showProgressDialog(mContext, "Saving...");

			APIManager.getInstance().addMagicOrder(jsonArrayOrderDetails, strCardId, new APIManager.OnSuccessListener() {
				@Override
				public void onSuccess(String strJson) {
					Common.getInstance().hideProgressDialog();
					Common.getInstance().showAlert("GoSkinCare", strJson, mContext, new Common.OnOkListener() {
						@Override
						public void onOk() {
						}
					});
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

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	Date getDateOfNextDay() {
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();

		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();

		return tomorrow;
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
		if(v.getId() == R.id.imgvMenu) {
			((MainActivity)mContext).toggleMenu();
		}else if(v.getId() == R.id.imgvQuestion) {
			Fragment f = new MagicOrderHelpFragment();
			String title = Constant.FRAGMENT_MAGIC_ORDER_HELP;

			FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom);

			transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
		}else if(v.getId() == R.id.btnSave) {
			addMagicOrder();
		}else if (v.getId() == R.id.btnAddressEdit) {
			UpdateProfileFragment f = new UpdateProfileFragment();
			String title = Constant.FRAGMENT_UPDATE_PROFILE;

			f.isPopUp = true;

			FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom);

			transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();

		}else if (v.getId() == R.id.btnPayEdit) {
			Fragment f = new CreditCardFragment();
			String title = Constant.FRAGMENT_CREDIT_CARD;

			FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.left_in,
					R.anim.left_out, R.anim.right_in,
					R.anim.right_out);

			transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
		}
	}
}
