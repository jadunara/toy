/*     */ package org.apache.ibatis.scripting.xmltags;
/*     */
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.ibatis.ognl.OgnlContext;
/*     */ import org.apache.ibatis.ognl.OgnlException;
/*     */ import org.apache.ibatis.ognl.OgnlRuntime;
/*     */ import org.apache.ibatis.ognl.PropertyAccessor;
/*     */ import org.apache.ibatis.reflection.MetaObject;
/*     */ import org.apache.ibatis.session.Configuration;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class DynamicContext
/*     */ {
/*     */   public static final String PARAMETER_OBJECT_KEY = "_parameter";
/*     */   public static final String DATABASE_ID_KEY = "_databaseId";
/*     */   private final ContextMap bindings;
/*     */
/*     */   static {
/*  38 */     OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
/*     */   }
/*     */
/*     */
/*  42 */   private final StringBuilder sqlBuilder = new StringBuilder();
/*  43 */   private int uniqueNumber = 0;
/*     */
/*     */   public DynamicContext(Configuration configuration, Object parameterObject) {
/*  46 */     if (parameterObject != null && !(parameterObject instanceof Map)) {
/*  47 */       MetaObject metaObject = configuration.newMetaObject(parameterObject);
/*  48 */       this.bindings = new ContextMap(metaObject);
/*     */     } else {
/*  50 */       this.bindings = new ContextMap(null);
/*     */     }
/*  52 */     this.bindings.put("_parameter", parameterObject);
/*  53 */     this.bindings.put("_databaseId", configuration.getDatabaseId());
/*     */   }
/*     */
/*     */   public Map<String, Object> getBindings() {
/*  57 */     return this.bindings;
/*     */   }
/*     */
/*     */   public void bind(String name, Object value) {
/*  61 */     this.bindings.put(name, value);
/*     */   }
/*     */
/*     */   public void appendSql(String sql) {
/*  65 */     this.sqlBuilder.append(sql);
/*  66 */     this.sqlBuilder.append(" ");
/*     */   }
/*     */
/*     */   public String getSql() {
/*  70 */     return this.sqlBuilder.toString();
/*     */   }
/*     */
/*     */   public int getUniqueNumber() {
/*  74 */     return this.uniqueNumber++;
/*     */   }
/*     */
/*     */   static class ContextMap extends HashMap<String, Object> {
/*     */     private static final long serialVersionUID = 2977601501966151582L;
/*     */     private MetaObject parameterMetaObject;
/*     */
/*     */     public ContextMap(MetaObject parameterMetaObject) {
/*  82 */       this.parameterMetaObject = parameterMetaObject;
/*     */     }
/*     */
/*     */
/*     */     @Override
public Object get(Object key) {
/*  87 */       String strKey = (String)key;
/*  88 */       if (containsKey(strKey)) {
/*  89 */         return super.get(strKey);
/*     */       }
/*     */
/*  92 */       if (this.parameterMetaObject != null)
/*     */       {
/*  94 */         return this.parameterMetaObject.getValue(strKey);
/*     */       }
/*     */
/*  97 */       return null;
/*     */     }
/*     */   }
/*     */
/*     */
/*     */   static class ContextAccessor
/*     */     implements PropertyAccessor
/*     */   {
/*     */     @Override
public Object getProperty(Map context, Object target, Object name) throws OgnlException {
/* 106 */       Map map = (Map)target;
/*     */
/* 108 */       Object result = map.get(name);
/* 109 */       if (map.containsKey(name) || result != null) {
/* 110 */         return result;
/*     */       }
/*     */
/* 113 */       Object parameterObject = map.get("_parameter");
/* 114 */       if (parameterObject instanceof Map) {
/* 115 */         return ((Map)parameterObject).get(name);
/*     */       }
/*     */
/* 118 */       return null;
/*     */     }
/*     */
/*     */
/*     */
/*     */     @Override
public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
/* 124 */       Map<Object, Object> map = (Map<Object, Object>)target;
/* 125 */       map.put(name, value);
/*     */     }
/*     */
/*     */
/*     */     @Override
public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
/* 130 */       return null;
/*     */     }
/*     */
/*     */
/*     */     @Override
public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
/* 135 */       return null;
/*     */     }
/*     */   }
/*     */ }
