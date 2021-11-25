package org.firstinspires.ftc.teamcode;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.content.Context;

public class Duck
{
    AssetFileDescriptor afd = Context.getAssets().openFd("duckSound.wav");
    MediaPlayer player = new MediaPlayer();

    public void setAfd(MediaPlayer player) {
        this.afd = afd;
    }

    public void playDuck(){
        player.start();
    }
}
