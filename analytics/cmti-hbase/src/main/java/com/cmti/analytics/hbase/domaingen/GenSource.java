package com.cmti.analytics.hbase.domaingen;
  
import java.lang.reflect.*;
import java.lang.annotation.*; 
import java.util.*;
import java.io.*; 
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
/**
 * usage:
 * java -cp C:\git\cmti2\cmti\analytics\tracking-app\target\tracking-app-1.0-SNAPSHOT.jar -Ddir=C:\git\cmti2\cmti\analytics\tracking-lib\src\main\java\com\cmti\analytics\app\tracking\hbase -Dpackage=com.cmti.analytics.app.tracking.hbase com.cmti.analytics.hbase.domaingen.GenSource Mr MrOnRoad Road RoadCell DriveTest DriveTestData
 * @author Guobiao Mo
 *
 */
public class GenSource {
	static final String WORK_PATH="/domaingen/";//resource files are in /analytics/src/main/resources/domaingen
	static String dir;
	static String homePackage;
	
	Class mappingClass; 
	VelocityContext context;

	static final String SEP = File.separator;
	static final String KEEP = "keep";
	
	static String[][] types = new String[][]{
		//domain
		new String[]{"DomainBean.vm", SEP+"domain"+SEP+"bean"+SEP+"{0}Bean.java",""},
		new String[]{"Domain.vm", SEP+"domain"+SEP+"{0}.java", KEEP},
		//Dao
		//new String[]{"Dao.vm", SEP+"dao"+SEP+"{0}Dao.java", KEEP}
	};

	static  Template[] vms;

	public static void main(String[] args) throws Exception{
		dir = System.getProperty("dir");//"C:\git\business\analytics\src\main\java\com\cmti\analytics"
		homePackage = System.getProperty("package");//"com.cmti.analytics"		

	    Properties prop=new Properties();
	    InputStream in = GenSource.class.getResourceAsStream(WORK_PATH+"velocity.properties"); 
	    prop.load(in); 
	    Velocity.init(prop);

	    vms = new Template[types.length];
	    for(int i=0; i<types.length; i++){
	    	vms[i]= Velocity.getTemplate(WORK_PATH+types[i][0]);
	    }

		for(String cl:args){	
			try{		
				String className = homePackage+".domain.mapping."+cl+"Mapping";
				Class c = Class.forName(className);
				System.out.println("GenJPASource processing: " + className);
				GenSource gen = new GenSource(c);
				gen.doClass();	
			}catch(Throwable e){
				System.out.println(e.getMessage()+"--<<<-skip>>"+cl);
				e.printStackTrace();
			}
		}		
	}

	private GenSource(Class c) {
		mappingClass = c;
		context = new VelocityContext();

		context.put("homePackage", homePackage);//"com.cmti.analytics"		
		context.put("rawName", getRawName());//"Event"

		ArrayList<DomainField> keys = new ArrayList<DomainField>();
		ArrayList<DomainField> fs = new ArrayList<DomainField>();

		Field[] fields = mappingClass.getDeclaredFields();
		
		for (Field field : fields) {
			Annotation[] annotations = field.getDeclaredAnnotations();
			
			if(ArrayUtils.isEmpty(annotations)){
				continue;
			}
			
			DomainField f = new DomainField();

			f.setHomePackage(homePackage);
			
			String fieldName = field.getName();//"userName"

			Class typeClass = field.getType();//String.class
			
			String typeName = typeClass.getName();//"java.lang.String"

			TypeVariable<Class>[] vars = typeClass.getTypeParameters();//<java.lang.String, int>
			if (vars.length > 0) {//only List type
				ParameterizedType type = (ParameterizedType)field.getGenericType(); 
				typeName = type.toString();//java.util.ArrayList<java.lang.String>
				f.setList(true);
				Class<?> t = (Class<?>)type.getActualTypeArguments()[0];//String.class
				f.setListType(trimDefaultPackage(t.getName()));//java.lang.String
			}
			
			if (typeClass.isArray()) {
				typeName = typeClass.getComponentType().getName()+"[]";
			}
						
			typeName = trimDefaultPackage(typeName);//"String"
			
			String dbName=null;
			boolean isHBaseField = false;
			for (Annotation annotation : annotations) {
				Class a = annotation.annotationType();
				String aname = a.getName();//com.cmti.hbase.annotation.Column
				String aStr = annotation.toString();//@com.cmti.hbase.annotation.Column(readVersion=false, cf=, value=d_pid)
				if ("com.cmti.analytics.hbase.annotation.Column".equals(aname)){
					dbName=StringUtils.substringBetween(aStr,"value=", ")");
					fs.add(f);
					isHBaseField = true;
				}else if ("com.cmti.analytics.hbase.annotation.Key".equals(aname)||"com.cmti.analytics.hbase.annotation.CompositeKey".equals(aname)){
					keys.add(f);	
					isHBaseField = true;				
				}
			}

			if(isHBaseField==false){
				continue;
			}
			
			f.setType(typeName);
			f.setName(fieldName);
			f.setDbName(dbName);
		}

		context.put("fields", fs);
		context.put("keys", keys);
	}

	private void doClass() throws Exception{
		File file = null;
		try{
			for(int i=0; i<vms.length; i++){
				String fileName = MessageFormat.format(types[i][1], getRawName());
				file=new File(dir+fileName);
				if(file.exists() && KEEP.equals(types[i][2])){
					continue;
				}

				System.out.println("Create : " + file.getAbsolutePath());
				file.getParentFile().mkdirs();
				PrintWriter out = new PrintWriter(new FileWriter(file));
				vms[i].merge(context, out);
				out.flush();
				out.close();
				
			}	
		}catch(NoClassDefFoundError e){
			System.out.println(e.getMessage()+"---skip");
			file.delete();
		}	
	}
	
	/**
	 * For EventMapping, 
	 * @return "Event"
	 */
	private String getRawName(){
		String mappingName = mappingClass.getSimpleName();
		int i= mappingName.indexOf("Mapping");
		return mappingName.substring(0, i);
	}

	/**
	 * 
	 * @param typeName "java.lang.String"
	 * @return "String"
	 */
	public static String trimDefaultPackage(String typeName){
		if(typeName==null)
			return null;
		
		if (typeName.startsWith("java.lang.")) {
			typeName = typeName.substring(10);
		}
		return typeName;
	}
}
