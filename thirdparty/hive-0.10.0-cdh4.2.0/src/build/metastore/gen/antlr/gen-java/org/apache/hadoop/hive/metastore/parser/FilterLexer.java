// $ANTLR 3.4 /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g 2014-05-20 11:11:54
package org.apache.hadoop.hive.metastore.parser;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FilterLexer extends Lexer {
    public static final int EOF=-1;
    public static final int Digit=4;
    public static final int EQUAL=5;
    public static final int GREATERTHAN=6;
    public static final int GREATERTHANOREQUALTO=7;
    public static final int Identifier=8;
    public static final int IntLiteral=9;
    public static final int KW_AND=10;
    public static final int KW_LIKE=11;
    public static final int KW_OR=12;
    public static final int LESSTHAN=13;
    public static final int LESSTHANOREQUALTO=14;
    public static final int LPAREN=15;
    public static final int Letter=16;
    public static final int NOTEQUAL=17;
    public static final int RPAREN=18;
    public static final int StringLiteral=19;
    public static final int WS=20;

      public String errorMsg;

      @Override
      public void emitErrorMessage(String msg) {
        // save for caller to detect invalid filter
    	errorMsg = msg;
      }


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public FilterLexer() {} 
    public FilterLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FilterLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g"; }

    // $ANTLR start "KW_AND"
    public final void mKW_AND() throws RecognitionException {
        try {
            int _type = KW_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:126:8: ( 'AND' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:126:10: 'AND'
            {
            match("AND"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_AND"

    // $ANTLR start "KW_OR"
    public final void mKW_OR() throws RecognitionException {
        try {
            int _type = KW_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:127:7: ( 'OR' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:127:9: 'OR'
            {
            match("OR"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_OR"

    // $ANTLR start "KW_LIKE"
    public final void mKW_LIKE() throws RecognitionException {
        try {
            int _type = KW_LIKE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:128:9: ( 'LIKE' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:128:11: 'LIKE'
            {
            match("LIKE"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_LIKE"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:131:8: ( '(' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:131:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:132:8: ( ')' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:132:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "EQUAL"
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:133:7: ( '=' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:133:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUAL"

    // $ANTLR start "NOTEQUAL"
    public final void mNOTEQUAL() throws RecognitionException {
        try {
            int _type = NOTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:134:10: ( '<>' | '!=' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='<') ) {
                alt1=1;
            }
            else if ( (LA1_0=='!') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:134:12: '<>'
                    {
                    match("<>"); 



                    }
                    break;
                case 2 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:134:19: '!='
                    {
                    match("!="); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQUAL"

    // $ANTLR start "LESSTHANOREQUALTO"
    public final void mLESSTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = LESSTHANOREQUALTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:135:19: ( '<=' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:135:21: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESSTHANOREQUALTO"

    // $ANTLR start "LESSTHAN"
    public final void mLESSTHAN() throws RecognitionException {
        try {
            int _type = LESSTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:136:10: ( '<' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:136:12: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESSTHAN"

    // $ANTLR start "GREATERTHANOREQUALTO"
    public final void mGREATERTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = GREATERTHANOREQUALTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:137:22: ( '>=' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:137:24: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATERTHANOREQUALTO"

    // $ANTLR start "GREATERTHAN"
    public final void mGREATERTHAN() throws RecognitionException {
        try {
            int _type = GREATERTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:138:13: ( '>' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:138:15: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATERTHAN"

    // $ANTLR start "Letter"
    public final void mLetter() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:144:5: ( 'a' .. 'z' | 'A' .. 'Z' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Letter"

    // $ANTLR start "Digit"
    public final void mDigit() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:149:5: ( '0' .. '9' )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Digit"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:153:5: ( ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' ) )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )
            {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\'') ) {
                alt4=1;
            }
            else if ( (LA4_0=='\"') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:7: '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\''
                    {
                    match('\''); 

                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:12: (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )*
                    loop2:
                    do {
                        int alt2=3;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0 >= '\u0000' && LA2_0 <= '&')||(LA2_0 >= '(' && LA2_0 <= '[')||(LA2_0 >= ']' && LA2_0 <= '\uFFFF')) ) {
                            alt2=1;
                        }
                        else if ( (LA2_0=='\\') ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:14: ~ ( '\\'' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:29: ( '\\\\' . )
                    	    {
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:29: ( '\\\\' . )
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:154:30: '\\\\' .
                    	    {
                    	    match('\\'); 

                    	    matchAny(); 

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    match('\''); 

                    }
                    break;
                case 2 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:7: '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"'
                    {
                    match('\"'); 

                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:12: (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )*
                    loop3:
                    do {
                        int alt3=3;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= '\u0000' && LA3_0 <= '!')||(LA3_0 >= '#' && LA3_0 <= '[')||(LA3_0 >= ']' && LA3_0 <= '\uFFFF')) ) {
                            alt3=1;
                        }
                        else if ( (LA3_0=='\\') ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:14: ~ ( '\\\"' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:29: ( '\\\\' . )
                    	    {
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:29: ( '\\\\' . )
                    	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:155:30: '\\\\' .
                    	    {
                    	    match('\\'); 

                    	    matchAny(); 

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    match('\"'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "IntLiteral"
    public final void mIntLiteral() throws RecognitionException {
        try {
            int _type = IntLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:161:5: ( ( Digit )+ )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:162:5: ( Digit )+
            {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:162:5: ( Digit )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IntLiteral"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:166:5: ( ( Letter | Digit ) ( Letter | Digit | '_' )* )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:167:5: ( Letter | Digit ) ( Letter | Digit | '_' )*
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:167:22: ( Letter | Digit | '_' )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= '0' && LA6_0 <= '9')||(LA6_0 >= 'A' && LA6_0 <= 'Z')||LA6_0=='_'||(LA6_0 >= 'a' && LA6_0 <= 'z')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Identifier"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:170:5: ( ( ' ' | '\\r' | '\\t' | '\\n' )+ )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:170:9: ( ' ' | '\\r' | '\\t' | '\\n' )+
            {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:170:9: ( ' ' | '\\r' | '\\t' | '\\n' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0 >= '\t' && LA7_0 <= '\n')||LA7_0=='\r'||LA7_0==' ') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:8: ( KW_AND | KW_OR | KW_LIKE | LPAREN | RPAREN | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | StringLiteral | IntLiteral | Identifier | WS )
        int alt8=15;
        alt8 = dfa8.predict(input);
        switch (alt8) {
            case 1 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:10: KW_AND
                {
                mKW_AND(); 


                }
                break;
            case 2 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:17: KW_OR
                {
                mKW_OR(); 


                }
                break;
            case 3 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:23: KW_LIKE
                {
                mKW_LIKE(); 


                }
                break;
            case 4 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:31: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 5 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:38: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 6 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:45: EQUAL
                {
                mEQUAL(); 


                }
                break;
            case 7 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:51: NOTEQUAL
                {
                mNOTEQUAL(); 


                }
                break;
            case 8 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:60: LESSTHANOREQUALTO
                {
                mLESSTHANOREQUALTO(); 


                }
                break;
            case 9 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:78: LESSTHAN
                {
                mLESSTHAN(); 


                }
                break;
            case 10 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:87: GREATERTHANOREQUALTO
                {
                mGREATERTHANOREQUALTO(); 


                }
                break;
            case 11 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:108: GREATERTHAN
                {
                mGREATERTHAN(); 


                }
                break;
            case 12 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:120: StringLiteral
                {
                mStringLiteral(); 


                }
                break;
            case 13 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:134: IntLiteral
                {
                mIntLiteral(); 


                }
                break;
            case 14 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:145: Identifier
                {
                mIdentifier(); 


                }
                break;
            case 15 :
                // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:1:156: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA8_eotS =
        "\1\uffff\3\14\3\uffff\1\22\1\uffff\1\24\1\uffff\1\25\2\uffff\1\14"+
        "\1\30\1\14\5\uffff\1\25\1\32\1\uffff\1\14\1\uffff\1\34\1\uffff";
    static final String DFA8_eofS =
        "\35\uffff";
    static final String DFA8_minS =
        "\1\11\1\116\1\122\1\111\3\uffff\1\75\1\uffff\1\75\1\uffff\1\60\2"+
        "\uffff\1\104\1\60\1\113\5\uffff\2\60\1\uffff\1\105\1\uffff\1\60"+
        "\1\uffff";
    static final String DFA8_maxS =
        "\1\172\1\116\1\122\1\111\3\uffff\1\76\1\uffff\1\75\1\uffff\1\172"+
        "\2\uffff\1\104\1\172\1\113\5\uffff\2\172\1\uffff\1\105\1\uffff\1"+
        "\172\1\uffff";
    static final String DFA8_acceptS =
        "\4\uffff\1\4\1\5\1\6\1\uffff\1\7\1\uffff\1\14\1\uffff\1\16\1\17"+
        "\3\uffff\1\10\1\11\1\12\1\13\1\15\2\uffff\1\2\1\uffff\1\1\1\uffff"+
        "\1\3";
    static final String DFA8_specialS =
        "\35\uffff}>";
    static final String[] DFA8_transitionS = {
            "\2\15\2\uffff\1\15\22\uffff\1\15\1\10\1\12\4\uffff\1\12\1\4"+
            "\1\5\6\uffff\12\13\2\uffff\1\7\1\6\1\11\2\uffff\1\1\12\14\1"+
            "\3\2\14\1\2\13\14\6\uffff\32\14",
            "\1\16",
            "\1\17",
            "\1\20",
            "",
            "",
            "",
            "\1\21\1\10",
            "",
            "\1\23",
            "",
            "\12\26\7\uffff\32\14\4\uffff\1\14\1\uffff\32\14",
            "",
            "",
            "\1\27",
            "\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32\14",
            "\1\31",
            "",
            "",
            "",
            "",
            "",
            "\12\26\7\uffff\32\14\4\uffff\1\14\1\uffff\32\14",
            "\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32\14",
            "",
            "\1\33",
            "",
            "\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32\14",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( KW_AND | KW_OR | KW_LIKE | LPAREN | RPAREN | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | StringLiteral | IntLiteral | Identifier | WS );";
        }
    }
 

}