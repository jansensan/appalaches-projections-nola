class ImageVO
{
	String imageURL;
	color linesColor;
	color fillColor;


	//===========/----------------------------------------------
	//  [_CON]  /  Constructor
	//=========/------------------------------------------------

	public ImageVO	(
						String url, 
						color lc,
						color fc
					)
	{
		imageURL = url;
		linesColor = lc;
		fillColor = fc;
	}


	String toString()
	{
		String str = "[ImageVO ";
		str += "imageURL='" + imageURL + "' ";
		str += "linesColor='" + hex(linesColor) + "' ";
		str += "fillColor='" + hex(fillColor) + "' ";
		str += "]";
		return str;
	}
}