package com.purplebox;
import net.runelite.client.config.*;
@ConfigGroup("purplebox")
public interface PurpleBoxConfig extends Config{
 @ConfigItem(keyName="sound",name="Play sound",description="Play an original mystery-box-style jingle")
 default boolean sound(){return true;}
 @Range(min=3,max=12)
 @ConfigItem(keyName="duration",name="Animation length",description="Length of the roll in seconds")
 default int duration(){return 6;}
 @ConfigItem(keyName="extraTrigger",name="Extra trigger text",description="Optional text which also starts a roll")
 default String extraTrigger(){return "";}
}
