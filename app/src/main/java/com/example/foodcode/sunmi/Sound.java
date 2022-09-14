package com.example.foodcode.sunmi;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;


@SuppressLint("UseSparseArrays")
public class Sound {

	/**
	 * TODO 上下文
	 */
	private Context context;

	/**
	 * TODO 声音池
	 */
	private SoundPool soundPool;

	/**
	 * TODO 添加的声音资源参数
	 */
	private HashMap<Integer, Integer> soundPoolMap;

	/**
	 * TODO 无限循环播放
	 */
	public static final int INFINITE_PLAY = -1;

	/**
	 * TODO 单次播放
	 */
	public static final int SINGLE_PLAY = 0;

	/**
	 * TODO 铃声音量
	 */
	public static final int RING_SOUND = 2;

	/**
	 * TODO 媒体音量
	 */
	public static final int MEDIA_SOUND = 3;

	/**
	 *
	 * TODO 构造器内初始化
	 *
	 * @author kingsway
	 * @date 2017-10-12
	 * @param context
	 *            上下文
	 */
	public Sound(Context context) {

		this.context = context;

		// 初始化声音池和声音参数map
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();

	}

	/**
	 *
	 * TODO 添加声音文件进声音池
	 *
	 * @author kingsway
	 * @date 2017-10-12
	 * @param order
	 *            所添加声音的编号，播放的时候指定
	 * @param soundRes
	 *            添加声音资源的id
	 * @see
	 */
	public void addSound(int order, int soundRes) {

		// 上下文，声音资源id，优先级
		soundPoolMap.put(order, soundPool.load(context, soundRes, 1));

	}

	/**
	 *
	 * TODO 播放声音
	 *
	 * @author kingsway
	 * @date 2017-10-12
	 * @param order
	 *            所添加声音的编号
	 * @param mode
	 *            循环次数，0无不循环，-1无永远循环
	 * @see
	 */
	public void playSound(int order, int mode, int type) {

		// 实例化AudioManager对象
		AudioManager am = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

		// 返回当前AudioManager对象播放所选声音的类型的最大音量值
		float maxVolumn = am.getStreamMaxVolume(type);

		// 返回当前AudioManager对象的音量值
		float currentVolumn = am.getStreamVolume(type);

		// 比值
		float volumnRatio = currentVolumn / maxVolumn;
		soundPool.play(soundPoolMap.get(order), volumnRatio, volumnRatio, 1, mode, 1);

	}

	/**
	 *
	 * TODO 直接播放媒体声音
	 *
	 * @author kingsway
	 * @date 2017-10-12
	 * @param soundRes
	 *            播放的声音资源id
	 * @see
	 */
	public void playsound(int soundRes) {

		MediaPlayer mediaPlayer = MediaPlayer.create(context, soundRes);
		mediaPlayer.start();

		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.reset();
				mp.release();
			}
		});

	}

}