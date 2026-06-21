package com.purplebox;
import java.io.*;
import java.util.concurrent.*;
import javax.inject.Singleton;
import net.runelite.client.audio.AudioPlayer;
@Singleton public class PurpleBoxSound{
 private final AudioPlayer player;
 private final ExecutorService executor=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"purple-box-sound");t.setDaemon(true);return t;});
 @javax.inject.Inject PurpleBoxSound(AudioPlayer p){player=p;}
 void play(){executor.submit(this::jingle);}void close(){executor.shutdownNow();}
 private void jingle(){
  try{player.play(new ByteArrayInputStream(wav()),-9f);}catch(Exception ignored){}
 }
 private static byte[] wav()throws IOException{int rate=22050;double[]notes={196,247,294,392,330,440,494,659};ByteArrayOutputStream pcm=new ByteArrayOutputStream();for(int i=0;i<notes.length;i++)tone(pcm,rate,notes[i],i==notes.length-1?310:105);byte[]data=pcm.toByteArray();ByteArrayOutputStream out=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(out);d.writeBytes("RIFF");le(d,36+data.length);d.writeBytes("WAVEfmt ");le(d,16);les(d,1);les(d,1);le(d,rate);le(d,rate*2);les(d,2);les(d,16);d.writeBytes("data");le(d,data.length);d.write(data);return out.toByteArray();}
 private static void tone(ByteArrayOutputStream out,int rate,double hz,int ms){int count=rate*ms/1000;for(int i=0;i<count;i++){double env=Math.min(1,i/180.0)*Math.min(1,(count-i)/240.0);double wave=Math.sin(2*Math.PI*hz*i/rate)+.24*Math.sin(4*Math.PI*hz*i/rate);short v=(short)(wave*env*.15*Short.MAX_VALUE);out.write(v&255);out.write((v>>>8)&255);}}
 private static void le(DataOutputStream d,int v)throws IOException{d.writeByte(v);d.writeByte(v>>>8);d.writeByte(v>>>16);d.writeByte(v>>>24);}
 private static void les(DataOutputStream d,int v)throws IOException{d.writeByte(v);d.writeByte(v>>>8);}
}
