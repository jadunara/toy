package kr.faz.app.sql.service;

import java.util.*;

/**
 * Performs formatting of basic SQL statements (DML + query).
 *
 * @author Gavin King
 * @author Steve Ebersole
 * hibernate code modify....
 */
public class BasicFormatterImpl {

    private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
    private static final Set<String> END_CLAUSES = new HashSet<String>();
    private static final Set<String> LOGICAL = new HashSet<String>();
    private static final Set<String> QUANTIFIERS = new HashSet<String>();
    private static final Set<String> DML = new HashSet<String>();
    private static final Set<String> MISC = new HashSet<String>();
    private static final Set<String> MISC_UPPER_CASE = new HashSet<String>();
    public static final String WHITESPACE = " \n\r\f\t";

    static {
        BEGIN_CLAUSES.add("left");
        BEGIN_CLAUSES.add("right");
        BEGIN_CLAUSES.add("inner");
        BEGIN_CLAUSES.add("outer");
        BEGIN_CLAUSES.add("group");
        BEGIN_CLAUSES.add("order");



        END_CLAUSES.add("where");
        END_CLAUSES.add("set");
        END_CLAUSES.add("having");
        END_CLAUSES.add("join");
        END_CLAUSES.add("from");
        END_CLAUSES.add("by");
        END_CLAUSES.add("join");
        END_CLAUSES.add("into");
        END_CLAUSES.add("union");

        LOGICAL.add("and");
        LOGICAL.add("or");
        LOGICAL.add("when");
        LOGICAL.add("else");
        LOGICAL.add("end");
        LOGICAL.add("like");

        QUANTIFIERS.add("in");
        QUANTIFIERS.add("all");
        QUANTIFIERS.add("exists");
        QUANTIFIERS.add("some");
        QUANTIFIERS.add("any");

        DML.add("insert");
        DML.add("update");
        DML.add("merge");
        DML.add("delete");

        MISC.add("select");
        MISC.add("on");

        MISC_UPPER_CASE.add("desc");
        MISC_UPPER_CASE.add("asc");
        MISC_UPPER_CASE.add("between");
        MISC_UPPER_CASE.add("case");
        MISC_UPPER_CASE.add("as");
        MISC_UPPER_CASE.add("is");
        MISC_UPPER_CASE.add("not");
        MISC_UPPER_CASE.add("null");
        MISC_UPPER_CASE.add("on");
        MISC_UPPER_CASE.add("over");
        MISC_UPPER_CASE.add("count");
        MISC_UPPER_CASE.add("max");
        MISC_UPPER_CASE.add("min");
//        MISC_UPPER_CASE.add("");
//        MISC_UPPER_CASE.add("");
//        MISC_UPPER_CASE.add("");
//        MISC_UPPER_CASE.add("");
//        MISC_UPPER_CASE.add("");
//        MISC_UPPER_CASE.add("");

    }

    private static final String INDENT_STRING = "    ";
    private static final String INITIAL = System.lineSeparator() + INDENT_STRING;


    public String format(String source) {
        return new FormatProcess(source).perform();
    }

    private static class FormatProcess {
        boolean beginLine = true;
        boolean afterBeginBeforeEnd;
        boolean afterByOrSetOrFromOrSelect;
        boolean afterValues;
        boolean afterOn;
        boolean afterBetween;
        boolean afterInsert;
        int inFunction;
        int parensSinceSelect;
        private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
        private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

        int indent = 1;

        StringBuilder result = new StringBuilder();
        StringTokenizer tokens;
        String lastToken;
        String token;
        /****
         * 토큰의 소문자.
         */
        String lcToken;

        public FormatProcess(String sql) {
            tokens = new StringTokenizer(
                    sql,
                    "()+*/-=<>'`\"[]," + WHITESPACE,
                    true
            );
        }

