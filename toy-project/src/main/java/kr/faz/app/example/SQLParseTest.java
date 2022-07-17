package kr.faz.app.example;

import java.util.Properties;

import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;

public class SQLParseTest {
	public static String sql =
			""
//	                + "MERGE INTO cfe.impairment imp\n" + "    USING ( WITH x AS (\n"
//	                + "                    SELECT  a.id_instrument\n"
//	                + "                            , a.id_currency\n"
//	                + "                            , a.id_instrument_type\n"
//	                + "                            , b.id_portfolio\n"
//	                + "                            , c.attribute_value product_code\n"
//	                + "                            , t.valid_date\n" + "                            , t.ccf\n"
//	                + "                    FROM cfe.instrument a\n"
//	                + "                        INNER JOIN cfe.impairment b\n"
//	                + "                            ON a.id_instrument = b.id_instrument\n"
//	                + "                        LEFT JOIN cfe.instrument_attribute c\n"
//	                + "                            ON a.id_instrument = c.id_instrument\n"
//	                + "                                AND c.id_attribute = 'product'\n"
//	                + "                        INNER JOIN cfe.ext_ccf t\n"
//	                + "                            ON ( a.id_currency LIKE t.id_currency )\n"
//	                + "                                AND ( a.id_instrument_type LIKE t.id_instrument_type )\n"
//	                + "                                AND ( b.id_portfolio LIKE t.id_portfolio\n"
//	                + "                                        OR ( b.id_portfolio IS NULL\n"
//	                + "                                                AND t.id_portfolio = '%' ) )\n"
//	                + "                                AND ( c.attribute_value LIKE t.product_code\n"
//	                + "                                        OR ( c.attribute_value IS NULL\n"
//	                + "                                                AND t.product_code = '%' ) ) )\n"
//	                + "SELECT /*+ PARALLEL */ *\n" + "            FROM x x1\n"
//	                + "            WHERE x1.valid_date = ( "
	                + "SELECT col1 AS x \n"
	                + "  FROM TB001 X1 \n"

//	                + "                                    WHERE id_instrument = x1.id_instrument ) ) s\n"
//	                + "        ON ( imp.id_instrument = s.id_instrument )\n" + "WHEN MATCHED THEN\n"
//	                + "    UPDATE SET  imp.ccf = s.ccf\n" + ";"
	                ;
//    @Test
//    public void testNestingDepth() throws Exception {
//    	JSqlParser jp = new JSqlParser();
//    	JSqlParserStatementWrapper x = jp.parse(sql);
//    	System.out.println(x.getSql());
//    	JdbcUrlParser ju = new JdbcUrlParser();
//    	DatabaseInfo dx = ju.parse(sql);
//    }
	public static void main(String[] args) throws SqlParseException {
		String sql = "C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\resources\\sample.sql";
		  final SqlParser parser = SqlParser.create(sql, SqlParser.Config.DEFAULT);
		  final SqlNode parsed = parser.parseStmt();
		  System.out.println(parsed);

//		  final CalciteCatalogReader catalogReader = new CalciteCatalogReader(
//		      CalciteSchema.from(rootSchema),
//		      CalciteSchema.from(defaultSchema).path(null),
//		      new JavaTypeFactoryImpl(), new CalciteConnectionConfigImpl(new Properties()));

//		  final SqlValidator validator = new ValidatorForTest(SqlStdOperatorTable.instance(),
//		      catalogReader, new JavaTypeFactoryImpl(), SqlConformanceEnum.DEFAULT);
//		  final SqlNode validated = validator.validate(parsed);
//		  final SqlToRelConverter.Config config = SqlToRelConverter.configBuilder()
//		      .withTrimUnusedFields(true)
//		      .withExpand(true)
//		      .withDecorrelationEnabled(true)
//		      .build();
//		  final SqlToRelConverter converter = new SqlToRelConverter(
//		      (rowType, queryString, schemaPath, viewPath) -> {
//		        throw new UnsupportedOperationException("cannot expand view");
//		      }, validator, catalogReader, cluster, StandardConvertletTable.INSTANCE, config);
//		   converter.convertQuery(validated, false, true).rel;
	}
}
