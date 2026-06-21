package com.purplebox;
import java.util.concurrent.*;
import javax.inject.Singleton;
import javax.sound.sampled.*;
@Singleton public class PurpleBoxSound{
 private final ExecutorService executor=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"purple-box-sound");t.setDaemon(true);return t;});
 void play(){executor.submit(this::jingle);}void close(){executor.shutdownNow();}
 private void jingle(){float rate=22050;AudioFormat f=new AudioFormat(rate,16,1,true,false);
  try(SourceDataLine line=AudioSystem.getSourceDataLine(f)){line.open(f,2048);line.start();double[]n={196,247,294,392,330,440,494,659};for(int i=0;i<n.length&&!Thread.currentThread().isInterrupted();i++)tone(line,rate,n[i],i==n.length-1?310:105);line.drain();}catch(Exception ignored){}
 }
 private static void tone(SourceDataLine l,float rate,double hz,int ms){int count=(int)(rate*ms/1000);byte[]d=new byte[count*2];for(int i=0;i<count;i++){double env=Math.min(1,i/180.0)*Math.min(1,(count-i)/240.0);double wave=Math.sin(2*Math.PI*hz*i/rate)+.24*Math.sin(4*Math.PI*hz*i/rate);short v=(short)(wave*env*.15*Short.MAX_VALUE);d[i*2]=(byte)v;d[i*2+1]=(byte)(v>>>8);}l.write(d,0,d.length);}
}
