// $ANTLR 3.4 /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g 2014-05-20 11:11:54

package org.apache.hadoop.hive.metastore.parser;

import org.apache.hadoop.hive.metastore.parser.ExpressionTree;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.LeafNode;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.Operator;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.LogicalOperator;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FilterParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Digit", "EQUAL", "GREATERTHAN", "GREATERTHANOREQUALTO", "Identifier", "IntLiteral", "KW_AND", "KW_LIKE", "KW_OR", "LESSTHAN", "LESSTHANOREQUALTO", "LPAREN", "Letter", "NOTEQUAL", "RPAREN", "StringLiteral", "WS"
    };

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

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public FilterParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public FilterParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return FilterParser.tokenNames; }
    public String getGrammarFileName() { return "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g"; }


      public ExpressionTree tree = new ExpressionTree();

      public static String TrimQuotes (String input) {
        if (input.length () > 1) {
          if ((input.charAt (0) == '"' && input.charAt (input.length () - 1) == '"')
            || (input.charAt (0) == '\'' && input.charAt (input.length () - 1) == '\'')) {
            return input.substring (1, input.length () - 1);
          }
        }
        return input;
      }



    // $ANTLR start "filter"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:67:1: filter : orExpression ;
    public final void filter() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:68:5: ( orExpression )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:69:5: orExpression
            {
            pushFollow(FOLLOW_orExpression_in_filter83);
            orExpression();

            state._fsp--;


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "filter"



    // $ANTLR start "orExpression"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:72:1: orExpression : andExpression ( KW_OR andExpression )* ;
    public final void orExpression() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:73:5: ( andExpression ( KW_OR andExpression )* )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:74:5: andExpression ( KW_OR andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_orExpression105);
            andExpression();

            state._fsp--;


            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:74:19: ( KW_OR andExpression )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==KW_OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:74:20: KW_OR andExpression
            	    {
            	    match(input,KW_OR,FOLLOW_KW_OR_in_orExpression108); 

            	    pushFollow(FOLLOW_andExpression_in_orExpression110);
            	    andExpression();

            	    state._fsp--;


            	     tree.addIntermediateNode(LogicalOperator.OR); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "orExpression"



    // $ANTLR start "andExpression"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:77:1: andExpression : expression ( KW_AND expression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:78:5: ( expression ( KW_AND expression )* )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:79:5: expression ( KW_AND expression )*
            {
            pushFollow(FOLLOW_expression_in_andExpression136);
            expression();

            state._fsp--;


            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:79:16: ( KW_AND expression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==KW_AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:79:17: KW_AND expression
            	    {
            	    match(input,KW_AND,FOLLOW_KW_AND_in_andExpression139); 

            	    pushFollow(FOLLOW_expression_in_andExpression141);
            	    expression();

            	    state._fsp--;


            	     tree.addIntermediateNode(LogicalOperator.AND); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "andExpression"



    // $ANTLR start "expression"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:82:1: expression : ( LPAREN orExpression RPAREN | operatorExpression );
    public final void expression() throws RecognitionException {
        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:83:5: ( LPAREN orExpression RPAREN | operatorExpression )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==LPAREN) ) {
                alt3=1;
            }
            else if ( ((LA3_0 >= Identifier && LA3_0 <= IntLiteral)||LA3_0==StringLiteral) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:84:5: LPAREN orExpression RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_expression168); 

                    pushFollow(FOLLOW_orExpression_in_expression170);
                    orExpression();

                    state._fsp--;


                    match(input,RPAREN,FOLLOW_RPAREN_in_expression172); 

                    }
                    break;
                case 2 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:86:5: operatorExpression
                    {
                    pushFollow(FOLLOW_operatorExpression_in_expression184);
                    operatorExpression();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "expression"



    // $ANTLR start "operatorExpression"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:89:1: operatorExpression : ( ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) ) ) ;
    public final void operatorExpression() throws RecognitionException {
        Token key=null;
        Token value=null;
        Operator op =null;


         
            boolean isReverseOrder = false;
            Object val = null;

        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:94:5: ( ( ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) ) ) )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:95:5: ( ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) ) )
            {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:95:5: ( ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) ) )
            int alt6=2;
            switch ( input.LA(1) ) {
            case Identifier:
                {
                int LA6_1 = input.LA(2);

                if ( ((LA6_1 >= EQUAL && LA6_1 <= GREATERTHANOREQUALTO)||LA6_1==KW_LIKE||(LA6_1 >= LESSTHAN && LA6_1 <= LESSTHANOREQUALTO)||LA6_1==NOTEQUAL) ) {
                    int LA6_4 = input.LA(3);

                    if ( (LA6_4==StringLiteral) ) {
                        alt6=1;
                    }
                    else if ( (LA6_4==IntLiteral) ) {
                        alt6=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 4, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;

                }
                }
                break;
            case StringLiteral:
                {
                alt6=1;
                }
                break;
            case IntLiteral:
                {
                alt6=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:96:8: ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) )
                    {
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:96:8: ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==Identifier) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==StringLiteral) ) {
                        alt4=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;

                    }
                    switch (alt4) {
                        case 1 :
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:97:9: (key= Identifier op= operator value= StringLiteral )
                            {
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:97:9: (key= Identifier op= operator value= StringLiteral )
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:97:10: key= Identifier op= operator value= StringLiteral
                            {
                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_operatorExpression235); 

                            pushFollow(FOLLOW_operator_in_operatorExpression241);
                            op=operator();

                            state._fsp--;


                            value=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_operatorExpression248); 

                            }


                            }
                            break;
                        case 2 :
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:99:9: (value= StringLiteral op= operator key= Identifier )
                            {
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:99:9: (value= StringLiteral op= operator key= Identifier )
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:99:10: value= StringLiteral op= operator key= Identifier
                            {
                            value=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_operatorExpression274); 

                            pushFollow(FOLLOW_operator_in_operatorExpression281);
                            op=operator();

                            state._fsp--;


                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_operatorExpression287); 

                            }


                             isReverseOrder = true; 

                            }
                            break;

                    }


                     val = TrimQuotes(value.getText()); 

                    }
                    break;
                case 2 :
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:102:8: ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) )
                    {
                    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:102:8: ( (key= Identifier op= operator value= IntLiteral ) | (value= IntLiteral op= operator key= Identifier ) )
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==Identifier) ) {
                        alt5=1;
                    }
                    else if ( (LA5_0==IntLiteral) ) {
                        alt5=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        throw nvae;

                    }
                    switch (alt5) {
                        case 1 :
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:103:9: (key= Identifier op= operator value= IntLiteral )
                            {
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:103:9: (key= Identifier op= operator value= IntLiteral )
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:103:10: key= Identifier op= operator value= IntLiteral
                            {
                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_operatorExpression334); 

                            pushFollow(FOLLOW_operator_in_operatorExpression340);
                            op=operator();

                            state._fsp--;


                            value=(Token)match(input,IntLiteral,FOLLOW_IntLiteral_in_operatorExpression346); 

                            }


                            }
                            break;
                        case 2 :
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:105:9: (value= IntLiteral op= operator key= Identifier )
                            {
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:105:9: (value= IntLiteral op= operator key= Identifier )
                            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:105:10: value= IntLiteral op= operator key= Identifier
                            {
                            value=(Token)match(input,IntLiteral,FOLLOW_IntLiteral_in_operatorExpression372); 

                            pushFollow(FOLLOW_operator_in_operatorExpression378);
                            op=operator();

                            state._fsp--;


                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_operatorExpression384); 

                            }


                             isReverseOrder = true; 

                            }
                            break;

                    }


                     val = Integer.parseInt(value.getText()); 

                    }
                    break;

            }



                    LeafNode node = new LeafNode();
                    node.keyName = key.getText();
                    node.value = val;
                    node.operator = op;
                    node.isReverseOrder = isReverseOrder;

                    tree.addLeafNode(node);
                

            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "operatorExpression"



    // $ANTLR start "operator"
    // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:118:1: operator returns [Operator op] : t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL ) ;
    public final Operator operator() throws RecognitionException {
        Operator op = null;


        Token t=null;

        try {
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:119:4: (t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL ) )
            // /home/leejy/work/hive-0.10.0-cdh4.2.0/src/metastore/src/java/org/apache/hadoop/hive/metastore/parser/Filter.g:120:4: t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL )
            {
            t=(Token)input.LT(1);

            if ( (input.LA(1) >= EQUAL && input.LA(1) <= GREATERTHANOREQUALTO)||input.LA(1)==KW_LIKE||(input.LA(1) >= LESSTHAN && input.LA(1) <= LESSTHANOREQUALTO)||input.LA(1)==NOTEQUAL ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }



                  op = Operator.fromString(t.getText().toUpperCase());
               

            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return op;
    }
    // $ANTLR end "operator"

    // Delegated rules


 

    public static final BitSet FOLLOW_orExpression_in_filter83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression105 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_KW_OR_in_orExpression108 = new BitSet(new long[]{0x0000000000088300L});
    public static final BitSet FOLLOW_andExpression_in_orExpression110 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_expression_in_andExpression136 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_AND_in_andExpression139 = new BitSet(new long[]{0x0000000000088300L});
    public static final BitSet FOLLOW_expression_in_andExpression141 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_LPAREN_in_expression168 = new BitSet(new long[]{0x0000000000088300L});
    public static final BitSet FOLLOW_orExpression_in_expression170 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAREN_in_expression172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorExpression_in_expression184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_operatorExpression235 = new BitSet(new long[]{0x00000000000268E0L});
    public static final BitSet FOLLOW_operator_in_operatorExpression241 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_StringLiteral_in_operatorExpression248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_operatorExpression274 = new BitSet(new long[]{0x00000000000268E0L});
    public static final BitSet FOLLOW_operator_in_operatorExpression281 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_Identifier_in_operatorExpression287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_operatorExpression334 = new BitSet(new long[]{0x00000000000268E0L});
    public static final BitSet FOLLOW_operator_in_operatorExpression340 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_IntLiteral_in_operatorExpression346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IntLiteral_in_operatorExpression372 = new BitSet(new long[]{0x00000000000268E0L});
    public static final BitSet FOLLOW_operator_in_operatorExpression378 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_Identifier_in_operatorExpression384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_operator432 = new BitSet(new long[]{0x0000000000000002L});

}