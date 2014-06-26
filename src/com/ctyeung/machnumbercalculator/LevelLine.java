package com.ctyeung.machnumbercalculator;

import android.graphics.PointF;
import android.graphics.Canvas;

public class LevelLine {

	int DIRECTION_UPDOWN = 0;
	int DIRECTION_LEFT = 1;
	int DIRECTION_RIGHT = 2;
	protected int canvasWidth;
	protected int canvasHeight;
	
	public LevelLine(Canvas canvas){
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
	}
	
	public PointF getIntersect(	PointF line1V1, PointF line1V2,
			  					PointF line2V1, PointF line2V2){
		
		PointF i = new PointF(-1,-1);
		//Line1
        float A1 = line1V2.y - line1V1.y;
        float B1 = line1V1.x - line1V2.x;
        float C1 = A1*line1V1.x + B1*line1V1.y;

        //Line2
        float A2 = line2V2.y - line2V1.y;
        float B2 = line2V1.x - line2V2.x;
        float C2 = A2 * line2V1.x + B2 * line2V1.y;

        float det = A1*B2 - A2*B1;
        if (det == 0)
        {
            return i;//parallel lines
        }
        else
        {
            i.x = (B2*C1 - B1*C2)/det;
            i.y = (A1 * C2 - A2 * C1) / det;
        }
        return i;
	}
	
	protected float getSlope(PointF p, 
							 PointF c)
	{
		float m = (p.y-c.y) / (p.x-c.x);
		return m;
	}
	
	protected PointF getPointOnLine(PointF p0,
									float m,
									float d,
									int direction){
		PointF p1 = new PointF();
		if(direction == DIRECTION_LEFT) {
			p1.x = canvasWidth;
		}
		else{
			p1.x = 0;
		}
		p1.y = (p1.x - p0.x)*m + p0.y;
		return p1;
	}
	
	public PointF getDivider(PointF line1V1, 
							PointF line1V2,
							PointF line2V1, 
							PointF line2V2,
		 					PointF center){
		float d = 0;
		int index = -1;
		PointF[] pts = new PointF[4];
		pts[0] = line1V1;
		pts[1] = line1V2;
		pts[2] = line2V1;
		pts[3] = line2V2;
		
		// find largest distance
		for(int i=0; i<pts.length; i++) {
			float D = getDistance(pts[i], center);
			if(D>d){
				index = i;
				d = D;
			}
		}
		
		// calculate 2nd point with same distance
		PointF p = (index<2)?
					line1V1:
						line2V1;
		
		if((p.x-center.x)!=0) {
			float m = getSlope(p, center);
			int direction = DIRECTION_LEFT;
			if(	center.x >= line1V1.x &&
				center.x >= line1V2.x &&
				center.x >= line2V1.x &&
				center.x >= line2V2.x)
				direction = DIRECTION_RIGHT;
			
			p = getPointOnLine(p, m, d, direction);
		}
		else {
			// vertical line
			
			p.x = center.x;
			
			if(	center.y <= pts[0].y &&
				center.y <= pts[1].y &&
				center.y <= pts[2].y &&
				center.y <= pts[3].y )
				
					p.y = canvasWidth;
				else
					p.y = 0;
		}
		
		return p;
	}
	
	protected float getDistance(PointF p, PointF p1){
		float x = p.x-p1.x;
		float y = p.y-p1.y;
		return (float)Math.sqrt(x*x+y*y);
	}
	
	
	
	////////////////////
/*
 * protected int getDirection(	PointF line1V1, 
								PointF line1V2,
								PointF line2V1, 
								PointF line2V2,
								PointF c) {
		if( line1V1.x <= c.x &&
			line1V2.x <= c.x &&
			line2V1.x >= c.x &&
			line2V2.x >= c.x)
			return DIRECTION_UPDOWN;
		
		else if(line1V1.x <= c.x &&
				line1V2.x <= c.x &&
				line2V1.x <= c.x &&
				line2V2.x <= c.x)
			return DIRECTION_RIGHT;
		
		else 
			return DIRECTION_LEFT;
	}
	
	protected PointF getPolar(PointF origin, PointF p){
		PointF p1 = new PointF(p.x - origin.x, p.y - origin.y);
		PointF polar = new PointF(-1, -1);
		
		p1.x = (float)((p1.x==0)?0.0000001:p1.x);		// worst case scenario
		polar.x = (float)Math.atan2 (p1.y, p1.x);
		polar.y = (float)Math.sqrt(p1.y*p1.y+p1.x*p1.x);
				
		return polar;
	}
	
	protected PointF getDividerPoint(PointF p) {

		PointF line1 = getPolar(p, linePts[0]);
		PointF line2 = getPolar(p, linePts[2]);
		float angle = (line1.x + line2.x)/2;
		float dis = (float)((line1.y+line2.y)/2.0);
		
		// polar to cartesian
		int d = getDirection(p);		// direction
		PointF divider = new PointF( (float)(dis * Math.cos(angle)) + p.x,
									 (float)(dis * Math.sin(angle)) + p.y);
		float m = 0;			
		
		switch(d){
		case 2: // right
			divider.x = 0;
			m = (divider.y - p.y) / (divider.x - p.x);
			divider.y = -p.x * m + p.y;
			break;
			
		case 1: // left
			divider.x = canvas.getWidth();
			m = (divider.y - p.y) / (divider.x - p.x);
			divider.y = (canvas.getWidth()-p.x) * m + p.y;
			break;
			
		case 0: // up/down

			if(p.y < linePts[0].y && p.y < linePts[2].y) {
				// points up
				angle += Math.PI / 180 * 90;
				divider.y = canvas.getHeight();
			}
			// points down
			else{
				angle -= Math.PI / 180 * 90;
				divider.y = 0;
			}
			
			PointF pt = new PointF( (float)(dis * Math.cos(angle)) + p.x,
		 						    (float)(dis * Math.sin(angle)) + p.y);
			if(pt.x-p.x==0){
				divider.x = p.x;
			}
			else {
				m = (pt.y - p.y) / (pt.x - p.x);
				divider.x = (pt.y-p.y) / m + p.x;
			}
			break;
		}
		return divider;
	}
	*/

}
