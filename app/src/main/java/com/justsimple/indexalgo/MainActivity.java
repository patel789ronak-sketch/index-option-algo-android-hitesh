package com.justsimple.indexalgo;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int BG=Color.rgb(13,20,27), PANEL=Color.rgb(25,36,46), PANEL2=Color.rgb(31,44,55);
    private static final int TEXT=Color.rgb(240,245,247), MUTED=Color.rgb(151,169,181), GREEN=Color.rgb(0,204,153), RED=Color.rgb(239,83,80), AMBER=Color.rgb(255,183,77);
    private LinearLayout content, nav; private TextView pageTitle, engineBadge; private SharedPreferences prefs;
    private final Button[] navButtons=new Button[5]; private int activeTab=0;

    @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("hitesh_algo",MODE_PRIVATE);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);View root=shell();setContentView(root);applySafeInsets(root);showDashboard();}

    private void applySafeInsets(View root){
        if(Build.VERSION.SDK_INT>=23)root.setOnApplyWindowInsetsListener((v,insets)->{v.setPadding(0,insets.getSystemWindowInsetTop(),0,insets.getSystemWindowInsetBottom());return insets.consumeSystemWindowInsets();});
        root.requestApplyInsets();
    }

    private View shell(){
        LinearLayout root=col(); root.setBackgroundColor(BG);
        LinearLayout top=row(); top.setGravity(Gravity.CENTER_VERTICAL); top.setPadding(dp(18),dp(14),dp(18),dp(12));
        LinearLayout names=col(); names.addView(txt("HITESH ALGO",20,GREEN,true)); pageTitle=txt("CONTROL CENTRE",11,MUTED,true); names.addView(pageTitle); top.addView(names,new LinearLayout.LayoutParams(0,-2,1));
        engineBadge=badge("● PAPER SAFE",GREEN); top.addView(engineBadge); root.addView(top);
        ScrollView scroll=new ScrollView(this); content=col(); content.setPadding(dp(16),dp(8),dp(16),dp(28)); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=row(); nav.setPadding(dp(4),dp(5),dp(4),dp(8)); nav.setBackgroundColor(Color.rgb(19,29,37));
        addNav(0,"⌂\nHOME",this::showDashboard);addNav(1,"▥\nMARKETS",this::showMarkets);addNav(2,"⚙\nSTRATEGY",this::showStrategy);addNav(3,"≡\nTRADES",this::showTrades);addNav(4,"●\nSETTINGS",this::showSettings);root.addView(nav,new LinearLayout.LayoutParams(-1,dp(76))); return root;
    }

    private void showDashboard(){selectTab(0);clear("CONTROL CENTRE");
        LinearLayout hero=card();hero.addView(txt("ALGO STATUS",11,GREEN,true));hero.addView(txt(prefs.getBoolean("running",false)?"Engine running":"Engine stopped",25,TEXT,true));hero.addView(txt("Mobile foreground execution • 5-minute close confirmation",12,MUTED,false));content.addView(hero);
        LinearLayout stats=row();stats.addView(stat("MODE","PAPER",GREEN),new LinearLayout.LayoutParams(0,dp(92),1));stats.addView(space(8));stats.addView(stat("OPEN","0",TEXT),new LinearLayout.LayoutParams(0,dp(92),1));stats.addView(space(8));stats.addView(stat("DAY P&L","₹0",TEXT),new LinearLayout.LayoutParams(0,dp(92),1));content.addView(stats);
        section("QUICK CONTROL");
        Button start=button("START PAPER ENGINE",GREEN,Color.rgb(5,30,24));start.setOnClickListener(v->startEngine());content.addView(start);
        Button stop=button("STOP ENGINE",PANEL2,TEXT);stop.setOnClickListener(v->stopEngine());content.addView(stop,marginTop(8));
        Button exit=button("EMERGENCY EXIT ALL",RED,Color.WHITE);exit.setOnClickListener(v->toast("No open position • paper mode"));content.addView(exit,marginTop(8));
        section("INDEX WATCH"); indexMini("NIFTY","Waiting for feed");indexMini("BANK NIFTY","Waiting for feed");indexMini("SENSEX","Waiting for feed");
        section("SAFETY"); content.addView(info("Live orders are locked. Connect Angel One, validate live candles and complete paper testing before enabling real-money mode.",AMBER));
    }

    private void showMarkets(){selectTab(1);clear("LIVE MARKETS"); content.addView(info("Prices will stream from Angel One SmartAPI after broker connection.",GREEN)); market("NIFTY 50","NSE","ORB Retest");market("BANK NIFTY","NFO","Dynamic ATR");market("SENSEX","BSE","Strict liquidity");
        section("SIGNAL PIPELINE");content.addView(step("1","Index ticks → 1m / 5m / 15m candles"));content.addView(step("2","Regime → ORB/VWAP/EMA/ADX/ATR"));content.addView(step("3","CE/PE score → liquidity → risk check"));content.addView(step("4","Paper fill → SL/target/trailing → exit"));
    }

    private void showStrategy(){selectTab(2);clear("STRATEGY EDITOR"); Spinner index=spinner(new String[]{"NIFTY","BANKNIFTY","SENSEX"});content.addView(index);LinearLayout editor=col();content.addView(editor);Runnable render=()->renderEditor(editor,index.getSelectedItem().toString());render.run();index.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){render.run();}});}

    private void renderEditor(LinearLayout box,String idx){box.removeAllViews();String key="cfg_"+idx;
        Switch enabled=toggle("Strategy enabled",prefs.getBoolean(key+"_enabled",true));box.addView(enabled);box.addView(caption("ACTIVE STRATEGY"));Spinner strategy=spinner(new String[]{"ORB_RETEST","VWAP_EMA_PULLBACK","PDH_PDL_BREAKOUT","COMPRESSION_BREAKOUT","GAP_CONTINUATION"});box.addView(strategy);
        EditText orbStart=field("ORB start",prefs.getString(key+"_orbStart","09:15"),false),orbEnd=field("ORB end",prefs.getString(key+"_orbEnd","09:30"),false),entryEnd=field("Last entry",prefs.getString(key+"_entryEnd","14:45"),false);
        EditText emaFast=field("EMA fast",prefs.getString(key+"_emaFast","20"),true),emaSlow=field("EMA slow",prefs.getString(key+"_emaSlow","50"),true),adx=field("Minimum ADX",prefs.getString(key+"_adx","20"),true),score=field("Minimum score",prefs.getString(key+"_score","85"),true);
        EditText risk=field("Risk per trade (%)",prefs.getString(key+"_risk",idx.equals("NIFTY")?"0.35":"0.25"),true),daily=field("Daily loss limit (%)",prefs.getString(key+"_daily","1.25"),true),spread=field("Maximum spread (%)",prefs.getString(key+"_spread","1.0"),true),trades=field("Maximum trades/day",prefs.getString(key+"_trades","3"),true);
        sectionInto(box,"SESSION");box.addView(orbStart);box.addView(orbEnd);box.addView(entryEnd);sectionInto(box,"INDICATORS & SCORE");box.addView(emaFast);box.addView(emaSlow);box.addView(adx);box.addView(score);sectionInto(box,"RISK & EXECUTION");box.addView(risk);box.addView(daily);box.addView(spread);box.addView(trades);
        Switch expiry=toggle("Expiry-day trading (default OFF)",prefs.getBoolean(key+"_expiry",false));box.addView(expiry);
        Button save=button("SAVE "+idx+" PROFILE",GREEN,Color.rgb(5,30,24));save.setOnClickListener(v->{prefs.edit().putBoolean(key+"_enabled",enabled.isChecked()).putString(key+"_orbStart",val(orbStart)).putString(key+"_orbEnd",val(orbEnd)).putString(key+"_entryEnd",val(entryEnd)).putString(key+"_emaFast",val(emaFast)).putString(key+"_emaSlow",val(emaSlow)).putString(key+"_adx",val(adx)).putString(key+"_score",val(score)).putString(key+"_risk",val(risk)).putString(key+"_daily",val(daily)).putString(key+"_spread",val(spread)).putString(key+"_trades",val(trades)).putBoolean(key+"_expiry",expiry.isChecked()).apply();toast(idx+" profile saved");});box.addView(save,marginTop(10));
        Button reset=button("RESTORE RECOMMENDED DEFAULTS",PANEL2,TEXT);reset.setOnClickListener(v->{prefs.edit().clear().apply();renderEditor(box,idx);toast("Defaults restored");});box.addView(reset,marginTop(8));
    }

    private void showTrades(){selectTab(3);clear("ORDERS & TRADES");LinearLayout stats=row();stats.addView(stat("TRADES","0",TEXT),new LinearLayout.LayoutParams(0,dp(92),1));stats.addView(space(8));stats.addView(stat("WIN RATE","—",TEXT),new LinearLayout.LayoutParams(0,dp(92),1));stats.addView(space(8));stats.addView(stat("P&L","₹0",TEXT),new LinearLayout.LayoutParams(0,dp(92),1));content.addView(stats);section("OPEN POSITION");content.addView(empty("No open position","Selected option, entry, stop, target and live P&L will appear here."));section("TODAY'S ACTIVITY");content.addView(empty("No trades yet","Paper orders and rejection reasons will be recorded automatically."));section("RISK STATUS");content.addView(info("0/3 trades • 0/2 consecutive losses • Daily loss ₹0",GREEN));}

    private void showSettings(){selectTab(4);clear("BROKER & APP SETTINGS");section("ANGEL ONE SMARTAPI");SecureStore secure=new SecureStore(this);EditText client=field("Client code",secure.get("client"),false),api=field("API key",secure.get("api"),false),pin=secret("Trading PIN"),totp=secret("TOTP secret");content.addView(client);content.addView(api);content.addView(pin);content.addView(totp);Button save=button("SAVE ENCRYPTED ON THIS DEVICE",GREEN,Color.rgb(5,30,24));save.setOnClickListener(v->{try{secure.put("client",val(client));secure.put("api",val(api));if(!val(pin).isEmpty())secure.put("pin",val(pin));if(!val(totp).isEmpty())secure.put("totp",val(totp));toast("Credentials encrypted and saved");}catch(Exception e){toast("Secure save failed");}});content.addView(save);Button test=button("TEST CONNECTION (NO ORDER)",PANEL2,TEXT);test.setOnClickListener(v->toast("Live connector pending validation"));content.addView(test,marginTop(8));section("TRADING MODE");Switch paper=toggle("Paper trading",true);paper.setEnabled(false);content.addView(paper);Switch live=toggle("Live trading (locked)",false);live.setEnabled(false);content.addView(live);section("MOBILE RELIABILITY");content.addView(info("Keep battery optimization OFF, allow background data and keep the engine notification enabled during market hours.",AMBER));section("APP");content.addView(kv("Version","4.0 Navigation Fix"));content.addView(kv("Order mode","Paper only"));content.addView(kv("Overnight","Blocked"));}

    private void startEngine(){if(android.os.Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},10);startForegroundService(new Intent(this,AlgoService.class));prefs.edit().putBoolean("running",true).apply();engineBadge.setText("● ENGINE ON");toast("Paper engine started");showDashboard();}
    private void stopEngine(){stopService(new Intent(this,AlgoService.class));prefs.edit().putBoolean("running",false).apply();engineBadge.setText("● PAPER SAFE");toast("Engine stopped");showDashboard();}
    private void market(String name,String exchange,String mode){LinearLayout c=card(),h=row();h.addView(txt(name,18,TEXT,true),new LinearLayout.LayoutParams(0,-2,1));h.addView(badge("OFFLINE",MUTED));c.addView(h);c.addView(txt(exchange+" • "+mode,12,MUTED,false));LinearLayout q=row();q.setPadding(0,dp(14),0,0);q.addView(kv("LTP","—"),new LinearLayout.LayoutParams(0,-2,1));q.addView(kv("SIGNAL","NO TRADE"),new LinearLayout.LayoutParams(0,-2,1));q.addView(kv("SCORE","—"),new LinearLayout.LayoutParams(0,-2,1));c.addView(q);content.addView(c);}
    private void indexMini(String n,String s){LinearLayout c=card(),r=row();r.setGravity(Gravity.CENTER_VERTICAL);r.addView(txt(n,16,TEXT,true),new LinearLayout.LayoutParams(0,-2,1));r.addView(txt(s,12,MUTED,false));c.addView(r);content.addView(c);}
    private LinearLayout stat(String a,String b,int color){LinearLayout c=card();c.setGravity(Gravity.CENTER);c.addView(txt(a,10,MUTED,true));c.addView(txt(b,16,color,true));return c;}
    private LinearLayout empty(String a,String b){LinearLayout c=card();c.setGravity(Gravity.CENTER);c.setPadding(dp(18),dp(30),dp(18),dp(30));c.addView(txt(a,17,TEXT,true));TextView d=txt(b,12,MUTED,false);d.setGravity(Gravity.CENTER);c.addView(d);return c;}
    private TextView info(String s,int color){TextView t=txt(s,13,color,false);t.setPadding(dp(15),dp(15),dp(15),dp(15));t.setBackgroundColor(PANEL);return t;}
    private LinearLayout step(String n,String s){LinearLayout r=row();r.setGravity(Gravity.CENTER_VERTICAL);r.addView(badge(n,GREEN));TextView t=txt(s,13,TEXT,false);t.setPadding(dp(12),dp(11),0,dp(11));r.addView(t,new LinearLayout.LayoutParams(0,-2,1));return r;}
    private LinearLayout kv(String k,String v){LinearLayout c=col();c.addView(txt(k,10,MUTED,true));c.addView(txt(v,13,TEXT,true));return c;}
    private void clear(String title){content.removeAllViews();pageTitle.setText(title);}private void addNav(int index,String label,Runnable action){Button b=button(label,Color.TRANSPARENT,MUTED);b.setTextSize(10);b.setGravity(Gravity.CENTER);b.setPadding(0,dp(5),0,dp(5));b.setOnClickListener(v->action.run());navButtons[index]=b;nav.addView(b,new LinearLayout.LayoutParams(0,-1,1));}
    private void selectTab(int index){activeTab=index;for(int i=0;i<navButtons.length;i++){Button b=navButtons[i];if(b!=null){b.setTextColor(i==index?GREEN:MUTED);b.setBackgroundColor(i==index?PANEL2:Color.TRANSPARENT);}}}
    private void section(String s){content.addView(sectionView(s));}private void sectionInto(LinearLayout b,String s){b.addView(sectionView(s));}private TextView sectionView(String s){TextView t=txt(s,11,GREEN,true);t.setLetterSpacing(.14f);t.setPadding(dp(2),dp(24),0,dp(10));return t;}
    private EditText field(String hint,String value,boolean num){EditText e=new EditText(this);e.setHint(hint);e.setHintTextColor(MUTED);e.setText(value);e.setTextColor(TEXT);e.setTextSize(16);e.setSingleLine();e.setSelectAllOnFocus(true);e.setPadding(dp(15),dp(12),dp(15),dp(12));e.setBackgroundColor(PANEL);if(num)e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58));p.setMargins(0,0,0,dp(8));e.setLayoutParams(p);return e;}
    private EditText secret(String hint){EditText e=field(hint,"",false);e.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);return e;}private Spinner spinner(String[] a){Spinner s=new Spinner(this);ArrayAdapter<String>d=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,a);s.setAdapter(d);s.setBackgroundColor(PANEL);s.setPadding(dp(12),0,dp(12),0);s.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(58)));return s;}
    private Switch toggle(String s,boolean checked){Switch v=new Switch(this);v.setText(s);v.setTextColor(TEXT);v.setTextSize(14);v.setChecked(checked);v.setPadding(dp(14),0,dp(12),0);v.setBackgroundColor(PANEL);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58));p.setMargins(0,0,0,dp(8));v.setLayoutParams(p);return v;}private TextView caption(String s){TextView t=txt(s,10,MUTED,true);t.setPadding(0,dp(12),0,dp(6));return t;}
    private LinearLayout card(){LinearLayout c=col();c.setPadding(dp(15),dp(15),dp(15),dp(15));c.setBackgroundColor(PANEL);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(10));c.setLayoutParams(p);return c;}private Button button(String s,int bg,int fg){Button b=new Button(this);b.setText(s);b.setTextColor(fg);b.setTextSize(12);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackgroundColor(bg);b.setAllCaps(false);return b;}
    private TextView badge(String s,int color){TextView t=txt(s,10,color,true);t.setPadding(dp(10),dp(7),dp(10),dp(7));t.setBackgroundColor(PANEL2);return t;}private TextView txt(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);return l;}private Space space(int w){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(dp(w),1));return s;}private LinearLayout.LayoutParams marginTop(int x){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));p.setMargins(0,dp(x),0,0);return p;}
    private String val(EditText e){return e.getText().toString().trim();}private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
