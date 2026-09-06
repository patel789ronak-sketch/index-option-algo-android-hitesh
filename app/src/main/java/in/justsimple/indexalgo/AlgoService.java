package in.justsimple.indexalgo;

import android.app.*;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class AlgoService extends Service {
    private PowerManager.WakeLock wakeLock;
    @Override public void onCreate() {
        super.onCreate();
        NotificationChannel c = new NotificationChannel("algo", "Algo status", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(c);
        Notification n = new NotificationCompat.Builder(this,"algo").setContentTitle("Paper Algo Running")
            .setContentText("Monitoring NIFTY, BANK NIFTY and SENSEX").setSmallIcon(android.R.drawable.ic_popup_sync).setOngoing(true).build();
        startForeground(101,n);
        PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"IndexAlgo:MarketWake"); wakeLock.acquire(8*60*60*1000L);
    }
    @Override public int onStartCommand(Intent i,int f,int id){ return START_STICKY; }
    @Override public void onDestroy(){ if(wakeLock!=null&&wakeLock.isHeld())wakeLock.release(); super.onDestroy(); }
    @Override public android.os.IBinder onBind(Intent i){ return null; }
}
