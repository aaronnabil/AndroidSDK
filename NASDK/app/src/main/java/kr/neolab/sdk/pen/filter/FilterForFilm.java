package kr.neolab.sdk.pen.filter;

import kr.neolab.sdk.ink.structure.DotType;

public class FilterForFilm
{

	private static final int delta = 2;
	private static final int filmSizeX = 60, filmSizeY = 90;

	private Fdot fdot1, fdot2;
	private boolean fsecondCheck = true, fthirdCheck = true;

	private IFilterListener listener = null;

	private static final int MAX_OWNER = 1024;
	private static final int MAX_NOTE_ID = 16384;
	private static final int MAX_PAGE_ID = 262143;

	public FilterForFilm( IFilterListener listener )
	{
		super();
		this.listener = listener;
	}

	public synchronized void put( Fdot mdot )
	{
		if ( !validateCode( mdot ) )
		{
			return;
		}

		// Start Dot는 일단 1번에 넣줌
		if ( DotType.isPenActionDown( mdot.dotType ) )
		{
			fdot1 = mdot;
		}

		// Middle dot는 두번째는 그냥 넣고 세번째부터 검증
		// 첫번쨰 dot 검증 실패시 두번째-> 첫번째, 현재-> 두번째
		// 첫번째 dot 건증 성공시
		else if ( DotType.isPenActionMove( mdot.dotType ) )
		{
			// Middle의 첫번째에서는 그냥 넣어줌
			if ( fsecondCheck )
			{
				fdot2 = mdot;
				fsecondCheck = false;
			}
			// 미들의 다음Dot는 첫번째것 검증 성공시 Middle validation 첵, 실패시 다음Dot를 넣어줌
			else if ( fthirdCheck )
			{
				if ( validateStartDot( fdot1, fdot2, mdot ) )
				{
					listener.onFilteredDot( fdot1 );

					if ( validateMiddleDot( fdot1, fdot2, mdot ) )
					{
						listener.onFilteredDot( fdot2 );
						fdot1 = fdot2;
						fdot2 = mdot;
					}
					else
					{
						fdot2 = mdot;
					}
				}
				else
				{
					fdot1 = fdot2;
					fdot2 = mdot;
				}

				fthirdCheck = false;
			}
			else
			{
				if ( validateMiddleDot( fdot1, fdot2, mdot ) )
				{
					listener.onFilteredDot( fdot2 );
					fdot1 = fdot2;
					fdot2 = mdot;
				}
				else
				{
					fdot2 = mdot;
				}
			}

		}
		else if ( DotType.isPenActionUp( mdot.dotType ) )
		{
			// 미들 Dot 검증
			if ( validateMiddleDot( fdot1, fdot2, mdot ) )
			{
				listener.onFilteredDot( fdot2 );
			}
			else
			{

			}
			// 마지막 Dot 검증
			if ( validateEndDot( fdot1, fdot2, mdot ) )
			{
				listener.onFilteredDot( mdot );
			}
			else
			{

			}

			// Dot 및 변수 초기화
			fdot1 = new Fdot();
			fdot2 = new Fdot();
			fsecondCheck = true;
			fthirdCheck = true;
		}
	}

	private boolean validateCode( Fdot dot )
	{
		if ( MAX_NOTE_ID < dot.noteId || MAX_PAGE_ID < dot.pageId )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	// ==============================================
	// 3점을 이용
	// 방향성과 Delta X, Delta Y 세가지를 이용
	// ==============================================

	private boolean validateStartDot( Fdot dot1, Fdot dot2, Fdot dot3 )
	{
		if ( dot1.x > filmSizeX || dot1.x < 1 )
			return false;

		if ( dot1.y > filmSizeY || dot1.y < 1 )
			return false;

		if ( (dot3.x - dot1.x) * (dot2.x - dot1.x) > 0 && Math.abs( dot3.x - dot1.x ) > delta && Math.abs( dot1.x - dot2.x ) > delta )
		{
			return false;
		}
		else if ( (dot3.y - dot1.y) * (dot2.y - dot1.y) > 0 && Math.abs( dot3.y - dot1.y ) > delta && Math.abs( dot1.y - dot2.y ) > delta )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean validateMiddleDot( Fdot dot1, Fdot dot2, Fdot dot3 )
	{
		if ( dot2.x > filmSizeX || dot2.x < 1 )
			return false;

		if ( dot2.y > filmSizeY || dot2.y < 1 )
			return false;

		if ( (dot1.x - dot2.x) * (dot3.x - dot2.x) > 0 && Math.abs( dot1.x - dot2.x ) > delta && Math.abs( dot3.x - dot2.x ) > delta )
		{
			return false;
		}
		else if ( (dot1.y - dot2.y) * (dot3.y - dot2.y) > 0 && Math.abs( dot1.y - dot2.y ) > delta && Math.abs( dot3.y - dot2.y ) > delta )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean validateEndDot( Fdot dot1, Fdot dot2, Fdot dot3 )
	{
		if ( dot3.x > filmSizeX || dot3.x < 1 )
			return false;

		if ( dot3.y > filmSizeY || dot3.y < 1 )
			return false;

		if ( (dot3.x - dot1.x) * (dot3.x - dot2.x) > 0 && Math.abs( dot3.x - dot1.x ) > delta && Math.abs( dot3.x - dot2.x ) > delta )
		{
			return false;
		}
		else if ( (dot3.y - dot1.y) * (dot3.y - dot2.y) > 0 && Math.abs( dot3.y - dot1.y ) > delta && Math.abs( dot3.y - dot2.y ) > delta )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
