package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

public class TestCaseClassLoader   {

    public static void main(String[] args) {
		ClassLoader classLoader = TestCaseClassLoader.class.getClass().getClassLoader();

		//대상 패키지 지정
		
		try {
			List<File>  classFiles = scan("io.mycat");

			for ( int i = 0 ; i < classFiles.size() ; i++ ) {
				File file = classFiles.get(i);
				String filecp = file.getCanonicalPath();
				filecp = filecp.substring(filecp.indexOf("\\classes\\") +9 ) ;
				filecp = filecp.replaceAll("\\\\", ".");
				
				filecp = filecp.replace(".class", "");
				Class c = getOrgClass( filecp , classLoader);
				
				if ( c.isInterface() ) {
					continue;
				}
				
				String constructData = getConstructData(c); 
				//
				String result = getGSetter( c , classLoader);
				filecp = filecp.replace(".class", "");
				filecp = filecp.substring( filecp.lastIndexOf(".") + 1);
				
				filecp = "\t@Test\n\tpublic void testVo" + filecp + "() {" + "\n"  + constructData + "\n" + result + " \n" + "\t}\n\n";
				System.out.println( filecp);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//Class new 정보
//	String classData = String.format("\t\t%s vo = new %s();\n", c.getSimpleName() , c.getSimpleName());

    private static String getConstructData(Class c) {
    	Constructor[] cs = c.getConstructors();
    	if ( cs == null || cs.length == 0 ) {
    		return String.format("\t\t%s vo = new %s();\n", c.getSimpleName() , c.getSimpleName());
    	}

    	//최초선언
    	if ( cs.length == 1 ) {
    		return String.format("\t\t%s vo = new %s(%s);\n", c.getSimpleName() , c.getSimpleName() , getParameter( cs[0] ));    		
    	}

    	StringBuffer sb = new StringBuffer();
    	sb.append( String.format("\t\t%s vo = null;\n", c.getSimpleName() ) );
    	for ( Constructor cst : cs) {
    		String paramInitValue = getParameter( cst ) ;
			sb.append( String.format("\t\tvo = new %s(%s);\n", c.getSimpleName() , paramInitValue )  );
    	}
    	return sb.toString();
	}

	private static String getParameter(Constructor cs) {
		if (  cs.getParameterCount() == 0 )
			return "";

		String paramInitValue = "";
		if ( cs.getParameterCount() != 0 ) {
			Parameter[] params = cs.getParameters();
			for ( Parameter pm : params ) {
				paramInitValue += getParamInitValue(pm)+",";
			}
			paramInitValue = paramInitValue.substring(0 , paramInitValue.length() -1);
		}
		return paramInitValue;
	}

	private static Class getOrgClass(String filecp, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
    	return ClassUtils.forName( filecp  , classLoader);
	}


	private static String getGSetter(Class c , ClassLoader classLoader) {
		StringBuffer gsb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		try {
			
			Method[] ms = c.getMethods();
			for ( Method m : ms ) {
				if ( isSkipMethod(m)) {
					continue;
				}
				String methodName = m.getName();
				String paramInitValue = "";
				if ( m.getParameterCount() != 0 ) {
					Parameter[] params = m.getParameters();
					for ( Parameter pm : params ) {
						paramInitValue += getParamInitValue(pm)+",";
					}
					paramInitValue = paramInitValue.substring(0 , paramInitValue.length() -1);
				}
				if ( methodName.startsWith("set")) {
					ssb.append("\t\tvo.");
					ssb.append(methodName );
					ssb.append("( ");
					ssb.append(paramInitValue) ;
					ssb.append(" );");
					ssb.append("\n");
				} else if ( methodName.startsWith("get") || methodName.startsWith("is") ) {

					gsb.append("\t\t\t+vo.");
					gsb.append(methodName );
					gsb.append(" ( ");
					gsb.append(paramInitValue) ;
					gsb.append(" )\n ");
				}
			}

		} catch (LinkageError e) {
			e.printStackTrace();
		}
		String getterString = gsb.toString();
		if ( StringUtils.isNotEmpty( getterString  )) {
			getterString = getterString.trim().substring(0, getterString.trim().length()  ) ;
		}  else {
			getterString = "";
		}
		getterString = String.format( "\t\tSystem.out.println(\"\" \n\t\t\t%s \n\t\t) ; " , getterString  + " + vo.toString() + vo.hashCode() + vo.equals( null ) ");
		
		//최종 정보 반환.
		return ssb.toString() + "\n" + getterString  ;
	}

	/********************************************************************************************
	 * method parameter 설정
	 * @param pm
	 * @return
	 ********************************************************************************************/
	private static String getParamInitValue(Parameter pm) {
		String typeString = pm.getParameterizedType().getTypeName();

		if ( "byte"  .equals( typeString  ) ) return " (byte) 1  /** byte **/ ";
		if ( "int"   .equals( typeString  ) ) return " 1   /** int    **/  ";
		if ( "long"  .equals( typeString  ) ) return " 1L  /** long   **/ ";
		if ( "float" .equals( typeString  ) ) return " 1.f /** float  **/ ";
		if ( "double".equals( typeString  ) ) return " 1.d /** double **/ ";
		if ( "java.lang.String"    .equals( typeString  ) ) return " \"x str\" /** String **/ ";
		if ( "java.util.List"      .equals( typeString  ) ) return " new ArrayList<>() /** java.util.List      **/ ";
		if ( "java.util.ArrayList" .equals( typeString  ) ) return " new ArrayList<>() /** java.util.ArrayList **/ ";
		if ( "java.util.HashMap"   .equals( typeString  ) ) return " new HashMap<>()   /** java.util.HashMap   **/ ";
		if ( "java.util.Map"       .equals( typeString  ) ) return " new HashMap<>()   /** java.util.Map       **/ ";

		//일반 class 의 경우 마지막 class name 취득.
		if ( typeString.lastIndexOf(".") != -1 ) {
			typeString = typeString.substring(typeString.lastIndexOf(".")+1);
		}
		if (typeString.indexOf("[]") != -1   ) {
			return " new " + typeString +" {} /** etc **/";
		} 
		return " new " + typeString +" () /** etc **/";
	}

	private static boolean isSkipMethod(Method m) {
		String name = m.getClass().getCanonicalName();
		if (m.toString().indexOf("native") != -1 || name.equals("equals")  ) return true;
		if (name.equals("wait")     || name.equals("toString")    ) return true;
		if (name.equals("notify")   || name.equals("notifyAll")   ) return true;
		if (name.equals("hashCode") || name.equals("getClass")    ) return true;

		if ( m.toString().indexOf("setAnnotationValue") != -1   )  return false;
		if ( m.toString().indexOf("getAnnotationValue") != -1   )  return false;
		if ( m.toString().indexOf("getAnnotationContent") != -1   )  return false;

		return true;
	}

	/***********************************************
	 * 패키지 스캔.
	 * @param basePackageName
	 * @return
	 * @throws Exception 
	 */
	public static List<File> scan(String basePackageName) throws Exception {
    	List<File> classFiles = new ArrayList<File>();
       	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
       	String path = basePackageName.replace('.', '/');
       	try {
       		List<File> files = new ArrayList<File>();
    		Enumeration<URL> resources = classLoader.getResources(path);
    		while (resources.hasMoreElements()) {
    			URL resource = resources.nextElement();
    			files.add(new File(resource.getFile()));
    		}
    		for (File file : files) {
    			if (file.isDirectory()) {
//    				System.out.println("[Directory] " + file.getAbsolutePath());
    				findClasses(file, classFiles);
    			}
    		}
    	} catch (IOException e) {
    		throw e;
    	}

		if ( classFiles.size() == 0 ) {
			throw new Exception("패키지가 없거나 경로지정이 잘못되었습니다.");
		}

       	return classFiles;
    }

    private static void findClasses(File directory, List<File> classFiles) {
    	if (!directory.exists()) {
    		return;
    	}
    	File[] files = directory.listFiles();
    	for (File file : files) {
    		if (file.isDirectory()) {
//    			System.out.println("[Directory] " + file.getAbsolutePath());
    			findClasses(file, classFiles);
    		} else if (file.getName().endsWith(".class")) {
//    			System.out.println("[File] " + file.getAbsolutePath());
    			classFiles.add(file);
    		}
    	}
    }
}
