package com.justsimple.indexalgo;
import android.app.*;import android.content.Intent;import android.os.IBinder;
public final class AlgoService extends Service{
 public void onCreate(){super.onCreate();String id="hitesh_engine";NotificationManager n=getSystemService(NotificationManager.class);n.createNotificationChannel(new NotificationChannel(id,"Hitesh Algo Engine",NotificationManager.IMPORTANCE_LOW));PendingIntent p=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE);Notification x=new Notification.Builder(this,id).setContentTitle("Hitesh Algo • PAPER").setContentText("Engine active — real orders locked").setSmallIcon(android.R.drawable.stat_notify_sync).setOngoing(true).setContentIntent(p).build();startForeground(77,x);}
 public int onStartCommand(Intent i,int f,int s){return START_STICKY;}public IBinder onBind(Intent i){return null;}
}