        public String perform() {

            //result.append(INITIAL);

            while (tokens.hasMoreTokens()) {
                token = tokens.nextToken();
                lcToken = token.toLowerCase(Locale.ROOT);

                if ("'".equals(token)) {
                    String t;
                    do {
                        t = tokens.nextToken();
                        token += t;
                    }
                    // cannot handle single quotes
                    while (!"'".equals(t) && tokens.hasMoreTokens());
                } else if ("\"".equals(token)) {
                    String t;
                    do {
                        t = tokens.nextToken();
                        token += t;
                    }
                    while (!"\"".equals(t));
                }

                if ( "select".equals(lcToken)   ) {
                	int a = 0 ;
                	a++;
                	System.out.println(a);
                }

                if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
                    commaAfterByOrFromOrSelect();
                } else if (afterOn && ",".equals(token)) {
                    commaAfterOn();
                } else if ("(".equals(token)) {
                    openParen();
                } else if (")".equals(token)) {
                    closeParen();
                } else if (BEGIN_CLAUSES.contains(lcToken)) {
                    beginNewClause();
                } else if (END_CLAUSES.contains(lcToken)) {
                    endNewClause();
                } else if ("select".equals(lcToken)) {
                    select();
                } else if (DML.contains(lcToken)) {
                    updateOrInsertOrDelete();
                } else if ("values".equals(lcToken)) {
                    values();
                } else if ("on".equals(lcToken)) {
                    on();
                } else if (afterBetween && lcToken.equals("and")) {
                    misc();
                    afterBetween = false;
                } else if (LOGICAL.contains(lcToken)) {
                    logical();
                } else if (isWhitespace(token)) {
                    white();
                } else {
                    misc();
                }

                if (!isWhitespace(token)) {
                    lastToken = lcToken;
                }

            }
            return result.toString();
        }

        private void commaAfterOn() {
            out();
            indent--;
            newline();
            afterOn = false;
            afterByOrSetOrFromOrSelect = true;
        }

        private void commaAfterByOrFromOrSelect() {
        	newline();
            out();
        }

        private void logical() {
            if ("end".equals(lcToken)) {
                indent--;
                newline();
            } else if ( "and".equals( lcToken ) ) {
            	if ( afterOn ) {
            		newline();
            	} else {
            		indent++;
            		newline();
            		indent--;
            	}
            } else if ( "like".equals( lcToken ) ) {
            	//아무것도 하지 않는다.
            } else {
                newline();
            }
            if ( "when".equals( lcToken) || "else".equals( lcToken) ) {
            	appendWhiteSpace(7+2);// n번 space 로 밀어준다.
            }
            out(true);
            beginLine = false;
        }

        private void on() {
            afterOn = true;
            newline();
            appendWhiteSpace( 1  );//n번 space 로 밀어준다.
            out(true);
            beginLine = false;
            indent++;
        }
        private void appendWhiteSpace(int loopCount) {
    		for ( int i = 0; i <= loopCount ; i++ )
    			result.append(" ");
        }
        /***
         * 기타에 대하여 출력
         */
        private void misc() {
        	if ( MISC_UPPER_CASE.contains(lcToken)) {
        		out(true);
        	} else if ( afterBetween && "and".equals(lcToken)) {//between 다음 단어중에서 and가 왔을때 대문자로 치환한다.
        		out(true);

        	} else if ( beginLine && "select".equals(lastToken) ) {//처음 시작할때...5칸을 밀어준다.
        		int loopCnt = 4;
        		loopCnt = ( parensSinceSelect * 6 ) + loopCnt;//Select 하위에 있는 첫번째 컬럼은 좀더 밀어준다.
        		appendWhiteSpace(loopCnt);//5번 space 로 밀어준다.
        		out();
        	} else {
        		out();
        	}


            if ("between".equals(lcToken)) {
                afterBetween = true;
            }
            if (afterInsert) {
                newline();
                afterInsert = false;
            } else {
                beginLine = false;
                if ("case".equals(lcToken)) {
                    indent++;
                }
            }
        }

        private void white() {
            if (!beginLine) {
                result.append(" ");
            }
        }

        private void updateOrInsertOrDelete() {
            out(true);
            indent++;
            beginLine = false;
            if ("update".equals(lcToken)) {
                newline();
            }
            if ("insert".equals(lcToken)) {
                afterInsert = true;
            }
        }

        private void select() {
            out(true);
            indent++;
            newline();
            parenCounts.addLast(parensSinceSelect);
            afterByOrFromOrSelects.addLast(afterByOrSetOrFromOrSelect);
            parensSinceSelect = 0;
            afterByOrSetOrFromOrSelect = true;
        }

        private void out() {
    		result.append(token);

        	if ( ",".equals( token)  ) {
        		result.append(" ");
        	} else {
        	}
        }
        private void out(boolean isSetupperCase) {
        	if (END_CLAUSES.contains(lcToken) && !"by".equals(lcToken) && !"into".equals(lcToken)  ) {
	        	for (int i = 0 ; i < 6 - token.length() - 1; i++ ) {
	        		result.append(" ");
	        	}
        	}

        	result.append(token.toUpperCase());
		}

