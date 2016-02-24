package kr.neolab.sdk.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import kr.neolab.sdk.ink.structure.Dot;
import kr.neolab.sdk.ink.structure.Stroke;
import kr.neolab.sdk.metadata.structure.Symbol;
import kr.neolab.sdk.util.NLog;
import android.util.SparseArray;
import android.util.Xml;

public class MetadataCtrl implements IMetadataCtrl
{
	private static MetadataCtrl myInstance = null;

	private HashMap<String, ArrayList<Symbol>> symbolTable;
	private HashMap<String, Page> pageTable;
	private SparseArray<Book> bookTable;

	public static float PIXEL_TO_DOT_SCALE = 600f / 72f / 56f;

	private MetadataCtrl()
	{
		symbolTable = new HashMap<String, ArrayList<Symbol>>();
		pageTable = new HashMap<String, Page>();
		bookTable = new SparseArray<Book>();
	}

	public static synchronized MetadataCtrl getInstance()
	{
		if ( myInstance == null )
			myInstance = new MetadataCtrl();

		return myInstance;
	}

	@Override
	public void loadFile( File file ) throws XmlPullParserException, IOException, SAXException, ParserConfigurationException
	{
		String fileName = file.getName().toLowerCase( Locale.US );

		NLog.d( "[MetadataCtrl] load file : " + fileName );

		if ( !fileName.endsWith( ".nproj" ) )
		{
			return;
		}

		InputStream is = new FileInputStream( file );

		this.parseBySAX( is );
	}

	@Override
	public void loadFiles( String fileDirectoryPath )
	{
		NLog.d( "[MetadataCtrl] loadFiles : " + fileDirectoryPath );

		try
		{
			File f = new File( fileDirectoryPath );
			File[] fileNames = f.listFiles();

			for ( int i = 0; i < fileNames.length; i++ )
			{
				File file = fileNames[i];

				if ( file.isFile() )
				{
					this.loadFile( file );
				}
			}
		}
		catch ( Exception e )
		{
			NLog.e( "[MetadataCtrl] can not load nproj file from " + fileDirectoryPath, e );
		}
	}

	private String getQueryString( int noteId, int pageId )
	{
		return noteId + "_" + pageId;
	}

	@Override
	public String getTitle( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return null;
		}

