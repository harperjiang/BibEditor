/**
 * Lexer for LaTeX
 */
package hao.texdojo.latexeditor.parser;

import hao.texdojo.latexeditor.model.*;
%%
%class LaTeXParser
%public
%unicode
%line
%function scan
%type LaTeXNode

%{

	public LaTeXParser(String content) {
		this(new java.io.StringReader(content));
	}

      StringBuilder buffer = new StringBuilder();
%}

LineTerminator = \r|\n|\r\n
Whitespace     = [ \t\f]+
Begin		   = \\"begin"
End			   = \\"end"
Command		   = \\[^ \t\r\n\{\[\]\}\%\\]+

Text		   = [^\\\{\[\]\]\%]+

%state OPTION
%state ARGS
%state COMMENT

%%

<YYINITIAL> {
"{"					{yybegin(ARGS); buffer = new StringBuilder();}
"%"					{yybegin(COMMENT); buffer = new StringBuilder();}
"["					{yybegin(OPTION); buffer = new StringBuilder();}
{Begin}				{return new BeginNode(null, 0, yytext().length(), yyline);}
{End}				{return new EndNode(null, 0, yytext().length(), yyline);}
{Command}			{return new CommandNode(yytext().substring(1), 0, yytext().length(), yyline);}
{Text}    			{return new TextNode(yytext(),0,yytext().length(), yyline);}
}

<OPTION> {
"]"					{yybegin(YYINITIAL);return new OptionNode(buffer.toString(),0,buffer.length(), yyline);}
{LineTerminator}    {buffer.append(yytext());}
.					{buffer.append(yytext());}
}

<ARGS> {
"}"					{yybegin(YYINITIAL);return new ArgNode(buffer.toString(),0,buffer.length(), yyline);}
{LineTerminator}    {buffer.append(yytext());}
.					{buffer.append(yytext());}
}

<COMMENT> {
{LineTerminator}	{yybegin(YYINITIAL);return new CommentNode(buffer.toString(), 0 , buffer.length(), yyline);}
.					{buffer.append(yytext());}
}

.                   {return new TextNode(yytext(),0,yytext().length(), yyline);}
