package com.purplebox;
import com.google.inject.Provides;
import java.util.*;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.*;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
@PluginDescriptor(name="Purple Box",description="A mystery-box roll for raid purples",tags={"raids","purple","loot"})
public class PurpleBoxPlugin extends Plugin{
 private static final Map<String,Integer> UNIQUES=uniques();
 @Inject private OverlayManager overlays;
 @Inject private ChatCommandManager commands;
 @Inject private PurpleBoxConfig config;
 @Inject private PurpleBoxOverlay overlay;
 @Inject private PurpleBoxSound sound;
 @Provides PurpleBoxConfig config(ConfigManager m){return m.getConfig(PurpleBoxConfig.class);}
 @Override protected void startUp(){overlays.add(overlay);commands.registerCommand("::purplebox",(m,s)->testRoll(),(i,s)->{testRoll();return true;});commands.registerCommand("!purplebox",(m,s)->testRoll(),(i,s)->{testRoll();return true;});}
 @Override protected void shutDown(){commands.unregisterCommand("::purplebox");commands.unregisterCommand("!purplebox");overlays.remove(overlay);overlay.stop();sound.close();}
 @Subscribe public void onChatMessage(ChatMessage e){
  if(e.getType()!=ChatMessageType.GAMEMESSAGE&&e.getType()!=ChatMessageType.SPAM)return;
  String msg=Text.removeTags(e.getMessage()).toLowerCase(Locale.ROOT);
  for(Map.Entry<String,Integer>x:UNIQUES.entrySet())if(msg.contains(x.getKey())){roll(x.getValue(),title(x.getKey()));return;}
  String extra=config.extraTrigger().trim().toLowerCase(Locale.ROOT);
  if(!extra.isEmpty()&&msg.contains(extra))roll(-1,"RAID UNIQUE");
 }
 private void testRoll(){List<Map.Entry<String,Integer>> x=new ArrayList<>(UNIQUES.entrySet());Collections.shuffle(x);roll(x.get(0).getValue(),title(x.get(0).getKey()));}
 private void roll(int id,String name){overlay.start(new ArrayList<>(UNIQUES.values()),id,name,config.duration());if(config.sound())sound.play();}
 private static String title(String s){StringBuilder b=new StringBuilder();for(String w:s.split(" ")){if(b.length()>0)b.append(' ');b.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));}return b.toString();}
 private static Map<String,Integer> uniques(){
  Map<String,Integer>i=new LinkedHashMap<>();
  i.put("twisted bow",20997);i.put("elder maul",21003);i.put("kodai insignia",21043);i.put("dragon claws",13652);
  i.put("dexterous prayer scroll",21034);i.put("arcane prayer scroll",21079);i.put("ancestral hat",21018);
  i.put("ancestral robe top",21021);i.put("ancestral robe bottom",21024);i.put("dinh's bulwark",21015);
  i.put("dragon hunter crossbow",21012);i.put("twisted buckler",21000);i.put("scythe of vitur",22325);
  i.put("ghrazi rapier",22324);i.put("sanguinesti staff",22481);i.put("avernic defender hilt",22477);
  i.put("justiciar faceguard",22326);i.put("justiciar chestguard",22327);i.put("justiciar legguards",22328);
  i.put("tumeken's shadow",27277);i.put("osmumten's fang",27246);i.put("lightbearer",25975);
  i.put("elidinis' ward",27251);i.put("masori mask",27226);i.put("masori body",27229);i.put("masori chaps",27232);
  return Collections.unmodifiableMap(i);
 }
}
