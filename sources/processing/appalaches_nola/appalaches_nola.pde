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

import codeanticode.syphon.*;


//===========/----------------------------------------------
//  [_CON]  /  Constants
//=========/------------------------------------------------

int FPS = 60;
color BG_COLOR = color(240);

int SPACEBAR = 32;

float FOV = PI / 3.0;

int LEFT_SCREEN = 1;
int RIGHT_SCREEN = 2;

float RISING_SPEED = 0.5;
float ROTATION_INCREMENT = 0.15;
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

void setup()
{
	// processing setup
	size(1440, 400, P3D);
	background(BG_COLOR);
	frameRate(FPS);

	_centerX = int(width * 0.5);
	_centerY = int(height * 0.5);
	_aspectRatio = float(width) / float(height);

	// 3d camera
	_cameraZ = (height / 2.0) / tan(FOV / 2.0);
	perspective	(
					FOV, 
					_aspectRatio, 
					_cameraZ / 10.0, 
					_cameraZ * 10.0
				);

	// images
	_screenBG = loadImage(DAVEY_JONES_BG);

	setupImageVOs();
}


void draw()
{
	background(BG_COLOR);
	drawScreenBG();

	imageMode(CENTER);
	drawImages();

	// imageMode(CORNER);
	// image(_stage, 0, 0, width, height);
}


void keyPressed()
{
	if(key == SPACEBAR)
	{
		addImage();
	}
}


//===========/----------------------------------------------
//  [_MTD]  /  Methods
//=========/------------------------------------------------

void toggleScreen()
{
	if(_screen == LEFT_SCREEN)
	{
		_screen = RIGHT_SCREEN;
	}
	else if(_screen == RIGHT_SCREEN)
	{
		_screen = LEFT_SCREEN;
	}
}


void drawScreenBG()
{
	noStroke();
	fill(240, 0, 0);

	int planeWidth = width;
	int planeHeight = height;
	float planeX = -720;
	float planeY = -200;
	float planeZ = -640;
	float planeCoefficient = 3.0;

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


void setupImageVOs()
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


String getFileName(int number)
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


ImageVO getRandomVO()
{
	int index = int(random(NUM_VALUE_OBJECTS));
	return _imageVOs.get(index);
}


void addImage()
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


void setImagePosition(ImageAsset img)
{
	if(_screen ==  LEFT_SCREEN)
	{
		img.x = random 	(
							-(width * 0.25), 
							(width * 0.25)
						);
	}
	else if(_screen == RIGHT_SCREEN)
	{
		img.x = random 	(
							1440 - (width * 0.25),
							1440 + (width * 0.25)
						);
	}
	toggleScreen();
	img.y = img.maxY + (random(height) * 0.5);
	img.z = -random(160, 320);
	img.rotation = random(360);
}


void drawImage(ImageAsset img)
{
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


void drawImages()
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


void sortImagesBackToFront()
{
	/*
	For transparency to work properly for images with a z index,
	images have to be drawn from the back to the front.

	See the thread on the Processing forum: https://forum.processing.org/topic/how-to-fix-transparency-issue-with-images-with-z-depths
	*/

	if(_images != null && _images.size() > 0)
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


void printImagesZ(ArrayList<ImageAsset> l)
{
	int numLoops = l.size();
	for(int i = 0; i < numLoops; i++)
	{
		ImageAsset asset = l.get(i);
		println("image z: " + asset.z);
	}
}