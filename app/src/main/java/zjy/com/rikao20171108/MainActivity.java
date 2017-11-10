package zjy.com.rikao20171108;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

public class MainActivity extends AppCompatActivity implements ProgressResponseBody.ProgressListener{

    @BindView(R.id.pb)
    ProgressBar pb;
    public static final String TAG = "MainActivity";
    public static final String PACKAGE_URL = "http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk";
    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.pb, R.id.start, R.id.pause, R.id.jx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start:
                breakPoints = 0L;
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"sample.apk");
                downloader = new ProgressDownloader(PACKAGE_URL,file,this);
                downloader.download(0L);
                break;
            case R.id.pause:
                downloader.pause();
                Toast.makeText(this,"下载暂停",Toast.LENGTH_SHORT).show();
                breakPoints = totalBytes;
                break;
            case R.id.jx:
                downloader.download(breakPoints);
                break;
        }
    }

    @Override
    public void onPreExecute(long contentLength) {
        if(this.contentLength == 0L){
            this.contentLength = contentLength;
            pb.setMax((int) (contentLength/1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        this.totalBytes = totalBytes+breakPoints;
        pb.setProgress((int) (totalBytes+breakPoints)/1024);
        if(done){
            Observable.empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                        }
                    }).subscribe();
        }
    }
}
