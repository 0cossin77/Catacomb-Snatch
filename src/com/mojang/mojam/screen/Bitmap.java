package com.mojang.mojam.screen;

import java.util.Arrays;

public class Bitmap {
	public int w, h;
	public int[] pixels;

	public Bitmap(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w * h];
	}
	
	public Bitmap(int w, int h, int[] pixels) {
		this.w = w;
		this.h = h;
		this.pixels = pixels;
	}

	public Bitmap(int[][] pixels2D) {
		w = pixels2D.length;
		if(w>0){
			h = pixels2D[0].length;
			pixels = new int[w*h];
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					pixels[y*w+x] = pixels2D[x][y];
				}
			}
		} else {
			h = 0;
			pixels = new int[0];
		}
	}

	public void clear(int color) {
		Arrays.fill(pixels, color);
	}

	public void blit(Bitmap bitmap, int x, int y) {
	
	    Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
		adjustBlitArea(blitArea);	
		
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = bitmap.pixels[xx];
				if (col < 0)
					pixels[tp + xx] = col;
			}
		}
	}

	public void blit(Bitmap bitmap, int x, int y, int width, int height) {
		
	    Rect blitArea = new Rect(x, y, width, height);
        adjustBlitArea(blitArea);
        		
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;
        
        for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
            int tp = yy * w + blitArea.topLeftX;
            int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
            tp -= sp;
            for (int xx = sp; xx < sp + blitWidth; xx++) {
                int col = bitmap.pixels[xx];
                if (col < 0)
                    pixels[tp + xx] = col;
            }
        }
	}

	/**
	 * Draws a Bitmap semi-transparent
	 * @param bitmap image to draw
	 * @param x position on screen
	 * @param y position on screen
	 * @param opacity range from 0x00 to 0xff
	 */
    public void opacityBlit(Bitmap bitmap, int x, int y, int opacity) {

        if(opacity == 0)
        {
            this.blit(bitmap, x, y);
            return;
        }
        
        opacity *= (int)Math.pow(16, 6);
        
        Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

        for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
            int tp = yy * w + blitArea.topLeftX;
            int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
            for (int xx = 0; xx < blitWidth; xx++) {
                int col = bitmap.pixels[sp + xx];
                if (col < 0) {
                    int color = pixels[tp + xx];
                    color += opacity;
                    
                    int a2 = (color >> 24) & 0xff;
                    int a1 = 256 - a2;

                    int rr = color & 0xff0000;
                    int gg = color & 0xff00;
                    int bb = color & 0xff;
                    
                    int r = (col & 0xff0000);
                    int g = (col & 0xff00);
                    int b = (col & 0xff);

                    r = ((r * a1 + rr * a2) >> 8) & 0xff0000;
                    g = ((g * a1 + gg * a2) >> 8) & 0xff00;
                    b = ((b * a1 + bb * a2) >> 8) & 0xff;
                    pixels[tp + xx] = 0xff000000 | r | g | b;
                }
            }
        }
    }

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {
	    
	    Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		int a2 = (color >> 24) & 0xff;
		int a1 = 256 - a2;

		int rr = color & 0xff0000;
		int gg = color & 0xff00;
		int bb = color & 0xff;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = bitmap.pixels[sp + xx];
				if (col < 0) {
					int r = (col & 0xff0000);
					int g = (col & 0xff00);
					int b = (col & 0xff);

					r = ((r * a1 + rr * a2) >> 8) & 0xff0000;
					g = ((g * a1 + gg * a2) >> 8) & 0xff00;
					b = ((b * a1 + bb * a2) >> 8) & 0xff;
					pixels[tp + xx] = 0xff000000 | r | g | b;
				}
			}
		}
	}


    /**
     * Fills semi-transparent region on screen
     * @param x position on screen
     * @param y position on screen
     * @param width of the region
     * @param height of the region
     * @param color to fill the region
     * @param opacity range from 0x00 to 0xff
     */
    public void opacityFill(int x, int y, int width, int height, int color, int opacity) {

        if(opacity == 0)
        {
            this.fill(x, y, width, height, color);
            return;
        }
        
        Bitmap bmp = new Bitmap(width, height);
        bmp.fill(0, 0, width, height, color);
        
        this.opacityBlit(bmp, x, y, opacity);
    }
    

	public void fill(int x, int y, int width, int height, int color) {
	    
	    Rect blitArea = new Rect(x, y, width, height);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			for (int xx = 0; xx < blitWidth; xx++) {
				pixels[tp + xx] = color;
			}
		}
	}
	

	private void adjustBlitArea(Rect blitArea){
	    
	    if (blitArea.topLeftX < 0) blitArea.topLeftX = 0;
        if (blitArea.topLeftY < 0) blitArea.topLeftY = 0;
        if (blitArea.bottomRightX > w) blitArea.bottomRightX = w;
        if (blitArea.bottomRightY > h) blitArea.bottomRightY = h;
	}

	public void rectangle(int x, int y, int bw, int bh, int color) {
		int x0 = x;
		int x1 = x + bw;
		int y0 = y;
		int y1 = y + bh;
		if (x0 < 0)
			x0 = 0;
		if (y0 < 0)
			y0 = 0;
		if (x1 > w)
			x1 = w;
		if (y1 > h)
			y1 = h;

		for (int yy = y0; yy < y1; yy++) {
			setPixel(x0, yy, color);
			setPixel(x1 - 1, yy, color);
		}

		for (int xx = x0; xx < x1; xx++) {
			setPixel(xx, y0, color);
			setPixel(xx, y1 - 1, color);
		}
	}

	private void setPixel(int x, int y, int color) {
		pixels[x+y*w]=color;
		
	}

	public static Bitmap rectangleBitmap(int x, int y, int x2, int y2, int color) {
		Bitmap rect = new Bitmap(x2,y2);	
		rect.rectangle(x, y, x2, y2, color);	
		return rect;
	}

	public static Bitmap rangeBitmap(int radius, int color) {
		Bitmap circle = new Bitmap(radius*2+100,radius*2+100);	
		
		circle.circle(radius, radius, radius, color);	
		return circle;
	}

	private void circle(int centerX, int centerY, int radius, int color) {
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;
	
		do {
		setPixel(centerX + x, centerY + y, color);
		setPixel(centerX + x, centerY - y, color);
		setPixel(centerX - x, centerY + y, color);
		setPixel(centerX - x, centerY - y, color);
		setPixel(centerX + y, centerY + x, color);
		setPixel(centerX + y, centerY - x, color);
		setPixel(centerX - y, centerY + x, color);
		setPixel(centerX - y, centerY - x, color);
		if (d < 0) {
		d = d + (4 * x) + 6;
		} else {
		d = d + 4 * (x - y) + 10;
		y--;
		}
		x++;
		} while (x <= y);
	}

}