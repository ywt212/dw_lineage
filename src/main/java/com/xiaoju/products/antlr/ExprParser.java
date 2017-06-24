package com.xiaoju.products.antlr;
import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ExprParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "INT", "NEWLINE", "WS", 
		"'('", "')'", "'*'", "'+'", "'-'", "'/'"
	};
	public static final int EOF=-1;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int T__11=11;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int ID=4;
	public static final int INT=5;
	public static final int NEWLINE=6;
	public static final int WS=7;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public ExprParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public ExprParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return ExprParser.tokenNames; }
	@Override public String getGrammarFileName() { return "E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g"; }



	// $ANTLR start "prog"
	// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:3:1: prog : stat ;
	public final void prog() throws RecognitionException {
		try {
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:3:5: ( stat )
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:3:7: stat
			{
			pushFollow(FOLLOW_stat_in_prog10);
			stat();
			state._fsp--;

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "prog"



	// $ANTLR start "stat"
	// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:4:2: stat : ( expr | NEWLINE );
	public final void stat() throws RecognitionException {
		try {
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:4:6: ( expr | NEWLINE )
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( ((LA1_0 >= ID && LA1_0 <= INT)||LA1_0==8) ) {
				alt1=1;
			}
			else if ( (LA1_0==NEWLINE) ) {
				alt1=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:4:8: expr
					{
					pushFollow(FOLLOW_expr_in_stat20);
					expr();
					state._fsp--;

					}
					break;
				case 2 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:5:5: NEWLINE
					{
					match(input,NEWLINE,FOLLOW_NEWLINE_in_stat27); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "stat"



	// $ANTLR start "expr"
	// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:7:2: expr : multExpr ( ( '+' | '-' ) multExpr )* ;
	public final void expr() throws RecognitionException {
		try {
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:7:7: ( multExpr ( ( '+' | '-' ) multExpr )* )
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:7:9: multExpr ( ( '+' | '-' ) multExpr )*
			{
			pushFollow(FOLLOW_multExpr_in_expr39);
			multExpr();
			state._fsp--;

			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:7:18: ( ( '+' | '-' ) multExpr )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= 11 && LA2_0 <= 12)) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:7:19: ( '+' | '-' ) multExpr
					{
					if ( (input.LA(1) >= 11 && input.LA(1) <= 12) ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_multExpr_in_expr48);
					multExpr();
					state._fsp--;

					}
					break;

				default :
					break loop2;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "expr"



	// $ANTLR start "multExpr"
	// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:8:2: multExpr : atom ( ( '*' | '/' ) atom )* ;
	public final void multExpr() throws RecognitionException {
		try {
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:8:11: ( atom ( ( '*' | '/' ) atom )* )
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:8:13: atom ( ( '*' | '/' ) atom )*
			{
			pushFollow(FOLLOW_atom_in_multExpr61);
			atom();
			state._fsp--;

			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:8:18: ( ( '*' | '/' ) atom )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==10||LA3_0==13) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:8:19: ( '*' | '/' ) atom
					{
					if ( input.LA(1)==10||input.LA(1)==13 ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_atom_in_multExpr70);
					atom();
					state._fsp--;

					}
					break;

				default :
					break loop3;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "multExpr"



	// $ANTLR start "atom"
	// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:9:2: atom : ( '(' expr ')' | INT | ID );
	public final void atom() throws RecognitionException {
		try {
			// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:9:6: ( '(' expr ')' | INT | ID )
			int alt4=3;
			switch ( input.LA(1) ) {
			case 8:
				{
				alt4=1;
				}
				break;
			case INT:
				{
				alt4=2;
				}
				break;
			case ID:
				{
				alt4=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}
			switch (alt4) {
				case 1 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:9:9: '(' expr ')'
					{
					match(input,8,FOLLOW_8_in_atom83); 
					pushFollow(FOLLOW_expr_in_atom85);
					expr();
					state._fsp--;

					match(input,9,FOLLOW_9_in_atom87); 
					}
					break;
				case 2 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:10:10: INT
					{
					match(input,INT,FOLLOW_INT_in_atom99); 
					}
					break;
				case 3 :
					// E:\\workspace\\sql_parse\\src\\main\\java\\com\\xiaoju\\products\\antlr\\Expr.g:11:7: ID
					{
					match(input,ID,FOLLOW_ID_in_atom109); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "atom"

	// Delegated rules



	public static final BitSet FOLLOW_stat_in_prog10 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_stat20 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEWLINE_in_stat27 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multExpr_in_expr39 = new BitSet(new long[]{0x0000000000001802L});
	public static final BitSet FOLLOW_set_in_expr42 = new BitSet(new long[]{0x0000000000000130L});
	public static final BitSet FOLLOW_multExpr_in_expr48 = new BitSet(new long[]{0x0000000000001802L});
	public static final BitSet FOLLOW_atom_in_multExpr61 = new BitSet(new long[]{0x0000000000002402L});
	public static final BitSet FOLLOW_set_in_multExpr64 = new BitSet(new long[]{0x0000000000000130L});
	public static final BitSet FOLLOW_atom_in_multExpr70 = new BitSet(new long[]{0x0000000000002402L});
	public static final BitSet FOLLOW_8_in_atom83 = new BitSet(new long[]{0x0000000000000130L});
	public static final BitSet FOLLOW_expr_in_atom85 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_9_in_atom87 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_atom99 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_atom109 = new BitSet(new long[]{0x0000000000000002L});
}
