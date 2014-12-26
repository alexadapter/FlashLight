package lee.flashlight;

import android.app.Activity;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Editor: lee
 * Email: shuhejiang@163.com
 * Date: 2014/12/4
 */
public class MainActivity extends Activity {

    private boolean isOPen = false;
    private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOPen) {
                    openCamera();
                } else {
                    releaseCamera();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isOPen) {
            releaseCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!supportFlash()){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.unSupport),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!isOPen) {
            openCamera();
        }
    }

    private void releaseCamera(){
        if (camera != null) {
            //fix bug on nexus5 no flash
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }
        isOPen = false;
    }

    private void openCamera(){
        try {
            camera = Camera.open();
            //fix bug on nexus5 no flash
            camera.setPreviewTexture(new SurfaceTexture(0));
        }catch (Exception e){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.cameraInUse
            ), Toast.LENGTH_SHORT).show();
        }
        if(camera != null) {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isOPen = true;
        }
    }

    private boolean supportFlash(){
        boolean ret = false;
        PackageManager pm= this.getPackageManager();
        FeatureInfo[]  features=pm.getSystemAvailableFeatures();
        for(FeatureInfo f : features){
            if(PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)){   //判断设备是否支持闪光灯
                ret = true;
                break;
            }
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
