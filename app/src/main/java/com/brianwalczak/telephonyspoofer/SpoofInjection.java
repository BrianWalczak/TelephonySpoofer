package com.brianwalczak.telephonyspoofer;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;

import android.telephony.euicc.DownloadableSubscription;
import android.app.PendingIntent;

public class SpoofInjection implements IXposedHookLoadPackage {
    private static final String MODULE_PKG = "com.brianwalczak.telephonyspoofer";
    private static final String PREFS_NAME = "config";

    // List of modifiable values in preferences
    private static final String PREF_FEATURE_TELEPHONY = "featureTelephony";
    private static final String PREF_FEATURE_TELEPHONY_GSM = "featureTelephonyGsm";
    private static final String PREF_FEATURE_TELEPHONY_CDMA = "featureTelephonyCdma";
    private static final String PREF_ESIM_SUPPORTED = "esimSupported";
    private static final String PREF_PHONE_TYPE = "phoneType";
    private static final String PREF_PHONE_COUNT = "phoneCount";
    private static final String PREF_VOICE_CAPABLE = "voiceCapable";
    private static final String PREF_SMS_CAPABLE = "smsCapable";
    private static final String PREF_NETWORK_TYPE = "networkType";
    private static final String PREF_DATA_NETWORK_TYPE = "dataNetworkType";
    private static final String PREF_NETWORK_OPERATOR = "networkOperator";
    private static final String PREF_NETWORK_OPERATOR_NAME = "networkOperatorName";
    private static final String PREF_NETWORK_COUNTRY_ISO = "networkCountryIso";
    private static final String PREF_NETWORK_ROAMING = "networkRoaming";
    private static final String PREF_SIM_STATE = "simState";
    private static final String PREF_HAS_ICC_CARD = "hasIccCard";
    private static final String PREF_SIM_OPERATOR = "simOperator";
    private static final String PREF_SIM_OPERATOR_NAME = "simOperatorName";
    private static final String PREF_SIM_COUNTRY_ISO = "simCountryIso";

