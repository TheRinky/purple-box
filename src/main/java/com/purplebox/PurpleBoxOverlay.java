package com.purplebox;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
public class PurpleBoxOverlay extends Overlay{
 private final ItemManager items;private List<Integer>pool=Collections.emptyList();private long start,duration;private int reward;private String name="";
 @Inject PurpleBoxOverlay(ItemManager i){items=i;setPosition(OverlayPosition.DYNAMIC);setLayer(OverlayLayer.ALWAYS_ON_TOP);}
 void start(List<Integer>p,int id,String n,int seconds){pool=new ArrayList<>(p);Collections.shuffle(pool);reward=id;name=n;duration=seconds*1_000_000_000L;start=System.nanoTime();}
 void stop(){start=0;}
 @Override public Dimension render(Graphics2D g){
  if(start==0||pool.isEmpty())return null;long elapsed=System.nanoTime()-start;
  if(elapsed>=duration+1_800_000_000L){stop();return null;}
  double progress=Math.min(1,elapsed/(double)duration);boolean reveal=progress>=1;
  int id=reveal&&reward>0?reward:pool.get(frame(elapsed,progress)%pool.size());
  Rectangle clip=g.getClipBounds();int cx=clip.width/2,cy=clip.height/2;
  float pulse=(float)((Math.sin(elapsed/120_000_000.0)+1)/2);
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  g.setComposite(AlphaComposite.SrcOver.derive(.42f));g.setColor(Color.BLACK);g.fillRoundRect(cx-190,cy-145,380,290,32,32);
  g.setComposite(AlphaComposite.SrcOver);
  for(int r=5;r>=1;r--){g.setColor(new Color(150,35,255,18+(int)(pulse*12)));g.setStroke(new BasicStroke(r*6f));g.drawRoundRect(cx-175,cy-130,350,260,28,28);}
  g.setStroke(new BasicStroke(3));g.setColor(reveal?new Color(245,210,90):new Color(195,95,255));g.drawRoundRect(cx-175,cy-130,350,260,28,28);
  BufferedImage image=items.getImage(id);if(image!=null)g.drawImage(image,cx-72,cy-76,144,144,null);
  g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,reveal?25:20));center(g,reveal?name:"?  ?  ?",cx,cy+105,reveal?new Color(255,225,110):Color.WHITE);
  g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));center(g,reveal?"PURPLE ACQUIRED":"MYSTERY BOX",cx,cy-100,new Color(215,145,255));return null;
 }
 private static int frame(long e,double p){return(int)(e/1_000_000.0/(55+390*p*p*p));}
 private static void center(Graphics2D g,String s,int x,int y,Color c){int w=g.getFontMetrics().stringWidth(s);g.setColor(new Color(0,0,0,210));g.drawString(s,x-w/2+2,y+2);g.setColor(c);g.drawString(s,x-w/2,y);}
}
