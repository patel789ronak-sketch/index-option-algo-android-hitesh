package com.justsimple.indexalgo;
import android.content.*;import android.security.keystore.*;import android.util.Base64;import java.nio.charset.StandardCharsets;import java.security.KeyStore;import javax.crypto.*;import javax.crypto.spec.GCMParameterSpec;
public final class SecureStore{
 private static final String A="hitesh_algo_key";private final SharedPreferences p;public SecureStore(Context c){p=c.getSharedPreferences("hitesh_secure",Context.MODE_PRIVATE);}
 private SecretKey key()throws Exception{KeyStore k=KeyStore.getInstance("AndroidKeyStore");k.load(null);if(!k.containsAlias(A)){KeyGenerator g=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");g.init(new KeyGenParameterSpec.Builder(A,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());g.generateKey();}return(SecretKey)k.getKey(A,null);}
 public void put(String n,String v)throws Exception{Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.ENCRYPT_MODE,key());byte[]d=c.doFinal(v.getBytes(StandardCharsets.UTF_8));p.edit().putString(n+"i",Base64.encodeToString(c.getIV(),2)).putString(n+"d",Base64.encodeToString(d,2)).apply();}
 public String get(String n){try{byte[]i=Base64.decode(p.getString(n+"i",""),2),d=Base64.decode(p.getString(n+"d",""),2);Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,i));return new String(c.doFinal(d),StandardCharsets.UTF_8);}catch(Exception e){return"";}}
}
