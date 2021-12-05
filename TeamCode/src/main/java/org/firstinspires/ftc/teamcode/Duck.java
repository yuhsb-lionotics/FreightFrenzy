package org.firstinspires.ftc.teamcode;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.content.Context;

public class Duck
{
    AssetFileDescriptor afd = Context.getAssets().openFd("duckSound.wav");
    MediaPlayer player = new MediaPlayer();

    player.afd = afd;

    public void playDuck(){
        player.start();
    }
}
