package com.example.claptrapper.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.claptrapper.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import io.paperdb.Paper;

public class ApplicationClass extends Application {


    public static InterstitialAd mInterstitialAd;


    //  public static AppOpenManager appOpenManager;
//    public static RewardedAd mRewardedAd;
    public static boolean mInterstitialAdShow = false;
//    public static NativeAd nativeAdInstance;
      public static AppOpenManager appOpenManager;


    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        appOpenManager = new AppOpenManager(this);



    }

    public static void showInterstitialAd(Activity activity) {

        mInterstitialAd.show(activity);
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("TAGAd", "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {

                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d("TAGAd", "Ad dismissed fullscreen content.");
                mInterstitialAd = null;

                loadInterstitialAd(activity);

            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e("TAGAd", "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("TAGAd", "Ad recorded an impression.");

            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("TAGAd", "Ad showed fullscreen content.");
            }
        });
    }

    public static void loadInterstitialAd(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, activity.getString(R.string.interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("TAGAd", loadAdError.toString());
                mInterstitialAd = null;

            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                Log.i("TAGAd", "onAdLoaded");
            }
        });
    }


    public static class InternetConnection {

        /**
         * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
         */
        public static boolean checkConnection(Context context) {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null) {
                NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                if (activeNetworkInfo != null) { // connected to the internet
                    // connected to the mobile provider's data plan
                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            }
            return false;
        }
    }

//    public static void loadRewardedAd(Activity activity) {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        RewardedAd.load(activity, activity.getString(R.string.reward_video_ad),
//                adRequest, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error.
//                        Log.d("TAG", loadAdError.toString());
//                        mRewardedAd = null;
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        mRewardedAd = rewardedAd;
//                        Log.d("TAG", "Ad was loaded.");
//
//
//                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                            @Override
//                            public void onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                                Log.d("TAG", "Ad was clicked.");
//                            }
//
//                            @Override
//                            public void onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                Log.d("TAG", "Ad dismissed fullscreen content.");
//                                mRewardedAd = null;
//                            }
//
//                            @Override
//                            public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                // Called when ad fails to show.
//                                Log.e("TAG", "Ad failed to show fullscreen content.");
//                                mRewardedAd = null;
//                            }
//
//                            @Override
//                            public void onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                                Log.d("TAG", "Ad recorded an impression.");
//                            }
//
//                            @Override
//                            public void onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                                Log.d("TAG", "Ad showed fullscreen content.");
//
//
//                            }
//                        });
//
//                    }
//                });
//    }
//
//    public static void loadNativeAd(Activity activity) {
//        if (nativeAdInstance == null) {
//            AdRequest adRequest = new AdRequest.Builder().build();
//
//            AdLoader adLoader = new AdLoader.Builder( activity,activity.getString(R.string.native_ad))
//                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                        @Override
//                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            nativeAdInstance = nativeAd;
//                            // handle ad loaded logic if needed
//                        }
//                    })
//                    .withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(LoadAdError loadAdError) {
//                            // This will print the error message to logcat
//                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
//                        }
//                    }).build();
//
//            adLoader.loadAd(adRequest);
//        }
//    }
//
//
//    public static NativeAd getLoadedNativeAd() {
//        return nativeAdInstance;
//    }
}
