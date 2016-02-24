package kr.neolab.sdk.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import kr.neolab.sdk.ink.structure.Stroke;
import kr.neolab.sdk.metadata.structure.Symbol;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

/**
 * 메타데이터 파일을 로딩하고 데이터 조회 기능을 제공함
 * 
 * @author CHY
 */
public interface IMetadataCtrl
{
	/**
	 * 메타데이터 파일 로딩 (개별파일)
	 * 
	 * @param file
	 */
	public void loadFile( File file ) throws XmlPullParserException, IOException, SAXException, ParserConfigurationException;

	/**
	 * 메타데이터 파일 로딩 (디렉토리)
	 * 
	 * @param fileDirectoryPath
	 */
	public void loadFiles( String fileDirectoryPath );

	/**
	 * 제목
	 * 
	 * @param noteId
	 * @return
	 */
	public String getTitle( int noteId );

	
	/**
	 * OwnerCode
	 * 
	 * @param noteId
	 * @return
	 */
	public int getOwnerCode( int noteId );
	
	/**
	 * SectionCode
	 * 
	 * @param noteId
	 * @return
	 */
	public int getSectionCode( int noteId );

	/**
	 * kind : 0 normal 1 franklin 
	 * 
	 * @param noteId
	 * @return
	 */
	public int getKind( int noteId );

	/**
	 * kind : 0 normal 1 franklin 
	 * 
	 * @param kind
	 * @return
	 */
	public ArrayList<Integer> getNoteTypeList( int kind );
	
	
	/**
	 * ExtraInfo
	 * 
	 * @param noteId
	 * @return
	 */
	public String getExtraInfo( int noteId );

	/**
	 * 총 페이지 수
	 * @param noteId
	 * @return
	 */
	public int getTotalPages( int noteId );
	
	/**
	 * 페이지 width
	 * 
	 * @param noteId
	 * @param pageId
	 * @return
	 */
	public float getPageWidth( int noteId, int pageId );

	/**
	 * 페이지 height
	 * 
	 * @param noteId
	 * @param pageId
	 * @return
	 */
	public float getPageHeight( int noteId, int pageId );

	/**
	 * Stroke와 겹쳐진 Symbol을 구함
	 * 
	 * @param nstr
	 * @return
	 */
	public Symbol[] findApplicableSymbols( Stroke nstr );

	/**
	 * x,y 좌표를 포함하는 Symbol을 구함
	 * 
	 * @param noteId
	 * @param pageId
	 * @param x
	 * @param y
	 * @return
	 */
	public Symbol[] findApplicableSymbols( int noteId, int pageId, float x, float y );

	/**
	 * Page에 등록된 모든 Symbol을 구함
	 * 
	 * @param noteId
	 * @param pageId
	 * @return
	 */
	public Symbol[] findApplicableSymbols( int noteId, int pageId );

	/**
	 * 등록된 모든 Symbol을 구함
	 * 
	 * @return
	 */
	public Symbol[] getSymbols();

	public void parseBySAX( InputStream istream ) throws IOException, SAXException, ParserConfigurationException;
	
	public void parseByXmlPullParser( InputStream istream ) throws XmlPullParserException, IOException;
	
	public void print();
}