        private void endNewClause() {
            if (!afterBeginBeforeEnd) {
                indent--;
                if (afterOn) {
                    indent--;
                    afterOn = false;
                }
                if ( "into".equals( lcToken ) ) {
                	
                } else {
                	newline();
                }
            }
            out(true);
            if (!"union".equals(lcToken)) {
                indent++;
            }

            if ( "by".equals(lcToken)  ) {
            	
            } else {
              //newline();
              result.append(" ");//from 다음에 들어가는 것.
            }

            afterBeginBeforeEnd = false;
            afterByOrSetOrFromOrSelect = "by".equals(lcToken)
                    || "set".equals(lcToken)
                    || "from".equals(lcToken);
        }


		private void beginNewClause() {
            if (!afterBeginBeforeEnd) {
                if (afterOn) {
                    indent--;
                    afterOn = false;
                }
                indent--;
                newline();
            }
            out(true);// order by , group by upper case 출력
            beginLine = false;
            afterBeginBeforeEnd = true;
        }

        private void values() {
            indent--;
            newline();
            out();
            indent++;
            newline();
            afterValues = true;
        }

        private void closeParen() {
            parensSinceSelect--;
            if (parensSinceSelect < 0) {
                indent = indent - 6;
                parensSinceSelect = parenCounts.removeLast();
                afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast();
            }
            if (inFunction > 0) {
                inFunction--;
                out();
            } else {
                if (!afterByOrSetOrFromOrSelect) {
                    indent--;
                    newline();
                	appendWhiteSpace(4);
                }
                out();
            }
            beginLine = false;
        }

        private void openParen() {

            if (isFunctionName(lastToken) || inFunction > 0) {
                inFunction++;
            }
            beginLine = false;
            if (inFunction > 0) {
                out();
            } else {
                out();
                if (!afterByOrSetOrFromOrSelect) {
                    //indent++;
                    indent = indent + 6;
                    newline();
                    beginLine = true;
                }
            }
            parensSinceSelect++;
        }

        private static boolean isFunctionName(String tok) {
            final char begin = tok.charAt(0);
            final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
            return isIdentifier &&
                    !LOGICAL.contains(tok) &&
                    !END_CLAUSES.contains(tok) &&
                    !QUANTIFIERS.contains(tok) &&
                    !DML.contains(tok) &&
                    !MISC.contains(tok);
        }

        private static boolean isWhitespace(String token) {
            return WHITESPACE.contains(token);
        }

        private void newline() {
        	int exceptCount = 0;
        	if (",".equals( lcToken ) ) {
        		exceptCount = 4;
        	} else if ( "end".equals(lcToken) ) {
        		exceptCount = 7;
        	}

        	if (afterByOrSetOrFromOrSelect && lcToken.equals(",")	) {
        		exceptCount = exceptCount -1;
        	}
        	
//        	exceptCount = (parensSinceSelect * 6 ) + exceptCount;//subquery 추가적인 depth.

            result.append(System.lineSeparator());
            for (int i = 0; i < (exceptCount + indent ) ; i++) {
//                result.append(INDENT_STRING);
            	result.append(" ");
            }

            beginLine = true;
        }
    }
    public static void main(String[] args) {
    	BasicFormatterImpl xx = new BasicFormatterImpl();
    	String source =
    					 " merge into tb1 a tb2  select a,b,c,d,e,f"
    					+" ,max(df2) as xk"
    					+" ,min(df2) over() as xk"
    					+" , ' asdfasdfl, asdfasdf' as xxx"
    					+" , case when bx is null then qq when cx is null then q1 else q2 end txt "
    					+" from test a join tb_a c"
    					+"  on 1 =1 and x = b and a.x1 = b.x1"
    					+"  and a.x2 = b.x2"
    					+"  join ("
    					+" select a,b,c,d,e,f"
    					+" ,max(df2) as xk"
    					+" ,min(df2) over() as xk"
    					+" , ' asdfasdfl, asdfasdf' as xxx"
    					+" , case when bx is null then qq when cx is null then q1 else q2 end txt "
    					+" from test a join tb_a c"
    					+"  on 1 =1 and x = b and a.x1 = b.x1"
    					+"  and a.x2 = b.x2"
    					+"  where qa like 'a%' and b = 'c'   "
    					+" and xt between cx and ct group by e, x, c, e order by e, x, c, e desc "
    					+" ) x1"
    					+" on a =1"
    					+" and wx = wq"
    					+"  where qa like 'a%' and b = 'c'   "
    					+" and xt between cx and ct group by e, x, c, e order by e, x, c, e desc "
    					;
		String sql = xx.format(source);


        System.out.println(sql);
//
//        sql = "SELECT * FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 " +
//                " WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5 WHERE AX = BX AND BQ = AQ) AND ID2 IN (SELECT * FROM MY_TABLE6)";
//        sql = xx.format(sql);
//        System.out.println(sql);
		
	}
}