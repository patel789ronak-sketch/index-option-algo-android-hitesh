package com.justsimple.indexalgo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String PREFS = "strategy_settings";
    private final Map<String, EditText> fields = new LinkedHashMap<>();
    private Switch strategyEnabled;
    private Switch paperTrading;
    private Switch overnight;
    private SharedPreferences prefs;

    private final int bg = Color.rgb(16, 24, 32);
    private final int panel = Color.rgb(27, 38, 49);
    private final int text = Color.rgb(238, 244, 247);
    private final int muted = Color.rgb(155, 169, 180);
    private final int green = Color.rgb(0, 200, 150);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        setContentView(buildScreen());
        loadSettings();
    }

    private View buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(bg);
        LinearLayout root = column();
        root.setPadding(dp(18), dp(24), dp(18), dp(28));
        scroll.addView(root);

        TextView brand = label("JUST SIMPLE • ALGO LAB", 12, green, true);
        brand.setLetterSpacing(.16f);
        root.addView(brand);
        root.addView(label("Index Option Algo", 28, text, true));
        TextView subtitle = label("NIFTY • OPTION BUYING • 5 MIN", 13, muted, false);
        subtitle.setPadding(0, dp(4), 0, dp(16));
        root.addView(subtitle);

        LinearLayout status = card();
        status.addView(label("TRIAL STRATEGY", 12, green, true));
        status.addView(label("ORB + VWAP + EMA + ADX", 18, text, true));
        status.addView(label("Forward Test & Paper Trading", 13, muted, false));
        root.addView(status);

        root.addView(section("MASTER CONTROL"));
        strategyEnabled = toggle("Strategy enabled", true);
        paperTrading = toggle("Paper trading only", true);
        overnight = toggle("Allow overnight positions", false);
        root.addView(strategyEnabled);
        root.addView(paperTrading);
        root.addView(overnight);

        root.addView(section("SESSION"));
        addField(root, "orb_start", "ORB start", "09:15", false);
        addField(root, "orb_end", "ORB end", "09:30", false);
        addField(root, "entry_start", "Entry start", "09:30", false);
        addField(root, "entry_end", "Entry cutoff", "15:00", false);
        addField(root, "force_exit", "Force exit", "15:15", false);

        root.addView(section("ENTRY FILTERS"));
        addField(root, "ema_fast", "Fast EMA", "20", true);
        addField(root, "ema_slow", "Slow EMA", "50", true);
        addField(root, "min_adx", "Minimum ADX", "20", true);
        addField(root, "timeframe", "Candle timeframe (minutes)", "5", true);

        root.addView(section("OPTION SELECTION"));
        addField(root, "strike_mode", "Strike selection", "ATM", false);
        addField(root, "expiry_mode", "Expiry", "Nearest valid expiry", false);
        addField(root, "min_volume", "Minimum volume", "1000", true);
        addField(root, "max_spread", "Maximum spread (%)", "1.0", true);

        root.addView(section("RISK CONTROL"));
        addField(root, "quantity", "Lots per trade", "1", true);
        addField(root, "stop_loss", "Stop loss (%)", "20", true);
        addField(root, "target", "Target (%)", "35", true);
        addField(root, "max_trades", "Maximum trades/day", "3", true);
        addField(root, "daily_loss", "Daily loss limit (₹)", "2000", true);

        Button save = button("SAVE STRATEGY", green, Color.rgb(8, 25, 22));
        save.setOnClickListener(v -> saveSettings());
        root.addView(save);

        Button reset = button("RESTORE DEFAULTS", panel, text);
        LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(-1, dp(52));
        resetParams.setMargins(0, dp(10), 0, 0);
        reset.setLayoutParams(resetParams);
        reset.setOnClickListener(v -> resetDefaults());
        root.addView(reset);

        TextView notice = label("Safety: This build is locked to paper/forward testing. It does not place live broker orders.", 12, muted, false);
        notice.setPadding(dp(8), dp(18), dp(8), 0);
        notice.setGravity(Gravity.CENTER);
        root.addView(notice);
        return scroll;
    }

    private void addField(LinearLayout root, String key, String title, String defaultValue, boolean numeric) {
        LinearLayout box = card();
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(-1, -2);
        boxParams.setMargins(0, 0, 0, dp(10));
        box.setLayoutParams(boxParams);
        box.setPadding(dp(15), dp(11), dp(15), dp(11));

        TextView caption = label(title, 12, muted, false);
        EditText input = new EditText(this);
        input.setText(defaultValue);
        input.setTextColor(text);
        input.setTextSize(17);
        input.setSingleLine(true);
        input.setSelectAllOnFocus(true);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setPadding(0, dp(3), 0, 0);
        input.setInputType(numeric
                ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_TEXT);
        box.addView(caption);
        box.addView(input, new LinearLayout.LayoutParams(-1, dp(46)));
        root.addView(box);
        input.setTag(defaultValue);
        fields.put(key, input);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("strategy_enabled", strategyEnabled.isChecked());
        editor.putBoolean("paper_trading", true);
        editor.putBoolean("overnight", overnight.isChecked());
        for (Map.Entry<String, EditText> item : fields.entrySet()) {
            editor.putString(item.getKey(), item.getValue().getText().toString().trim());
        }
        editor.apply();
        paperTrading.setChecked(true);
        Toast.makeText(this, "Strategy settings saved", Toast.LENGTH_SHORT).show();
    }

    private void loadSettings() {
        strategyEnabled.setChecked(prefs.getBoolean("strategy_enabled", true));
        paperTrading.setChecked(true);
        paperTrading.setEnabled(false);
        overnight.setChecked(prefs.getBoolean("overnight", false));
        for (Map.Entry<String, EditText> item : fields.entrySet()) {
            String fallback = String.valueOf(item.getValue().getTag());
            item.getValue().setText(prefs.getString(item.getKey(), fallback));
        }
    }

    private void resetDefaults() {
        prefs.edit().clear().apply();
        strategyEnabled.setChecked(true);
        paperTrading.setChecked(true);
        overnight.setChecked(false);
        for (EditText input : fields.values()) input.setText(String.valueOf(input.getTag()));
        Toast.makeText(this, "Default strategy restored", Toast.LENGTH_SHORT).show();
    }

    private LinearLayout column() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        return v;
    }

    private LinearLayout card() {
        LinearLayout v = column();
        v.setBackgroundColor(panel);
        v.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 0, 0, dp(14));
        v.setLayoutParams(p);
        return v;
    }

    private TextView section(String value) {
        TextView t = label(value, 12, green, true);
        t.setLetterSpacing(.12f);
        t.setPadding(dp(2), dp(12), 0, dp(10));
        return t;
    }

    private Switch toggle(String title, boolean checked) {
        Switch s = new Switch(this);
        s.setText(title);
        s.setTextColor(text);
        s.setTextSize(16);
        s.setChecked(checked);
        s.setPadding(dp(14), dp(8), dp(8), dp(8));
        s.setBackgroundColor(panel);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(58));
        p.setMargins(0, 0, 0, dp(8));
        s.setLayoutParams(p);
        return s;
    }

    private Button button(String title, int color, int textColor) {
        Button b = new Button(this);
        b.setText(title);
        b.setTextColor(textColor);
        b.setTextSize(14);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackgroundColor(color);
        b.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(54)));
        return b;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
