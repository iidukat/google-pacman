package jp.or.iidukat.example.pacman;


@SuppressWarnings("serial")
public class SoundPlayerException extends Exception {

	public SoundPlayerException() {
	}
	
	public SoundPlayerException(String detailMessage) {
		super(detailMessage);
	}

	public SoundPlayerException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SoundPlayerException(Throwable throwable) {
		super(throwable);
	}

}
