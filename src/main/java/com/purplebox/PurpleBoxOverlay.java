package com.purplebox;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
public class PurpleBoxOverlay extends Overlay{
 private final Client client;private final ItemManager items;private List<Integer>pool=Collections.emptyList();private long start,duration;private int reward;private String name="";private Collection<GameObject>anchors=Collections.emptyList();
 @Inject PurpleBoxOverlay(Client c,ItemManager i){client=c;items=i;setPosition(OverlayPosition.DYNAMIC);setLayer(OverlayLayer.ALWAYS_ON_TOP);}
 void start(List<Integer>p,int id,String n,int seconds){pool=new ArrayList<>(p);Collections.shuffle(pool);reward=id;name=n;duration=seconds*1_000_000_000L;start=System.nanoTime();}
 void setAnchors(Collection<GameObject>a){anchors=a;}
 void stop(){start=0;}
 @Override public Dimension render(Graphics2D g){
  if(start==0||pool.isEmpty())return null;long elapsed=System.nanoTime()-start;
  if(elapsed>=duration+1_800_000_000L){stop();return null;}
  double progress=Math.min(1,elapsed/(double)duration);boolean reveal=progress>=1;
  int id=reveal&&reward>0?reward:pool.get(frame(elapsed,progress)%pool.size());
  Rectangle clip=g.getClipBounds();net.runelite.api.Point anchor=anchor();boolean world=anchor!=null;int cx=world?anchor.getX():clip.width/2,cy=world?anchor.getY():clip.height/2;double scale=world?.56:1;
  float pulse=(float)((Math.sin(elapsed/120_000_000.0)+1)/2);
  double open=Math.min(1,progress*1.45);
  int bob=(int)(Math.sin(elapsed/95_000_000.0)*7);
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  if(world){g=(Graphics2D)g.create();g.translate(cx,cy);g.scale(scale,scale);g.translate(-cx,-cy);}
  g.setComposite(AlphaComposite.SrcOver.derive(.42f));g.setColor(Color.BLACK);g.fillOval(cx-210,cy+76,420,54);
  g.setComposite(AlphaComposite.SrcOver);
  drawGlow(g,cx,cy,pulse,reveal);
  drawCrate(g,cx,cy,open,pulse,reveal);
  BufferedImage image=items.getImage(id);if(image!=null){int s=reveal?120:104;int iy=cy+8-(int)(open*96)+(reveal?0:bob);g.setComposite(AlphaComposite.SrcOver.derive((float)(.42+.58*open)));g.drawImage(image,cx-s/2,iy-s/2,s,s,null);g.setComposite(AlphaComposite.SrcOver);}
  g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,reveal?25:20));center(g,reveal?name:"?  ?  ?",cx,cy+126,reveal?new Color(255,225,110):Color.WHITE);
  g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));center(g,reveal?"PURPLE ACQUIRED":"RAID CRATE",cx,cy-118,new Color(215,145,255));if(world)g.dispose();return null;
 }
 private net.runelite.api.Point anchor(){GameObject best=null;for(GameObject o:anchors){if(o==null)continue;net.runelite.api.Point p=o.getCanvasLocation(95);if(p!=null&&(best==null||dist(o)<dist(best)))best=o;}return best==null?null:best.getCanvasLocation(95);}
 private int dist(GameObject o){Player p=client.getLocalPlayer();if(p==null)return Integer.MAX_VALUE;return p.getWorldLocation().distanceTo(o.getWorldLocation());}
 private static int frame(long e,double p){return(int)(e/1_000_000.0/(55+390*p*p*p));}
 private static void center(Graphics2D g,String s,int x,int y,Color c){int w=g.getFontMetrics().stringWidth(s);g.setColor(new Color(0,0,0,210));g.drawString(s,x-w/2+2,y+2);g.setColor(c);g.drawString(s,x-w/2,y);}
 private static void drawGlow(Graphics2D g,int cx,int cy,float pulse,boolean reveal){for(int r=7;r>=1;r--){g.setColor(new Color(reveal?245:150,reveal?205:35,reveal?75:255,18+(int)(pulse*12)));g.setStroke(new BasicStroke(r*7f));g.drawRoundRect(cx-178,cy-86,356,176,14,14);}}
 private static void drawCrate(Graphics2D g,int cx,int cy,double open,float pulse,boolean reveal){
  int x=cx-185,y=cy-72,w=370,h=154;
  g.setStroke(new BasicStroke(2));g.setColor(new Color(29,22,13,210));g.fillRoundRect(x-7,y+3,w+14,h+12,10,10);
  GradientPaint body=new GradientPaint(x,y+52,new Color(82,61,29),x,y+h,new Color(46,34,20));g.setPaint(body);g.fillRoundRect(x,y+45,w,h-44,8,8);
  int glow=(int)(60+90*open+35*pulse);g.setColor(new Color(reveal?245:175,reveal?210:70,255,Math.min(210,glow)));g.fillPolygon(new int[]{cx-112,cx+112,cx+58,cx-58},new int[]{y+70,y+70,y-54-(int)(open*38),y-54-(int)(open*38)},4);
  g.setStroke(new BasicStroke(2));g.setColor(new Color(230,185,255,80+(int)(90*open)));g.drawLine(cx-62,y+65,cx-34,y-44-(int)(open*42));g.drawLine(cx+62,y+65,cx+34,y-44-(int)(open*42));
  int lidLift=(int)(58*open),lidSkew=(int)(28*open);
  Polygon lidPoly=new Polygon(new int[]{x+lidSkew,x+w-lidSkew,x+w-4,x+4},new int[]{y-lidLift,y-lidLift,y+62-(int)(lidLift*.45),y+62-(int)(lidLift*.45)},4);
  GradientPaint lid=new GradientPaint(x,y-lidLift,new Color(105,83,37),x,y+58-lidLift,new Color(57,43,22));g.setPaint(lid);g.fillPolygon(lidPoly);
  g.setColor(new Color(29,22,13,170));g.drawPolygon(lidPoly);
  g.setColor(new Color(33,24,14,120));for(int i=0;i<9;i++)g.drawLine(x+12,y+24+i*15,x+w-12,y+18+i*14);
  g.setColor(new Color(132,104,48,150));for(int i=0;i<12;i++)g.drawLine(x+18+i*31,y+48,x+45+i*22,y+42);
  metal(g,x+22,y+22-(int)(lidLift*.55),34,128);metal(g,x+w/2-17,y+18-(int)(lidLift*.55),34,134);metal(g,x+w-56,y+22-(int)(lidLift*.55),34,128);
  g.setColor(new Color(48,39,28));g.fillRoundRect(cx-22,y+56,44,60,8,8);g.setColor(new Color(90,83,76));g.fillRoundRect(cx-14,y+66,28,42,6,6);g.setColor(new Color(28,25,23));g.fillRect(cx-4,y+88,8,14);
  g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,54));center(g,"?",cx-74,y+52,new Color(238,201,38));center(g,"?",cx+74,y+52,new Color(238,201,38));
  g.setStroke(new BasicStroke(3));g.setColor(new Color(25,20,14,190));g.drawRoundRect(x,y,w,h,10,10);g.setStroke(new BasicStroke(2));g.setColor(new Color(160,110,255,130));g.drawRoundRect(x+4,y+4,w-8,h-8,8,8);
 }
 private static void metal(Graphics2D g,int x,int y,int w,int h){g.setColor(new Color(39,39,38));g.fillRoundRect(x,y,w,h,8,8);g.setColor(new Color(105,105,100));g.fillRoundRect(x+5,y+7,w-10,h-14,6,6);g.setColor(new Color(165,165,155));g.fillRect(x+9,y+15,w-18,5);g.setColor(new Color(22,22,22,170));g.drawRoundRect(x,y,w,h,8,8);}
}
