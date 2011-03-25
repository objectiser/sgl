grammar ScribbleLang;

options {
	output=AST;
	backtrack=true;
	k=2;
}

tokens {
	INTERACTION = 'interaction' ;
	PLUS 	= '+' ;
	MINUS	= '-' ;
	MULT	= '*' ;
	DIV	= '/' ;
	FULLSTOP = '.' ;
	LT = '<' ;
	GT = '>' ;
}

@header {
package org.scribble.lang.parser.antlr;
}
   
@lexer::header {
package org.scribble.lang.parser.antlr;
}
   
@members {
	private org.scribble.common.logging.Journal m_journal=null;
	private String m_document=null;
	
	public static void main(String[] args) throws Exception {
        ScribbleLangLexer lex = new ScribbleLangLexer(new ANTLRFileStream(args[0]));
       	CommonTokenStream tokens = new CommonTokenStream(lex);

		ScribbleLangParser parser = new ScribbleLangParser(tokens);

		//LangTreeAdaptor adaptor=new LangTreeAdaptor();
		//adaptor.setParser(parser);
		
		//parser.setTreeAdaptor(adaptor);
		
        try {
            ScribbleLangParser.description_return r=parser.description();
            
            CommonTree t=(CommonTree)r.getTree();
            
            //Tree t=(Tree)r.getTree();
            
            System.out.println(t.toStringTree());
            
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }
    }
    
    public void setJournal(org.scribble.common.logging.Journal journal) {
    	m_journal = journal;
    }
    
    public void setDocument(String doc) {
    	m_document = doc;
    }
    
    public void emitErrorMessage(String mesg) {
    	if (m_journal == null) {
    		super.emitErrorMessage(mesg);
    	} else {
    		m_journal.error(ANTLRMessageUtil.getMessageText(mesg),
    					ANTLRMessageUtil.getProperties(mesg, m_document));
    	}
    }
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

description: namespaceStatement ( importStatement | requiresStatement )* langUnit ;

namespaceStatement: 'namespace'^ qualifiedName ';'! ;

protocolDef: qualifiedName '@' simpleName ;

qualifiedName: ID ( '.' ID )* ;

importStatement: 'import'^ metaQualifiedName ';'! ;

metaQualifiedName: ID ( '.' ( metaQualifiedName | '*' ) )? ;

requiresStatement: 'requires'^ simpleName requiresParam ( ','! requiresParam )* ';'! ;

requiresParam: ID '=' StringLiteral ;

langUnit: ID '('! ( actorDef ( ','! actorDef )* )? ')'! blockDef ;

actorDef: simpleName ( ( 'implements' protocolDef ( 'with' actorParam ( ','! actorParam )* )? |
					'as' simpleName ) )? ;
					
actorRef: simpleName ;

actorParam: ID '=' expressionDef ;

typeDef: qualifiedName ( '<'! typeDef ( ','! typeDef )* '>'! )? | primitiveType ;

simpleName: ID ;

blockDef: '{'! activityDef '}'! ;

activityDef: ( ( ( actorsList | interactionDef | recurCallDef | 
					variableDef | exprDef ) ';'! ) 
			| ifDef | parDef | choiceDef | whileDef | recurBlockDef )* ;

/* assignDef |  */

actorsList: 'actor'^ actorDef ( ','! actorDef )* ;

interactionDef: actorFromExpressionDef '->' actorToVariableDef ;

actorFromExpressionDef: actorRef ( ':'! expressionDef )* ;

actorToVariableDef: actorRef ( ':'! simpleName )* ;

stateAccessorDef: simpleName ;

// assignDef: actorToVariableDef '=' expressionDef ;

stateDef: ( actorRef ':'! )? simpleName ( '=' expressionDef )? ;

variableDef: typeDef stateDef ( ','! stateDef )* ;

ifDef: 'if'^ '('! actorRef ':'! expressionDef ')'! blockDef ( elseIfDef )* ( elseDef )? ;

elseIfDef: 'elseif'^ '('! expressionDef ')'! blockDef ;

elseDef: 'else'^ blockDef ;

expressionDef: expression ;

exprDef: actorRef ':'! expressionDef ;

parDef: 'par'^ concurrentPathDef ( 'and'! concurrentPathDef ) ;

concurrentPathDef: blockDef ;

choiceDef: 'choice'^ '{'! ( whenDef )* '}'! ;

whenDef: interactionDef blockDef ;

whileDef: 'while'^ '('! expressionDef ')'! blockDef ;

recurBlockDef: simpleName ':'! blockDef ;

recurCallDef: simpleName;

/*------------------------------------------------------------------
 * EXPRESSIONS 
 * Java grammer can be found at http://www.antlr.org/grammar/1152141644268/Java.g
 *------------------------------------------------------------------*/

expression
    :   conditionalExpression (assignmentOperator expression)?
    ;
    
parExpression
    :   '(' expression ')'
    ;
    
expressionList
    :   expression (',' expression)*
    ;

type
	:	classOrInterfaceType ('[' ']')*
	|	primitiveType ('[' ']')*
	;

classOrInterfaceType
	:	ID typeArguments? ('.' ID typeArguments? )*
	;

literal 
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    |   'null'
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   'true'
    |   'false'
    ;

primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

assignmentOperator
    :   '='
    |   '+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |   ('<' '<' '=')=> t1='<' t2='<' t3='=' 
        { $t1.getLine() == $t2.getLine() &&
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   ('>' '>' '>' '=')=> t1='>' t2='>' t3='>' t4='='
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&
          $t3.getLine() == $t4.getLine() && 
          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() }?
    |   ('>' '>' '=')=> t1='>' t2='>' t3='='
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    ;

conditionalExpression
    :   conditionalOrExpression ( '?' expression ':' expression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( '||' conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( '&&' inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( '|' exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( '^' andExpression )*
    ;

andExpression
    :   equalityExpression ( '&' equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( ('==' | '!=') instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression ('instanceof' type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
    ;
    
relationalOp
    :   ('<' '=')=> t1='<' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   ('>' '=')=> t1='>' t2='=' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   '<' 
    |   '>' 
    ;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
    ;

shiftOp
    :   ('<' '<')=> t1='<' t2='<' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   ('>' '>' '>')=> t1='>' t2='>' t3='>' 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   ('>' '>')=> t1='>' t2='>'
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    ;


additiveExpression
    :   multiplicativeExpression ( ('+' | '-') multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
    ;
    
unaryExpression
    :   '+' unaryExpression
    |   '-' unaryExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    |   '!' unaryExpression
    |   castExpression
    |   primary selector* ('++'|'--')?
    ;

castExpression
    :  '(' primitiveType ')' unaryExpression
    |  '(' (type | expression) ')' unaryExpressionNotPlusMinus
    ;

primary
    :   parExpression
    |   'this' ('.' ID)* identifierSuffix?
    |   'super' superSuffix
    |   literal
    |   'new' creator
    |   ID ('.' ID)* identifierSuffix?
    |   primitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    ;

identifierSuffix
    :   ('[' ']')+ '.' 'class'
    |   ('[' expression ']')+ // can also be matched by selector, but do here
    |   arguments
    |   '.' 'class'
    |   '.' explicitGenericInvocation
    |   '.' 'this'
    |   '.' 'super' arguments
    |   '.' 'new' innerCreator
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   classOrInterfaceType
    |   primitiveType
    ;
    
innerCreator
    :   nonWildcardTypeArguments? ID classCreatorRest
    ;

arrayCreatorRest
    :   '[' expression ']' ('[' expression ']')* ('[' ']')*
    ;

classCreatorRest
    :   arguments
    ;
    
explicitGenericInvocation
    :   nonWildcardTypeArguments ID arguments
    ;
    
nonWildcardTypeArguments
    :   '<' typeList '>'
    ;
    
selector
    :   '.' ID arguments?
    |   '.' 'this'
    |   '.' 'super' superSuffix
    |   '.' 'new' innerCreator
    |   '[' expression ']'
    ;
    
superSuffix
    :   arguments
    |   '.' ID arguments?
    ;

arguments
    :   '(' expressionList? ')'
    ;
    
typeList
    :   type (',' type)*
    ;
    
typeArguments
    :   '<' typeArgument (',' typeArgument)* '>'
    ;
    
typeArgument
    :   type
    |   '?' (('extends' | 'super') type)?
    ;
 

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

ID : ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;

//NUMBER	: (DIGIT)+ ;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ 	{ $channel = HIDDEN; } ;

//fragment DIGIT	: '0'..'9' ;

ML_COMMENT
    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT : '//' (options {greedy=false;} : .)* '\n' {$channel=HIDDEN;} ;



HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;
