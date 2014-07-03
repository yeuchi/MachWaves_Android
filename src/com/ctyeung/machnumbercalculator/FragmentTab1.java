package com.ctyeung.machnumbercalculator;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ctyeung.machnumbercalculator.CameraPreview;
import com.ctyeung.machnumbercalculator.LevelLine;
import com.ctyeung.machnumbercalculator.R;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
 
public class FragmentTab1 extends Fragment {
    // View to display the camera output.
    private CameraPreview mPreview;

	Bitmap bmp;
	TextView txtResult;
	ImageView imageView;
	LevelLine levelLine;
	Canvas canvas;
    Paint paint;
	float downx = 0, downy = 0, upx = 0, upy = 0;
	int index = 0;
	PointF[] linePts = new PointF[4];
	float ratio = (float)(.8);
	float offsetX = -50;
	float offsetY = -100;
	
	protected float findAngle(){
		if(index==2){	// have 2 lines
			// vectors	
			float ax = linePts[0].x-linePts[1].x;
			float ay = linePts[0].y-linePts[1].y;
			
			float bx = linePts[2].x-linePts[3].x;
			float by = linePts[2].y-linePts[3].y;
			
			float radian = (float)Math.acos( (ax*bx + ay*by) / (Math.sqrt(ax*ax+ay*ay)*Math.sqrt(bx*bx+by*by)));
			float degrees = (float) (180 * radian / Math.PI);
			
			float MachNum = (float) (1.0 / Math.sin(radian/2));
   			txtResult.setText("Angle: "+String.valueOf(degrees)+ " Mach: " + String.valueOf(MachNum));
			return radian;
		}
		txtResult.setText("Angle: ");
		return -1;
	}
	
	protected void drawDivider(PointF p0, PointF p1){
		paint.setColor(Color.BLUE);
		canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
		imageView.invalidate();
	}
	
	protected void drawLine(PointF pt0, PointF pt1, int pos){
		pos -= 1;
		if(downx>-1){
			linePts[pos*2] = pt0; 
			linePts[pos*2+1] = pt1; 
		}
		
		canvas.drawBitmap(bmp, new Matrix(), null);
		
		for(int i=0; i<index; i++){
			canvas.drawLine(linePts[i*2].x, linePts[i*2].y, linePts[i*2+1].x, linePts[i*2+1].y, paint);
		}
        imageView.invalidate();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View rootView = inflater.inflate(R.layout.fragment1, container, false);
        Context context = rootView.getContext();
        
        txtResult = (TextView)rootView.findViewById(R.id.txtResult);
		
        // frame to hold camera preview
		mPreview = new CameraPreview(context);
		View view = rootView.findViewById(R.id.frame);
		if(null!=view) {
			FrameLayout frame = (FrameLayout)view;
			frame.addView(mPreview);
		

		// image to show photo
			imageView = (ImageView)rootView.findViewById(R.id.imageView1);
			imageView.setOnTouchListener(new View.OnTouchListener() {
					
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					if(bmp== null)
						return false;
					
					// TODO Auto-generated method stub
					int action = event.getAction();
				    switch (action) {
					    case MotionEvent.ACTION_DOWN:
					      downx = offsetX + (float)event.getX() *ratio;
					      downy = offsetY + (float)event.getY() *ratio;
					      break;
					    case MotionEvent.ACTION_MOVE:
					      break;
					    case MotionEvent.ACTION_UP:
					      upx = offsetX + (float)event.getX() *ratio;
					      upy = offsetY + (float)event.getY() *ratio;
					      
					      index = (index<2)?++index:1;
					      drawLine(new PointF(downx, downy), new PointF(upx, upy), index);
					      float radian = findAngle();
					      
					      // if there are exactly 2 lines.
					      if(radian != -1){
					    	  if(levelLine==null)
					    		  levelLine = new LevelLine(canvas);
					    	  
					    	  PointF p = levelLine.getIntersect(linePts[0], linePts[1], linePts[2], linePts[3]);
					    	  PointF p1 = levelLine.getDivider(linePts[0], linePts[1], linePts[2], linePts[3], p);
					    	//  drawDivider(p, p1);
					      }
					      break;
					    case MotionEvent.ACTION_CANCEL:
					      break;
					    default:
					      break;
				    }
				    return true;
				}
	
			});
				
			// capture image
			final Button button_capture = (Button)rootView.findViewById(R.id.button_capture);
			button_capture.bringToFront();
					
			button_capture.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					index = 0;
					findAngle();
					mPreview.capture(new Camera.PictureCallback() {
						
						@Override
						public void onPictureTaken(byte[] data, Camera camera) {
							//http://stackoverflow.com/questions/17022221/openfileoutput-how-to-create-files-outside-the-data-data-path
							FileOutputStream fos;
							
							try{
								File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
								DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
								String date = df.format(Calendar.getInstance().getTime());
								String path = folder.getAbsolutePath()+"/" + date + ".png";
								fos = new FileOutputStream(path);
								bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
								bmp.compress(CompressFormat.PNG, 100, fos);
								fos.close();
								
								Matrix matrix = new Matrix();
						        matrix.postRotate(90);
	
						        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
						        		bmp.getHeight(), matrix, true);
						        
						        Bitmap bmpSrc = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
						        		bmp.getHeight(), new Matrix(), true);
						        if(bmpSrc!=null) {
								    canvas = new Canvas(bmpSrc);
								    paint = new Paint();
								    paint.setColor(Color.GREEN);
								  }
								  imageView.setImageBitmap(bmpSrc);
								
							}catch(Exception e) {
								Log.e("Still", "error writing file", e);
							}
						}
					});
				}
			});
		
			// clear captured button
			final Button button_clear = (Button)rootView.findViewById(R.id.button_clear);
			button_clear.bringToFront();
			button_clear.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					imageView.setImageBitmap(null);
					index = 0;
					findAngle();
				}
			});
					
	        return rootView;
	    }
		

    return null;
	}
}