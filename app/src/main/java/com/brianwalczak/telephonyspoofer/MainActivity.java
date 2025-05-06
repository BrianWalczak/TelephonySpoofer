package com.brianwalczak.telephonyspoofer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "config"; // configs r stored here //

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

    // List of all the UI elements to update values
    private CheckBox cbFeatureTelephony;
    private CheckBox cbFeatureTelephonyGsm;
    private CheckBox cbFeatureTelephonyCdma;
    private Switch switchEsimEnabled;
    private Spinner spPhoneType;
    private EditText etPhoneCount;
    private CheckBox cbVoiceCapable;
    private CheckBox cbSmsCapable;
    private Spinner spNetworkType;
    private Spinner spDataNetworkType;
    private EditText etNetworkOperator;
    private EditText etNetworkOperatorName;
    private EditText etNetworkCountryIso;
    private CheckBox cbNetworkRoaming;
    private Spinner spSimState;
    private CheckBox cbHasIccCard;
    private EditText etSimOperator;
    private EditText etSimOperatorName;
    private EditText etSimCountryIso;
    private Button btnApply;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views to variables
        cbFeatureTelephony = findViewById(R.id.cb_feature_telephony);
        cbFeatureTelephonyGsm = findViewById(R.id.cb_feature_telephony_gsm);
        cbFeatureTelephonyCdma = findViewById(R.id.cb_feature_telephony_cdma);
        switchEsimEnabled = findViewById(R.id.switch_euicc_enabled);
        spPhoneType = findViewById(R.id.sp_phone_type);
        etPhoneCount = findViewById(R.id.et_phone_count);
        cbVoiceCapable = findViewById(R.id.cb_voice_capable);
        cbSmsCapable = findViewById(R.id.cb_sms_capable);
        spNetworkType = findViewById(R.id.sp_network_type);
        spDataNetworkType = findViewById(R.id.sp_data_network_type);
        etNetworkOperator = findViewById(R.id.et_network_operator);
        etNetworkOperatorName = findViewById(R.id.et_network_operator_name);
        etNetworkCountryIso = findViewById(R.id.et_network_country_iso);
        cbNetworkRoaming = findViewById(R.id.cb_network_roaming);
        spSimState = findViewById(R.id.sp_sim_state);
        cbHasIccCard = findViewById(R.id.cb_has_icc_card);
        etSimOperator = findViewById(R.id.et_sim_operator);
        etSimOperatorName = findViewById(R.id.et_sim_operator_name);
        etSimCountryIso = findViewById(R.id.et_sim_country_iso);
        btnApply = findViewById(R.id.btn_apply);
        btnReset = findViewById(R.id.btn_reset);

        // Load the preferences and services used
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS | Context.MODE_WORLD_READABLE);
        PackageManager pm = getPackageManager();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        EuiccManager euiccManager = (EuiccManager) getSystemService(Context.EUICC_SERVICE);

        // Find the system defaults for all the options
        final boolean defaultTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        final boolean defaultTelephonyGsm = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM);
        final boolean defaultTelephonyCdma = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA);
        final boolean defaultEsim = euiccManager != null && euiccManager.isEnabled();
        final int defaultPhoneType = tm.getPhoneType();
        final int defaultSimState = tm.getSimState();
        final int defaultNetworkType = tm.getNetworkType();
        final int defaultDataNetworkType = tm.getDataNetworkType();
        final boolean defaultHasIccCard = tm.hasIccCard();
        final boolean defaultNetworkRoaming = tm.isNetworkRoaming();
        final String defaultNetworkOperator = tm.getNetworkOperator();
        final String defaultNetworkOperatorName = tm.getNetworkOperatorName();
        final String defaultNetworkCountryIso = tm.getNetworkCountryIso();
        final String defaultSimOperator = tm.getSimOperator();
        final String defaultSimOperatorName = tm.getSimOperatorName();
        final String defaultSimCountryIso = tm.getSimCountryIso();
        final int defaultPhoneCount = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? tm.getPhoneCount() : 1;
        final boolean defaultVoiceCapable = tm.isVoiceCapable();
        final boolean defaultSmsCapable = tm.isSmsCapable();

        // Apply default values to elements (placeholder)
        cbFeatureTelephony.setChecked(prefs.getBoolean(PREF_FEATURE_TELEPHONY, defaultTelephony));
        cbFeatureTelephonyGsm.setChecked(prefs.getBoolean(PREF_FEATURE_TELEPHONY_GSM, defaultTelephonyGsm));
        cbFeatureTelephonyCdma.setChecked(prefs.getBoolean(PREF_FEATURE_TELEPHONY_CDMA, defaultTelephonyCdma));
        switchEsimEnabled.setChecked(prefs.getBoolean(PREF_ESIM_SUPPORTED, defaultEsim));
        spPhoneType.setSelection(prefs.getInt(PREF_PHONE_TYPE, defaultPhoneType));
        etPhoneCount.setText(String.valueOf(prefs.getInt(PREF_PHONE_COUNT, defaultPhoneCount)));
        cbVoiceCapable.setChecked(prefs.getBoolean(PREF_VOICE_CAPABLE, defaultVoiceCapable));
        cbSmsCapable.setChecked(prefs.getBoolean(PREF_SMS_CAPABLE, defaultSmsCapable));
        spNetworkType.setSelection(prefs.getInt(PREF_NETWORK_TYPE, defaultNetworkType));
        spDataNetworkType.setSelection(prefs.getInt(PREF_DATA_NETWORK_TYPE, defaultDataNetworkType));
        etNetworkOperator.setText(prefs.getString(PREF_NETWORK_OPERATOR, defaultNetworkOperator));
        etNetworkOperatorName.setText(prefs.getString(PREF_NETWORK_OPERATOR_NAME, defaultNetworkOperatorName));
        etNetworkCountryIso.setText(prefs.getString(PREF_NETWORK_COUNTRY_ISO, defaultNetworkCountryIso));
        cbNetworkRoaming.setChecked(prefs.getBoolean(PREF_NETWORK_ROAMING, defaultNetworkRoaming));
        spSimState.setSelection(prefs.getInt(PREF_SIM_STATE, defaultSimState));
        cbHasIccCard.setChecked(prefs.getBoolean(PREF_HAS_ICC_CARD, defaultHasIccCard));
        etSimOperator.setText(prefs.getString(PREF_SIM_OPERATOR, defaultSimOperator));
        etSimOperatorName.setText(prefs.getString(PREF_SIM_OPERATOR_NAME, defaultSimOperatorName));
        etSimCountryIso.setText(prefs.getString(PREF_SIM_COUNTRY_ISO, defaultSimCountryIso));

        // Apply changes button
        btnApply.setOnClickListener(v -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    // Open editor and write each preference

                    boolean valFeatureTelephony = cbFeatureTelephony.isChecked();
                    if (valFeatureTelephony != defaultTelephony)
                        editor.putBoolean(PREF_FEATURE_TELEPHONY, valFeatureTelephony);
                    else editor.remove(PREF_FEATURE_TELEPHONY);

                    boolean valFeatureTelephonyGsm = cbFeatureTelephonyGsm.isChecked();
                    if (valFeatureTelephonyGsm != defaultTelephonyGsm)
                        editor.putBoolean(PREF_FEATURE_TELEPHONY_GSM, valFeatureTelephonyGsm);
                    else editor.remove(PREF_FEATURE_TELEPHONY_GSM);

                    boolean valFeatureTelephonyCdma = cbFeatureTelephonyCdma.isChecked();
                    if (valFeatureTelephonyCdma != defaultTelephonyCdma)
                        editor.putBoolean(PREF_FEATURE_TELEPHONY_CDMA, valFeatureTelephonyCdma);
                    else editor.remove(PREF_FEATURE_TELEPHONY_CDMA);

                    boolean valEsim = switchEsimEnabled.isChecked();
                    if (valEsim != defaultEsim) editor.putBoolean(PREF_ESIM_SUPPORTED, valEsim);
                    else editor.remove(PREF_ESIM_SUPPORTED);

                    int valPhoneType = spPhoneType.getSelectedItemPosition();
                    if (valPhoneType != defaultPhoneType) editor.putInt(PREF_PHONE_TYPE, valPhoneType);
                    else editor.remove(PREF_PHONE_TYPE);

                    int phoneCount;
                    try {
                        phoneCount = Integer.parseInt(etPhoneCount.getText().toString());
                    } catch (NumberFormatException e) {
                        phoneCount = defaultPhoneCount;
                    }
                    if (phoneCount != defaultPhoneCount) editor.putInt(PREF_PHONE_COUNT, phoneCount);
                    else editor.remove(PREF_PHONE_COUNT);

                    boolean valVoiceCapable = cbVoiceCapable.isChecked();
                    if (valVoiceCapable != defaultVoiceCapable)
                        editor.putBoolean(PREF_VOICE_CAPABLE, valVoiceCapable);
                    else editor.remove(PREF_VOICE_CAPABLE);

                    boolean valSmsCapable = cbSmsCapable.isChecked();
                    if (valSmsCapable != defaultSmsCapable)
                        editor.putBoolean(PREF_SMS_CAPABLE, valSmsCapable);
                    else editor.remove(PREF_SMS_CAPABLE);

                    int valNetworkType = spNetworkType.getSelectedItemPosition();
                    if (valNetworkType != defaultNetworkType)
                        editor.putInt(PREF_NETWORK_TYPE, valNetworkType);
                    else editor.remove(PREF_NETWORK_TYPE);

                    int valDataNetworkType = spDataNetworkType.getSelectedItemPosition();
                    if (valDataNetworkType != defaultDataNetworkType)
                        editor.putInt(PREF_DATA_NETWORK_TYPE, valDataNetworkType);
                    else editor.remove(PREF_DATA_NETWORK_TYPE);

                    String valNetworkOperator = etNetworkOperator.getText().toString();
                    if (!valNetworkOperator.equals(defaultNetworkOperator))
                        editor.putString(PREF_NETWORK_OPERATOR, valNetworkOperator);
                    else editor.remove(PREF_NETWORK_OPERATOR);

                    String valNetworkOperatorName = etNetworkOperatorName.getText().toString();
                    if (!valNetworkOperatorName.equals(defaultNetworkOperatorName))
                        editor.putString(PREF_NETWORK_OPERATOR_NAME, valNetworkOperatorName);
                    else editor.remove(PREF_NETWORK_OPERATOR_NAME);

                    String valNetworkCountryIso = etNetworkCountryIso.getText().toString();
                    if (!valNetworkCountryIso.equals(defaultNetworkCountryIso))
                        editor.putString(PREF_NETWORK_COUNTRY_ISO, valNetworkCountryIso);
                    else editor.remove(PREF_NETWORK_COUNTRY_ISO);

                    boolean valNetworkRoaming = cbNetworkRoaming.isChecked();
                    if (valNetworkRoaming != defaultNetworkRoaming)
                        editor.putBoolean(PREF_NETWORK_ROAMING, valNetworkRoaming);
                    else editor.remove(PREF_NETWORK_ROAMING);

                    int valSimState = spSimState.getSelectedItemPosition();
                    if (valSimState != defaultSimState) editor.putInt(PREF_SIM_STATE, valSimState);
                    else editor.remove(PREF_SIM_STATE);

                    boolean valHasIccCard = cbHasIccCard.isChecked();
                    if (valHasIccCard != defaultHasIccCard)
                        editor.putBoolean(PREF_HAS_ICC_CARD, valHasIccCard);
                    else editor.remove(PREF_HAS_ICC_CARD);

                    String valSimOperator = etSimOperator.getText().toString();
                    if (!valSimOperator.equals(defaultSimOperator))
                        editor.putString(PREF_SIM_OPERATOR, valSimOperator);
                    else editor.remove(PREF_SIM_OPERATOR);

                    String valSimOperatorName = etSimOperatorName.getText().toString();
                    if (!valSimOperatorName.equals(defaultSimOperatorName))
                        editor.putString(PREF_SIM_OPERATOR_NAME, valSimOperatorName);
                    else editor.remove(PREF_SIM_OPERATOR_NAME);

                    String valSimCountryIso = etSimCountryIso.getText().toString();
                    if (!valSimCountryIso.equals(defaultSimCountryIso))
                        editor.putString(PREF_SIM_COUNTRY_ISO, valSimCountryIso);
                    else editor.remove(PREF_SIM_COUNTRY_ISO);

                    editor.apply();
                    File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/" + PREFS_NAME + ".xml");
                    prefsFile.setReadable(true, false);

                    Toast.makeText(MainActivity.this, "Your preferences have been saved.", Toast.LENGTH_SHORT).show();
        });

        // Reset defaults button
        btnReset.setOnClickListener(v -> {
            prefs.edit().clear().apply(); // Clear and delete preferences
            File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/" + PREFS_NAME + ".xml");
            if (prefsFile.exists()) prefsFile.delete(); // Delete preferences file (since empty)

            // Reapply system defaults to elements
            cbFeatureTelephony.setChecked(defaultTelephony);
            cbFeatureTelephonyGsm.setChecked(defaultTelephonyGsm);
            cbFeatureTelephonyCdma.setChecked(defaultTelephonyCdma);
            switchEsimEnabled.setChecked(defaultEsim);
            spPhoneType.setSelection(defaultPhoneType);
            etPhoneCount.setText(String.valueOf(defaultPhoneCount));
            cbVoiceCapable.setChecked(defaultVoiceCapable);
            cbSmsCapable.setChecked(defaultSmsCapable);
            spNetworkType.setSelection(defaultNetworkType);
            spDataNetworkType.setSelection(defaultDataNetworkType);
            etNetworkOperator.setText(defaultNetworkOperator);
            etNetworkOperatorName.setText(defaultNetworkOperatorName);
            etNetworkCountryIso.setText(defaultNetworkCountryIso);
            cbNetworkRoaming.setChecked(defaultNetworkRoaming);
            spSimState.setSelection(defaultSimState);
            cbHasIccCard.setChecked(defaultHasIccCard);
            etSimOperator.setText(defaultSimOperator);
            etSimOperatorName.setText(defaultSimOperatorName);
            etSimCountryIso.setText(defaultSimCountryIso);

            Toast.makeText(MainActivity.this, "Your defaults have been restored.", Toast.LENGTH_SHORT).show();
        });
    }
}