package in.justsimple.indexalgo;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.google.android.material.card.MaterialCardView;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    LinearLayout root, content; android.content.SharedPreferences prefs;
    int green=Color.rgb(32,214,138), white=Color.rgb(245,247,250), muted=Color.rgb(155,170,189), card=Color.rgb(18,32,51);
    @Override protected void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.rgb(7,17,31));
        prefs=getSharedPreferences("settings",MODE_PRIVATE); buildShell(); showDashboard(); }
    TextView text(String s,float z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setPadding(8,8,8,8);return v;}
    void buildShell(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.rgb(7,17,31));root.setPadding(20,18,20,12);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);TextView title=text("INDEX ALGO  •  PAPER",20,white);title.setTypeface(null,1);bar.addView(title,new LinearLayout.LayoutParams(0,-2,1));TextView live=text("LIVE LOCKED",12,Color.rgb(255,93,108));bar.addView(live);root.addView(bar);
        content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);ScrollView sv=new ScrollView(this);sv.addView(content);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this);String[] ns={"Dashboard","Profiles","Broker","Risk"};for(String n:ns){Button x=new Button(this);x.setText(n);x.setTextSize(11);x.setOnClickListener(v->{if(n.equals("Dashboard"))showDashboard();else if(n.equals("Profiles"))showProfiles();else if(n.equals("Broker"))showBroker();else showRisk();});nav.addView(x,new LinearLayout.LayoutParams(0,-2,1));}root.addView(nav);setContentView(root);}
    MaterialCardView panel(String title,String body){MaterialCardView c=new MaterialCardView(this);c.setCardBackgroundColor(card);c.setRadius(22);c.setStrokeColor(Color.rgb(35,58,80));c.setStrokeWidth(1);c.setContentPadding(18,14,18,14);LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);TextView t=text(title,17,white);t.setTypeface(null,1);l.addView(t);l.addView(text(body,13,muted));c.addView(l);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,10,0,10);c.setLayoutParams(p);return c;}
    void clear(String h,String sub){content.removeAllViews();TextView t=text(h,25,white);t.setTypeface(null,1);content.addView(t);content.addView(text(sub,13,muted));}
    void showDashboard(){clear("Trading Dashboard","Safe forward-testing release • No real orders");content.addView(panel("MARKET STATUS","Waiting for Angel One connection\nData: OFFLINE  |  Last tick: —"));content.addView(panel("NIFTY","ORB Retest • ATM / 1 ITM\nSignal score —  |  Position: FLAT"));content.addView(panel("BANK NIFTY","Reduced risk profile\nSignal score —  |  Position: FLAT"));content.addView(panel("SENSEX","Strict liquidity profile\nSignal score —  |  Position: FLAT"));
        Button start=new Button(this);start.setText("START PAPER ALGO");start.setBackgroundColor(green);start.setOnClickListener(v->{if(android.os.Build.VERSION.SDK_INT>=33&&ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},7);ContextCompat.startForegroundService(this,new Intent(this,AlgoService.class));Toast.makeText(this,"Paper monitoring started",Toast.LENGTH_LONG).show();});content.addView(start);
        Button exit=new Button(this);exit.setText("EMERGENCY EXIT ALL / STOP");exit.setTextColor(Color.WHITE);exit.setBackgroundColor(Color.rgb(190,45,60));exit.setOnClickListener(v->{stopService(new Intent(this,AlgoService.class));Toast.makeText(this,"Service stopped; paper positions cleared",Toast.LENGTH_LONG).show();});content.addView(exit);}
    EditText field(String label,String value){TextView l=text(label,13,muted);content.addView(l);EditText e=new EditText(this);e.setText(value);e.setTextColor(white);e.setHintTextColor(muted);e.setBackgroundColor(card);e.setPadding(18,14,18,14);content.addView(e,new LinearLayout.LayoutParams(-1,-2));return e;}
    void showProfiles(){clear("Editable Profiles","Saved locally on this phone");String[] idx={"NIFTY","BANK NIFTY","SENSEX"};for(String s:idx){content.addView(panel(s,"Strategy ON • ORB 09:15–09:30 • Entry 09:30–15:00\nEMA 20/50 • ADX ≥ 20 • Score ≥ 85\nATM/1 ITM • Max 3 trades • Force exit 15:15"));}Button reset=new Button(this);reset.setText("RESET DEFAULT PRESETS");reset.setOnClickListener(v->{prefs.edit().clear().apply();Toast.makeText(this,"Defaults restored",Toast.LENGTH_SHORT).show();});content.addView(reset);}
    void showRisk(){clear("Risk Management","Conservative paper defaults");EditText risk=field("Risk per trade (%)",prefs.getString("risk","0.25"));EditText trades=field("Maximum trades",prefs.getString("trades","3"));EditText losses=field("Shutdown after consecutive losses",prefs.getString("losses","2"));content.addView(panel("GUARDRAILS","One open position • One lot • No averaging\nNo martingale • Expiry trading OFF • 15:15 force exit"));Button save=new Button(this);save.setText("SAVE RISK SETTINGS");save.setOnClickListener(v->{prefs.edit().putString("risk",risk.getText().toString()).putString("trades",trades.getText().toString()).putString("losses",losses.getText().toString()).apply();Toast.makeText(this,"Risk settings saved",Toast.LENGTH_SHORT).show();});content.addView(save);}
    void showBroker(){clear("Angel One Setup","Stored with Android Keystore encryption • never exported");try{MasterKey key=new MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();android.content.SharedPreferences sec=EncryptedSharedPreferences.create(this,"broker",key,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);EditText client=field("Client Code",sec.getString("client",""));EditText api=field("SmartAPI API Key",sec.getString("api",""));EditText pin=field("Trading PIN","");pin.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);EditText totp=field("TOTP Secret","");totp.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);content.addView(panel("SECURITY","Paper mode only in this release. Credentials are not included in logs, exports or screenshots generated by the app."));Button save=new Button(this);save.setText("SAVE ENCRYPTED");save.setOnClickListener(v->{sec.edit().putString("client",client.getText().toString().trim()).putString("api",api.getText().toString().trim()).putString("pin",pin.getText().toString()).putString("totp",totp.getText().toString().trim()).apply();pin.setText("");totp.setText("");Toast.makeText(this,"Encrypted broker setup saved",Toast.LENGTH_LONG).show();});content.addView(save);}catch(Exception e){content.addView(panel("SECURE STORAGE ERROR",e.getClass().getSimpleName()));}}
}
