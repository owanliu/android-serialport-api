package android.serialport.sample;

import java.io.IOException;

import android.os.Bundle;
import android.widget.TextView;

public class LoopbackActivity extends SerialPortActivity {

	int mIncoming;
	int mOutgoing;
	SendingThread mSendingThread;
	TextView mTextViewOutgoing;
	TextView mTextViewIncoming;

	private class SendingThread extends Thread {
		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int i;
			for (i=0; i<buffer.length; i++) {
				buffer[i] = 0x55;
			}
			while(!isInterrupted()) {
				try {
					mOutputStream.write(buffer);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				mOutgoing += buffer.length;
				runOnUiThread(new Runnable() {
					public void run() {
						mTextViewOutgoing.setText(new Integer(mOutgoing).toString());
					}
				});
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loopback);
		mTextViewOutgoing = (TextView) findViewById(R.id.TextViewOutgoingValue);
		mTextViewIncoming = (TextView) findViewById(R.id.TextViewIncomingValue);
		mSendingThread = new SendingThread();
		mSendingThread.start();
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		mIncoming += size;
		runOnUiThread(new Runnable() {
			public void run() {
				mTextViewIncoming.setText(new Integer(mIncoming).toString());
			}
		});
	}

	@Override
	protected void onDestroy() {
		mSendingThread.interrupt();
		super.onDestroy();
	}
}