		return result.title;
	}

	@Override
	public int getOwnerCode( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return -1;
		}

		return result.ownerCode;
	}

	@Override
	public int getSectionCode( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return -1;
		}

		return result.sectionCode;
	}
	
	@Override
	public int getKind( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return 0;
		}

		return result.kind;
	}
	
	@Override
	public ArrayList<Integer> getNoteTypeList( int kind )
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < bookTable.size();i++)
		{
			int key = bookTable.keyAt(i);
			Book book = bookTable.get(key);
			if(book.kind == kind)
				ret.add(key);
		}
		return ret;
	}

	
	@Override
	public String getExtraInfo( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return null;
		}

		return result.extra_info;
	}

	@Override
	public float getPageWidth( int noteId, int pageId )
	{
		Page result = this.pageTable.get( this.getQueryString( noteId, pageId ) );

		if ( result == null )
		{
			return 0;
		}

		return result.width;
	}

	@Override
	public float getPageHeight( int noteId, int pageId )
	{
		Page result = this.pageTable.get( this.getQueryString( noteId, pageId ) );

		if ( result == null )
		{
			return 0;
		}

		return result.height;
	}

	@Override
	public int getTotalPages( int noteId )
	{
		Book result = bookTable.get( noteId );

		if ( result == null )
		{
			return 0;
		}

		return result.totalPage;
	}

	private void put( int noteId, int pageId, Symbol event )
	{
		ArrayList<Symbol> result = this.symbolTable.get( this.getQueryString( noteId, pageId ) );

		if ( result == null )
		{
			ArrayList<Symbol> newEvents = new ArrayList<Symbol>();
			newEvents.add( event );

			this.symbolTable.put( this.getQueryString( noteId, pageId ), newEvents );
		}
		else
		{
			result.add( event );
		}
	}

	private void put( int noteId, int pageId, Page page )
	{
		this.pageTable.put( this.getQueryString( noteId, pageId ), page );
	}

	@Override
	public Symbol[] findApplicableSymbols( int noteId, int pageId )
	{
		ArrayList<Symbol> candidates = this.symbolTable.get( getQueryString( noteId, pageId ) );

		if ( candidates == null || candidates.size() <= 0 )
		{
			return null;
		}

		ArrayList<Symbol> result = new ArrayList<Symbol>();

		for ( Symbol e : candidates )
		{
			result.add( e );
		}

		if ( result.size() > 0 )
		{
			return result.toArray( new Symbol[1] );
		}
		else
		{
			return null;
		}
	}

	@Override
	public Symbol[] findApplicableSymbols( Stroke nstr )
	{
		ArrayList<Symbol> candidates = this.symbolTable.get( getQueryString( nstr.noteId, nstr.pageId ) );

		if ( candidates == null || candidates.size() <= 0 )
		{
			return null;
		}

		ArrayList<Symbol> result = new ArrayList<Symbol>();

		for ( Symbol e : candidates )
		{
			for ( int i = 0; i < nstr.size(); i++ )
			{
				Dot pf = nstr.get( i );

				if ( e.contains( pf.x, pf.y ) )
				{
					result.add( e );
					break;
				}
			}
		}

		if ( result.size() > 0 )
		{
			return result.toArray( new Symbol[1] );
		}
		else
		{
			return null;
		}
	}

	@Override
	public Symbol[] findApplicableSymbols( int noteId, int pageId, float x, float y )
	{
		ArrayList<Symbol> candidates = this.symbolTable.get( getQueryString( noteId, pageId ) );

		if ( candidates == null || candidates.size() <= 0 )
		{
			return null;
		}

		ArrayList<Symbol> result = new ArrayList<Symbol>();

		for ( Symbol e : candidates )
		{
			if ( pageId == e.pageId && e.contains( x, y ) )
			{
				result.add( e );
			}
		}

		if ( result.size() > 0 )
		{
			return result.toArray( new Symbol[1] );
		}
		else
		{
			return null;
		}
	}

	@Override
	public Symbol[] getSymbols()
	{
		ArrayList<Symbol> result = new ArrayList<Symbol>();

		Set<String> keys = this.symbolTable.keySet();

		for ( String key : keys )
		{
			ArrayList<Symbol> candidates = this.symbolTable.get( key );

			if ( candidates != null && candidates.size() > 0 )
			{
				result.addAll( candidates );
			}
		}

		if ( result.size() > 0 )
		{
			return result.toArray( new Symbol[1] );
		}
		else
		{
			return null;
		}
	}

	private int noteId = 0, ownerCode = 0, sectionCode = 0,totalPage = 0, kind = 0;
	private String bookTitle = "", tag = "", extra_info = "";
	private boolean isSymbol = false;
	private Symbol symbol = null;
	private LinkedHashMap<String, Symbol> lnkTbl = new LinkedHashMap<String, Symbol>();

	@Override
	public void parseBySAX( InputStream istream ) throws IOException, SAXException, ParserConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();

		XMLReader reader = parser.getXMLReader();

		lnkTbl.clear();

		reader.setContentHandler( new DefaultHandler()
		{
			@Override
			public void startElement( String uri, String localName, String qName, Attributes atts ) throws SAXException
			{
				tag = localName;

				if ( tag.equals( "pages" ) )
				{
					totalPage = Integer.parseInt( atts.getValue( "count" ) );
				}
				else if ( tag.equals( "page_item" ) )
				{
					Page page = new Page();

					page.noteId = noteId;
					page.pageId = Integer.parseInt( atts.getValue( "number" ) ) + 1;
					page.angle = Integer.parseInt( atts.getValue( "rotate_angle" ) );
					page.width = Float.parseFloat( atts.getValue( "x2" ) ) * PIXEL_TO_DOT_SCALE;
					page.height = Float.parseFloat( atts.getValue( "y2" ) ) * PIXEL_TO_DOT_SCALE;

					// 페이지 데이터 테이블 추가
					put( noteId, page.pageId, page );
				}
				else if ( tag.equals( "symbol" ) )
				{
					isSymbol = true;

					int pageId = Integer.parseInt( atts.getValue( "page" ) ) + 1;
					float x = Float.parseFloat( atts.getValue( "x" ) ) * PIXEL_TO_DOT_SCALE;
					float y = Float.parseFloat( atts.getValue( "y" ) ) * PIXEL_TO_DOT_SCALE;
					float width = Float.parseFloat( atts.getValue( "width" ) ) * PIXEL_TO_DOT_SCALE;
					float height = Float.parseFloat( atts.getValue( "height" ) ) * PIXEL_TO_DOT_SCALE;

					symbol = new Symbol( noteId, pageId, "", "", "", x, y, x + width, y + height );
				}
				else if ( isSymbol && tag.equals( "command" ) )
				{
					symbol.action = atts.getValue( "action" );
					symbol.param = atts.getValue( "param" );
				}
				else if ( isSymbol && tag.equals( "matching_symbols" ) )
				{
					symbol.previousId = atts.getValue( "previous" );
					symbol.nextId = atts.getValue( "next" );
				}
			}

			@Override
			public void endElement( String uri, String localName, String qName ) throws SAXException
			{
				if ( localName.equals( "symbol" ) )
				{
					isSymbol = false;

					lnkTbl.put( symbol.id, symbol );

					// 심볼 데이터 테이블 추가
					put( noteId, symbol.pageId, symbol );

					symbol = null;
				}
			}

			@Override
			public void characters( char[] ch, int start, int length ) throws SAXException
			{
				if ( length <= 0 || tag == null )
				{
					return;
				}
				
				if ( tag.equals( "owner" ) )
				{
					ownerCode = Integer.parseInt( charsToString( ch, start, length ) );
					tag = null;
				}
				else if ( tag.equals( "section" ) )
				{
					sectionCode = Integer.parseInt( charsToString( ch, start, length ) );
					tag = null;
				}
				else if ( tag.equals( "code" ) )
				{
					noteId = Integer.parseInt( charsToString( ch, start, length ) );
					tag = null;
				}
				else if ( tag.equals( "kind" ) )
				{
					kind = Integer.parseInt( charsToString( ch, start, length ) );
					tag = null;
				}
				else if ( tag.equals( "title" ) )
				{
					bookTitle = charsToString( ch, start, length );
					tag = null;
				}
				else if ( tag.equals( "extra_info" ) )
				{
					extra_info = charsToString( ch, start, length );
					tag = null;
				}

				if ( isSymbol )
				{
					if ( tag.equals( "id" ) )
					{
						symbol.id = charsToString( ch, start, length );
						tag = null;
					}
					else if ( tag.equals( "name" ) )
					{
						symbol.name = charsToString( ch, start, length );
						tag = null;
					}
				}
			}

		} );

		reader.parse( new InputSource( istream ) );

		Book nBook = new Book( noteId,ownerCode, sectionCode, totalPage, bookTitle, kind, extra_info );

		bookTable.put( noteId, nBook );

		// 뽑아낸 심볼들 한번 돌려서 서로 링크 걸린 심볼을 연결
		Set<String> ids = lnkTbl.keySet();

		Iterator<String> it = ids.iterator();

		while ( it.hasNext() )
		{
			Symbol sym = lnkTbl.get( it.next() );

			if ( sym.previousId != null )
			{
				sym.previous = lnkTbl.get( sym.previousId );
			}

			if ( sym.nextId != null )
			{
				sym.next = lnkTbl.get( sym.nextId );
			}
		}
	}

	@Override
	public void print()
	{
		int books = bookTable.size();

		for ( int i = 0; i < books; i++ )
		{
			Book book = bookTable.get( bookTable.keyAt( i ) );
			NLog.d( "[MetadataCtrl] book : " + book.toString() );
		}

		Symbol[] syms = getSymbols();

		for ( Symbol sym : syms )
		{
			NLog.d( "[MetadataCtrl] " + sym.param + "/" + sym.next + "/" + sym.previous );
		}
	}

	private String charsToString( char[] ch, int start, int length )
	{
		String value = null;

		if ( ch.length > 0 )
		{
			StringBuilder item = new StringBuilder();
			item.append( ch, start, length );

			value = item.toString().trim();

			if ( value.equals( "" ) )
			{
				value = null;
			}

			item = null;
		}

		return value;
	}

	@Override
	public void parseByXmlPullParser( InputStream istream ) throws XmlPullParserException, IOException
	{
		XmlPullParser parser = Xml.newPullParser();

		String tag = null, nameSpace = null;

		boolean isSymbol = false;

		Symbol symbol = null;

		int noteId = 0, ownerCode = 0, sectionCode = 0, totalPage = 0, kind = 0;
		String bookTitle = "", extra_info = "";

		LinkedHashMap<String, Symbol> lnkTbl = new LinkedHashMap<String, Symbol>();

		parser.setInput( new InputStreamReader( istream ) );

		for ( int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next() )
		{
			switch ( eventType )
			{
				case XmlPullParser.START_TAG:

					tag = parser.getName();

					if ( tag.equals( "pages" ) )
					{
						totalPage = Integer.parseInt( parser.getAttributeValue( nameSpace, "count" ) );
					}
					else if ( tag.equals( "page_item" ) )
					{
						Page page = new Page();

						page.noteId = noteId;

						page.pageId = Integer.parseInt( parser.getAttributeValue( nameSpace, "number" ) ) + 1;
						page.angle = Integer.parseInt( parser.getAttributeValue( nameSpace, "rotate_angle" ) );
						page.width = Float.parseFloat( parser.getAttributeValue( nameSpace, "x2" ) ) * PIXEL_TO_DOT_SCALE;
						page.height = Float.parseFloat( parser.getAttributeValue( nameSpace, "y2" ) ) * PIXEL_TO_DOT_SCALE;

						// 페이지 데이터 테이블 추가
						this.put( noteId, page.pageId, page );
					}
					else if ( tag.equals( "symbol" ) )
					{
						isSymbol = true;

						int pageId = Integer.parseInt( parser.getAttributeValue( nameSpace, "page" ) ) + 1;
						float x = Float.parseFloat( parser.getAttributeValue( nameSpace, "x" ) ) * PIXEL_TO_DOT_SCALE;
						float y = Float.parseFloat( parser.getAttributeValue( nameSpace, "y" ) ) * PIXEL_TO_DOT_SCALE;
						float width = Float.parseFloat( parser.getAttributeValue( nameSpace, "width" ) ) * PIXEL_TO_DOT_SCALE;
						float height = Float.parseFloat( parser.getAttributeValue( nameSpace, "height" ) ) * PIXEL_TO_DOT_SCALE;

						symbol = new Symbol( noteId, pageId, "", "", "", x, y, x + width, y + height );
					}
					else if ( isSymbol && tag.equals( "command" ) )
					{
						symbol.action = parser.getAttributeValue( nameSpace, "action" );
						symbol.param = parser.getAttributeValue( nameSpace, "param" );
					}

					break;

				case XmlPullParser.TEXT:

					String value = parser.getText().trim();

					if ( value != null && !value.equals( "" ) )
					{
						if ( tag.equals( "owner" ) )
						{
							ownerCode = Integer.parseInt( value );
						}
						else if ( tag.equals( "section" ) )
						{
							sectionCode = Integer.parseInt( value );
						}
						else if ( tag.equals( "code" ) )
						{
							noteId = Integer.parseInt( value );
						}
						else if ( tag.equals( "kind" ) )
						{
							kind = Integer.parseInt( value );
						}
						else if ( tag.equals( "title" ) )
						{
							bookTitle = value;
						}
						else if ( tag.equals( "extra_info" ) )
						{
							extra_info = value;
						}

						if ( isSymbol )
						{
							if ( tag.equals( "id" ) )
							{
								symbol.id = value;
							}
							else if ( tag.equals( "name" ) )
							{
								symbol.name = value;
							}
						}
					}

					break;

				case XmlPullParser.END_TAG:

					tag = parser.getName();

					if ( tag.equals( "symbol" ) )
					{
						isSymbol = false;

						lnkTbl.put( symbol.id, symbol );

						// 심볼 데이터 테이블 추가
						this.put( noteId, symbol.pageId, symbol );

						symbol = null;
					}

					break;

				default:
					break;
			}
		}

		Book nBook = new Book( noteId,ownerCode, sectionCode, totalPage, bookTitle , kind, extra_info );

		bookTable.put( noteId, nBook );

		// 뽑아낸 심볼들 한번 돌려서 서로 링크 걸린 심볼을 연결
		Set<String> ids = lnkTbl.keySet();

		Iterator<String> it = ids.iterator();

		while ( it.hasNext() )
		{
			Symbol sym = lnkTbl.get( it.next() );

			if ( sym.previousId != null )
			{
				sym.previous = lnkTbl.get( sym.previousId );
			}

			if ( sym.nextId != null )
			{
				sym.next = lnkTbl.get( sym.nextId );
			}
		}
	}

	private class Page
	{
		public int noteId, pageId, angle;
		public float width, height;

		public String toString()
		{
			return "Page => noteId : " + noteId + ", pageId : " + pageId + ", angle : " + angle + ", width : " + width + ", height : " + height;
		}
	}

	private static class Book
	{
		public int noteId, totalPage, ownerCode, sectionCode, kind;
		public String title, extra_info;

		public Book( int noteId, int ownerCode, int sectionCode, int totalPage, String title , int kind, String extra_info)
		{
			this.noteId = noteId;
			this.ownerCode = ownerCode;
			this.sectionCode = sectionCode;
			this.kind = kind;
			this.totalPage = totalPage;
			this.title = title;
			if(this.title == null)
				this.title = "";
			
			this.extra_info = extra_info;
			if(this.extra_info == null)
				this.extra_info = "";
		}

		public String toString()
		{
			return "Book => title : " + title + ", noteId : " + noteId  + ", ownerCode : " + ownerCode  + ", sectionCode : " + sectionCode + ", totalPage : " + totalPage + ", kind : " + kind + ", extra_info : " + extra_info;
		}
	}
}
