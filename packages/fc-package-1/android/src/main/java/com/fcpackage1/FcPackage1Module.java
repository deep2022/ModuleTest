// package com.fcpackage1;

// import androidx.annotation.NonNull;

// import com.facebook.react.bridge.Promise;
// import com.facebook.react.bridge.ReactApplicationContext;
// import com.facebook.react.bridge.ReactContextBaseJavaModule;
// import com.facebook.react.bridge.ReactMethod;
// import com.facebook.react.module.annotations.ReactModule;

// @ReactModule(name = FcPackage1Module.NAME)
// public class FcPackage1Module extends ReactContextBaseJavaModule {
//   public static final String NAME = "FcPackage1";

//   public FcPackage1Module(ReactApplicationContext reactContext) {
//     super(reactContext);
//   }

//   @Override
//   @NonNull
//   public String getName() {
//     return NAME;
//   }


//   // Example method
//   // See https://reactnative.dev/docs/native-modules-android
//   @ReactMethod
//   public void multiply(double a, double b, Promise promise) {
//     promise.resolve(a * b);
//   }
// }

package com.fcpackage1;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.google.android.material.snackbar.Snackbar;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = FcPackage1Module.NAME)
public class FcPackage1Module extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    public static final String NAME = "FcPackage1";
    private final String BROWSER_SAMSUNG = "com.sec.android.app.sbrowser";
    private final String BROWSER_CHROME = "com.android.chrome";

    public FcPackage1Module(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

    /**
     * This method will be called from js file, to check CHROME/SAMSUNG is installed and enable with the required version or not.
     *
     * @param promise
     */
    @ReactMethod
    public void isRequiredBrowserInstalled(Promise promise) {
        PackageManager pm = reactContext.getPackageManager();
        WritableMap map = Arguments.createMap();
        try {
            if (appInstalledOrNot(BROWSER_CHROME)) {
                int versionName = checkBrowserVersionName(BROWSER_CHROME);
                map.putInt("chrome", versionName);
            }
            if (appInstalledOrNot(BROWSER_SAMSUNG)) {
                int versionName = checkBrowserVersionName(BROWSER_SAMSUNG);
                map.putInt("samsung", versionName);
            }

            map.putBoolean("error", (!appInstalledOrNot(BROWSER_CHROME) || (checkBrowserVersionName(BROWSER_CHROME) <= 65)) && (!appInstalledOrNot(BROWSER_SAMSUNG) || (checkBrowserVersionName(BROWSER_SAMSUNG) <= 10)));
            promise.resolve(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    /**
     * This is a method to check whether the app is installed or not.
     *
     * @param packageName, Sending the Package Name.
     * @return
     */
    private boolean appInstalledOrNot(String packageName) {
        PackageManager pm = reactContext.getPackageManager();
        ApplicationInfo ai =
                null;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ai = reactContext.getPackageManager().getApplicationInfo(packageName, 0); // This code will check whether the app is enabled/disabled.
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This is a method to check the app's correct version is present or not.
     *
     * @param appName, Sending the Package Name.
     * @return
     */
    private int checkBrowserVersionName(String appName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = reactContext.getPackageManager().getPackageInfo(appName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageInfo != null) {
            String verName = packageInfo.versionName;
            String[] version = verName.split("\\.");
            return Integer.parseInt(version[0]);
        }
        return 0;
    }


    /**
     * This is a method which is invoked from the javascript file.
     * In this method we are checking the device has CHROME/SAMSUNG Browser
     * and the updated version of them is there or not.
     *
     * @param url, Sending the URL to open it in required browser.
     */
    @ReactMethod
    public void openUrl(String url, Promise promise) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (appInstalledOrNot(BROWSER_CHROME)) {             // Method to check Chrome is there or not.
            if (checkBrowserVersionName(BROWSER_CHROME) > 65) {   // Method to check required version of Chrome is there or not.
                i.setPackage(BROWSER_CHROME);                // Opening the URL in the Chrome.
                reactContext.startActivity(i);
                promise.resolve(true);
            }
//             else {
                // Opening the Snackbar, showing a message to update, and an option to open the play store to update.
//                 showSnackBar("Please update your Chrome", BROWSER_CHROME);
//             }
        } else if (appInstalledOrNot(BROWSER_SAMSUNG)) {     // Method to check Samsung Browser is there or not.
            if (checkBrowserVersionName(BROWSER_SAMSUNG) > 10) {  // Method to check required version of Samsung Browser is there or not.
                i.setPackage(BROWSER_SAMSUNG);               // Opening the URL in the Samsung Browser.
                reactContext.startActivity(i);
                promise.resolve(true);
            }
//             else {
                // Opening the Snackbar, showing a message to update, and an option to open the play store to update.
//                 showSnackBar("Please update your Samsung Browser", BROWSER_SAMSUNG);
//             }
        }
//         else {
//             showSnackBar("Please update your Chrome or Samsung Browser", BROWSER_CHROME);
//         }
        promise.resolve(false);
    }

    /**
     * This method will be invoked when the Updated Version of CHROME/SAMSUNG is not present.
     *
     * @param message,    A message will be shown to the user.
     * @param packageName Sending a package name, to update.
     */
    private void showSnackBar(String message, String packageName) {
        Activity activity = reactContext.getCurrentActivity();
        if (activity != null) {
          goToPlayStore(packageName);
        }
    }

    /**
     * This method will be invoked to update the CHROME/BROWSER, this is invoked from the SnackBar Action.
     *
     * @param packageName, Sending a package name to update from the play store.
     */
    public void goToPlayStore(String packageName) {
        String playStoreMarketUrl = "market://details?id=";
        String playStoreWebUrl = "https://play.google.com/store/apps/details?id=";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreMarketUrl + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            reactContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreWebUrl + packageName));
            reactContext.startActivity(intent);
        }
    }
}