    private static void hookBool(XSharedPreferences prefs, Class<?> clazz, String method, String key) {
        XposedHelpers.findAndHookMethod(
                clazz,
                method,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        prefs.reload();
                        if (prefs == null || !prefs.contains(key)) return;

                        boolean spoof = prefs.getBoolean(key, false);
                        XposedBridge.log("[SpoofInjection] Spoofing " + key + " to -> " + spoof);
                        param.setResult(spoof);
                    }
                }
        );
    }

    private static void hookString(XSharedPreferences prefs, Class<?> clazz, String method, String key) {
        XposedHelpers.findAndHookMethod(
                clazz,
                method,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        prefs.reload();
                        if (prefs == null || !prefs.contains(key)) return;

                        String spoof = prefs.getString(key, null);
                        if(spoof == null || spoof.isEmpty()) return;

                        XposedBridge.log("[SpoofInjection] Spoofing " + key + " to -> " + spoof);
                        param.setResult(spoof);
                    }
                }
        );
    }

    private static void hookInt(XSharedPreferences prefs, Class<?> clazz, String method, String key) {
        XposedHelpers.findAndHookMethod(
                clazz,
                method,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        prefs.reload();
                        if (prefs == null || !prefs.contains(key)) return;

                        int spoof = prefs.getInt(key, Integer.MIN_VALUE);
                        if (spoof == Integer.MIN_VALUE) return;

                        XposedBridge.log("[SpoofInjection] Spoofing " + key + " to -> " + spoof);
                        param.setResult(spoof);
                    }
                }
        );
    }

    private void hookPackageManagerFeatures(final XSharedPreferences prefs, ClassLoader cl) {
        Class<?> apmClass = XposedHelpers.findClass(
                "android.app.ApplicationPackageManager",
                cl
        );

        // Spoof individual hasSystemFeature(...) calls
        XposedHelpers.findAndHookMethod(
                apmClass,
                "hasSystemFeature",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        prefs.reload();
                        String feature = (String) param.args[0];

                        // Only override if the pref is present
                        if ("android.hardware.telephony".equals(feature)
                                && prefs.contains(PREF_FEATURE_TELEPHONY)) {
                            param.setResult(prefs.getBoolean(PREF_FEATURE_TELEPHONY, false));

                        } else if ("android.hardware.telephony.gsm".equals(feature)
                                && prefs.contains(PREF_FEATURE_TELEPHONY_GSM)) {
                            param.setResult(prefs.getBoolean(PREF_FEATURE_TELEPHONY_GSM, false));

                        } else if ("android.hardware.telephony.cdma".equals(feature)
                                && prefs.contains(PREF_FEATURE_TELEPHONY_CDMA)) {
                            param.setResult(prefs.getBoolean(PREF_FEATURE_TELEPHONY_CDMA, false));
                        }
                    }
                }
        );
    }

    private void hookESIMDownload(final XSharedPreferences prefs, String IS_ENABLED) {
        XposedHelpers.findAndHookMethod(
                EuiccManager.class,
                "downloadSubscription",
                DownloadableSubscription.class,
                boolean.class,                  // switchAfterDownload
                PendingIntent.class,            // callback intent
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        prefs.reload();

                        // only intercept when we’re spoofing esim
                        if (prefs.getBoolean(IS_ENABLED, false)) {
                            DownloadableSubscription sub = (DownloadableSubscription) param.args[0];
                            String code = sub.getEncodedActivationCode(); // raw activation string

                            XposedBridge.log("[SpoofInjection] eSIM activation code → " + code);
                        }
                    }
                }
        );
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences prefs = new XSharedPreferences(MODULE_PKG, PREFS_NAME);

        hookBool(prefs, EuiccManager.class, "isEnabled", PREF_ESIM_SUPPORTED);
        hookBool(prefs, TelephonyManager.class, "isSmsCapable", PREF_SMS_CAPABLE);
        hookBool(prefs, TelephonyManager.class, "isNetworkRoaming", PREF_NETWORK_ROAMING);
        hookBool(prefs, TelephonyManager.class, "hasIccCard", PREF_HAS_ICC_CARD);
        hookBool(prefs, TelephonyManager.class, "isVoiceCapable", PREF_VOICE_CAPABLE);

        hookString(prefs, TelephonyManager.class, "getNetworkOperator", PREF_NETWORK_OPERATOR);
        hookString(prefs, TelephonyManager.class, "getNetworkOperatorName", PREF_NETWORK_OPERATOR_NAME);
        hookString(prefs, TelephonyManager.class, "getNetworkCountryIso", PREF_NETWORK_COUNTRY_ISO);
        hookString(prefs, TelephonyManager.class, "getSimOperator", PREF_SIM_OPERATOR);
        hookString(prefs, TelephonyManager.class, "getSimOperatorName", PREF_SIM_OPERATOR_NAME);
        hookString(prefs, TelephonyManager.class, "getSimCountryIso", PREF_SIM_COUNTRY_ISO);

        hookInt(prefs, TelephonyManager.class, "getPhoneType", PREF_PHONE_TYPE);
        hookInt(prefs, TelephonyManager.class, "getPhoneCount", PREF_PHONE_COUNT);
        hookInt(prefs, TelephonyManager.class, "getNetworkType", PREF_NETWORK_TYPE);
        hookInt(prefs, TelephonyManager.class, "getDataNetworkType", PREF_DATA_NETWORK_TYPE);
        hookInt(prefs, TelephonyManager.class, "getSimState", PREF_SIM_STATE);

        // add all the ApplicationPackageManager ones
        hookPackageManagerFeatures(prefs, lpparam.classLoader);

        // hook onto eSIM download information
        hookESIMDownload(prefs, PREF_ESIM_SUPPORTED);
    }
}
