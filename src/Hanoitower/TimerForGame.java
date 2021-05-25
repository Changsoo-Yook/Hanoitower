package Hanoitower;

public class TimerForGame extends Thread {
	GameMain gm;
	public TimerForGame(GameMain gm) {
		this.gm = gm;
	}
	
	public void run() {
		while(true) {
		gm.playingTime++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}	
}