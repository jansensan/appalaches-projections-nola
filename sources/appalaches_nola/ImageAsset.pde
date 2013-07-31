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

	private color _linesColor;
	private color _fillColor;


	//===========/----------------------------------------------
	//  [_CON]  /  Constructor
	//=========/------------------------------------------------

	public ImageAsset	(
							String url,
							color linesColor,
							color fillColor
						)
	{
		_linesColor = linesColor;
		_fillColor = fillColor;
		resetImageSource(url);
	}


	//===========/----------------------------------------------
	//  [_PUB]  /  Public methods
	//=========/------------------------------------------------

	void resetImageSource(String url)
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


	void draw()
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


	int compareZWith(ImageAsset comparable)
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


	String toString()
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

	void setTargetDimensions(int w, int h)
	{
		_targetDimensions = new int[2];
		float ratio;

		if(w > h)
		{
			ratio = w / h;
			_targetDimensions[0] = int(TARGET_SIZE);
			_targetDimensions[1] = int(h * ratio);
		}
		else
		{
			ratio = h / w;
			_targetDimensions[0] = int(w * ratio);
			_targetDimensions[1] = int(TARGET_SIZE);
		}
	}


	//===========/----------------------------------------------
	//  [_GTR]  /  Getters
	//=========/------------------------------------------------

	int imageWidth()
	{
		return _targetDimensions[0];
	}


	int imageHeight()
	{
		return _targetDimensions[1];
	}
}