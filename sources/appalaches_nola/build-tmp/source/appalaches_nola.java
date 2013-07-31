import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import codeanticode.syphon.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class appalaches_nola extends PApplet {

//===========/----------------------------------------------
//  [_TBL]  /  Table of Contents
//=========/------------------------------------------------
/*
- Imports		[_IMP]
- Constants		[_CON]
- Variables		[_VAR]
- Processing	[_PRO]
- Methods		[_MTD]
*/


//===========/----------------------------------------------
//  [_IMP]  /  Imports
//=========/------------------------------------------------




//===========/----------------------------------------------
//  [_CON]  /  Constants
//=========/------------------------------------------------

int FPS = 60;
int BG_COLOR = color(240);

int SPACEBAR = 32;

float FOV = PI / 3.0f;

int LEFT_SCREEN = 1;
int RIGHT_SCREEN = 2;

float RISING_SPEED = 0.5f;
float ROTATION_INCREMENT = 0.15f;
int NUM_VALUE_OBJECTS = 2000;
int MAX_ASSETS = 48;

String IMAGE_DIR = "images/";
String DAVEY_JONES_BG = IMAGE_DIR + "davey-jones-bg.png";


//===========/----------------------------------------------
//  [_VAR]  /  Variables
//=========/------------------------------------------------

int _centerX;
int _centerY;

float _aspectRatio;
int _screen = LEFT_SCREEN;

PImage _screenBG;

float _cameraZ;

ArrayList<ImageVO> _imageVOs;

ArrayList<ImageAsset> _images;
int _numVOs = 0;
int _numImages = 0;


//===========/----------------------------------------------
//  [_PRO]  /  Processing
//=========/------------------------------------------------

public void setup()
{
	// processing setup
	size(1440, 400, P3D);
	background(BG_COLOR);
	frameRate(FPS);

	_centerX = PApplet.parseInt(width * 0.5f);
	_centerY = PApplet.parseInt(height * 0.5f);
	_aspectRatio = PApplet.parseFloat(width) / PApplet.parseFloat(height);

	// 3d camera
	_cameraZ = (height / 2.0f) / tan(FOV / 2.0f);
	perspective	(
					FOV, 
					_aspectRatio, 
					_cameraZ / 10.0f, 
					_cameraZ * 10.0f
				);

	// images
	_screenBG = loadImage(DAVEY_JONES_BG);

	setupImageVOs();
}


public void draw()
{
	background(BG_COLOR);
	drawScreenBG();

	imageMode(CENTER);
	drawImages();

	// imageMode(CORNER);
	// image(_stage, 0, 0, width, height);
}


public void keyPressed()
{
	if(key == SPACEBAR)
	{
		addImage();
	}
}


//===========/----------------------------------------------
//  [_MTD]  /  Methods
//=========/------------------------------------------------

public void toggleScreen()
{
	if(_screen == LEFT_SCREEN)
	{
		_screen = RIGHT_SCREEN;
	}
	else if(_screen == RIGHT_SCREEN)
	{
		_screen = LEFT_SCREEN;
	}
	println("_screen: " + _screen);
}


public void drawScreenBG()
{
	noStroke();
	fill(240, 0, 0);

	int planeWidth = width;
	int planeHeight = height;
	float planeX = -720;
	float planeY = -200;
	float planeZ = -640;
	float planeCoefficient = 3.0f;

	pushMatrix();
	translate(planeX, planeY, planeZ);	// move anchor point to object position
	imageMode(CORNER);
	image	(
				_screenBG, 
				planeX, 
				planeY, 
				planeWidth * planeCoefficient, 
				planeHeight * planeCoefficient
			);
	translate(-planeX, -planeY, planeZ);	// move back anchor to initial position
	popMatrix();
}


public void setupImageVOs()
{
	_imageVOs = new ArrayList<ImageVO>();

	for(int i = 0; i < NUM_VALUE_OBJECTS; i++)
	{
		ImageVO vo = new ImageVO	(
										getFileName(i + 1), 
										color(random(255), random(255), random(255)),
										color(random(255), random(255), random(255))
									);
		// println("vo: " + vo);
		_imageVOs.add(vo);
	}
	_numVOs = _imageVOs.size();
}


public String getFileName(int number)
{
	String filename = "" + number;

	String itemNumberPadding = "";
	switch(filename.length())
	{
		case 1:
			itemNumberPadding = "000";
			break;

		case 2:
			itemNumberPadding = "00";
			break;

		case 3:
			itemNumberPadding = "0";
			break;
	}
	String itemNumber = itemNumberPadding + number;

	if(number < 1001)
	{
		filename = "0000-1000";
	}
	else if(number < 2001) 
	{
		filename = "1001-2000";
	}
	else if(number < 3001) 
	{
		filename = "2001-3000";
	}
	else if(number < 4001) 
	{
		filename = "3001-4000";
	}
	else
	{
		filename = "4001-5000";
	}
	filename += "/drawing-" + itemNumber + ".png";
	return IMAGE_DIR + filename;
}


public ImageVO getRandomVO()
{
	int index = PApplet.parseInt(random(NUM_VALUE_OBJECTS));
	return _imageVOs.get(index);
}


public void addImage()
{
	ImageVO vo = getRandomVO();

	// max otherwise memory isnt sufficient
	if(_numImages < MAX_ASSETS)
	{
		// create asset
		ImageAsset img = new ImageAsset	(
											vo.imageURL, 
											vo.linesColor,
											vo.fillColor
										);
		img.minY = -img.imageHeight();
		img.maxY = height + img.imageHeight();
		setImagePosition(img);

		// add to array
		if(_images == null)
		{
			_images = new ArrayList<ImageAsset>();
		}
		_images.add(img);
		_numImages = _images.size();

		// TODO: check for max, maybe with pool
	}
	else
	{
		println("max reached, consider pooling");
	}

	// sort
	sortImagesBackToFront();
}


public void setImagePosition(ImageAsset img)
{
	if(_screen ==  LEFT_SCREEN)
	{
		img.x = random 	(
							-(width * 0.25f), 
							(width * 0.25f)
						);
		// img.x = 0;
	}
	else if(_screen == RIGHT_SCREEN)
	{
		// img.x = 1440;
		img.x = random 	(
							1440 - (width * 0.25f),
							1440 + (width * 0.25f)
						);
	}
	// println("img.x: " + img.x);
	toggleScreen();
	// img.x = width * 0.5;
	
	// img.y = img.maxY + (random(height) * 0.5);
	img.y = height * 0.5f; 
	
	img.z = -random(160, 320);
	
	img.rotation = random(360);
}


public void drawImage(ImageAsset img)
{
	// println("--- drawImage() ---");
	// println("img: " + img);

	// draw
	pushMatrix();

	translate(img.x, img.y, img.z);		// move anchor point to object position
	rotate(radians(img.rotation));		// apply rotation
	translate(-img.x, -img.y, img.z);	// move back anchor to initial position
	
	img.draw();

	popMatrix();


	// set values for next draw call
	img.y -= RISING_SPEED;
	if(img.y <= img.minY)
	{
		ImageVO vo = getRandomVO();
		img.resetImageSource(vo.imageURL);
		
		setImagePosition(img);
		sortImagesBackToFront();
	}
	img.rotation += ROTATION_INCREMENT;
}


public void drawImages()
{
	if(_images != null && _images.size() > 0)
	{
		for(int i = 0; i < _numImages; i++)
		{
			ImageAsset img = _images.get(i);
			drawImage(img);
		}
	}
}


public void sortImagesBackToFront()
{
	/*
	For transparency to work properly for images with a z index,
	images have to be drawn from the back to the front.

	See the thread on the Processing forum: https://forum.processing.org/topic/how-to-fix-transparency-issue-with-images-with-z-depths
	*/

	if(_images != null && _images.size() > 0)
	{
		ArrayList<ImageAsset> sortedImages = new ArrayList<ImageAsset>();
		
		for(int i = 0; i < _numImages; i++)
		{
			ImageAsset current = _images.get(i);
			if(i == 0)
			{
				sortedImages.add(current);
			}
			else
			{
				int numJLoops = sortedImages.size();
				jLoop:
				for(int j = 0; j < numJLoops; j++)
				{
					ImageAsset sorted = sortedImages.get(j);
					
					// if value is higher
					if(current.compareZWith(sorted) > 0)
					{
						// if last sorted item
						if(j == numJLoops - 1)
						{
							sortedImages.add(current);
							break jLoop;
						}
					}
					// if value is lower or equal
					else
					{
						sortedImages.add(j, current);
						break jLoop;
					}
				}
			}
		}
		_images = sortedImages;
	}
}


public void printImagesZ(ArrayList<ImageAsset> l)
{
	int numLoops = l.size();
	for(int i = 0; i < numLoops; i++)
	{
		ImageAsset asset = l.get(i);
		println("image z: " + asset.z);
	}
}
class ImageAsset
{
	int TARGET_SIZE = 320;


	float x = 0;
	float y = 0;
	float z = 0;
	float maxY;
	float minY;
	float rotation = 0;

	private PImage _source;

	private int[] _targetDimensions;
	private PImage _linesImage;
	private PImage _fillImage;

	private int _linesColor;
	private int _fillColor;


	//===========/----------------------------------------------
	//  [_CON]  /  Constructor
	//=========/------------------------------------------------

	public ImageAsset	(
							String url,
							int linesColor,
							int fillColor
						)
	{
		_linesColor = linesColor;
		_fillColor = fillColor;
		resetImageSource(url);
	}


	//===========/----------------------------------------------
	//  [_PUB]  /  Public methods
	//=========/------------------------------------------------

	public void resetImageSource(String url)
	{
		// set source
		_source = loadImage(url);
		_source.loadPixels();

		// var relative to source
		int numPixels = _source.pixels.length;
		setTargetDimensions	(
								_source.width, 
								_source.height
							);

		// instantiate fill color
		_linesImage = createImage	(
										_source.width, 
										_source.height,
										ARGB
									);
		_linesImage.loadPixels();

		// instantiate lines color
		_fillImage = createImage	(
										_source.width, 
										_source.height,
										ARGB
									);
		_fillImage.loadPixels();

		// loop through source
		int threshold = 128;
		for(int i = 0; i < numPixels; i++)
		{
			// fill color
			_linesImage.pixels[i] = color	(
												red(_linesColor),
												green(_linesColor),
												blue(_linesColor),
												alpha(_source.pixels[i])
											); 

			// lines color
			if	(
					red(_source.pixels[i]) > threshold 
					&& green(_source.pixels[i]) > threshold
					&& blue(_source.pixels[i]) > threshold
				)
			{
				_fillImage.pixels[i] = _fillColor;
			}
		}
		_linesImage.updatePixels();
		_fillImage.updatePixels();
		_source = null;
	}


	public void draw()
	{
		image	(
					_linesImage, 
					x, y,
					_targetDimensions[0],
					_targetDimensions[1]
				);
		image	(
					_fillImage, 
					x, y,
					_targetDimensions[0],
					_targetDimensions[1]
				);
	}


	public int compareZWith(ImageAsset comparable)
	{
		// value is smaller
		if(z < comparable.z)
		{
			return -1;
		}
		
		// value is bigger
		if(z > comparable.z)
		{
			return 1;
		}
		
		// value is the same
		return 0;
	}


	public String toString()
	{
		String str = "[ImageAsset ";
		str += "imageWidth='" + _targetDimensions[0] + "' ";
		str += "imageHeight='" + _targetDimensions[1] + "' ";
		str += "linesColor='" + hex(_linesColor) + "' ";
		str += "fillColor='" + hex(_fillColor) + "' ";
		str += "]";
		return str;
	}


	//===========/----------------------------------------------
	//  [_MTD]  /  Methods
	//=========/------------------------------------------------

	public void setTargetDimensions(int w, int h)
	{
		_targetDimensions = new int[2];
		float ratio;

		if(w > h)
		{
			ratio = w / h;
			_targetDimensions[0] = PApplet.parseInt(TARGET_SIZE);
			_targetDimensions[1] = PApplet.parseInt(h * ratio);
		}
		else
		{
			ratio = h / w;
			_targetDimensions[0] = PApplet.parseInt(w * ratio);
			_targetDimensions[1] = PApplet.parseInt(TARGET_SIZE);
		}
	}


	//===========/----------------------------------------------
	//  [_GTR]  /  Getters
	//=========/------------------------------------------------

	public int imageWidth()
	{
		return _targetDimensions[0];
	}


	public int imageHeight()
	{
		return _targetDimensions[1];
	}
}
class ImageVO
{
	String imageURL;
	int linesColor;
	int fillColor;


	//===========/----------------------------------------------
	//  [_CON]  /  Constructor
	//=========/------------------------------------------------

	public ImageVO	(
						String url, 
						int lc,
						int fc
					)
	{
		imageURL = url;
		linesColor = lc;
		fillColor = fc;
	}


	public String toString()
	{
		String str = "[ImageVO ";
		str += "imageURL='" + imageURL + "' ";
		str += "linesColor='" + hex(linesColor) + "' ";
		str += "fillColor='" + hex(fillColor) + "' ";
		str += "]";
		return str;
	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "appalaches_nola" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
