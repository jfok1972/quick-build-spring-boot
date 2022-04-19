package com.jhopesoft.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.jhopesoft.framework.bean.TableBean;
import com.jhopesoft.framework.bean.TableFieldBean;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */
public class TableBeanGenerator {
    private List<String> all = new ArrayList<String>();
    private Set<String> imports = new TreeSet<String>();
    private List<String> headers = new ArrayList<String>();
    private List<String> privateFields = new ArrayList<String>();
    private List<String> createClassLines = new ArrayList<String>();
    private List<String> getSetLines = new ArrayList<String>();

    private String systemkey;
    private String schemeName;
    private String tableName;
    private String beanClassName;
    private boolean addManyToOne;

    private TableBean tableBean;

    public TableBeanGenerator(String systemkey, String schemeName, TableBean tableBean, boolean addManyToOne) {
        this.systemkey = systemkey == null ? schemeName : systemkey;
        this.setSchemeName(schemeName);
        this.tableName = tableBean.getTablename();
        this.tableBean = tableBean;
        this.addManyToOne = addManyToOne;
        this.beanClassName = CamelCaseUtils.firstCharacterUpperCase(CamelCaseUtils.underlineToCamelhump(tableName));
        tableBean.getFields().forEach(field -> getFieldList(field));
        initImports();
        initHeaders();
        initCreateClassLines();
    }

    public InputStream getInputStream() throws IOException {
        genAllParts();
        Writer fw = null;
        fw = new StringWriter();
        for (String str : all) {
            fw.write(str);
            fw.write("\n");
        }
        fw.flush();
        fw.close();
        return new ByteArrayInputStream(fw.toString().getBytes());
    }

    public void writeToBeanFile(String path) {
        genAllParts();
        all.forEach(line -> System.out.println(line));
        FileWriter fw;
        try {
            fw = new FileWriter(path + beanClassName + ".java");
            for (String str : all) {
                fw.write(str);
                fw.write("\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genAllParts() {
        all.clear();
        all.add(String.format("package com.jhopesoft.business.%s.entity;", systemkey));
        all.add(String.format("// Generated %s by Quick build System",
                new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(new Date())));
        addEmptyLine();
        imports.forEach(line -> all.add("import " + line + ";"));
        addEmptyLine();

        all.addAll(headers);
        addEmptyLine();
        all.addAll(privateFields);
        addEmptyLine();
        all.addAll(createClassLines);
        all.addAll(getSetLines);

        addEmptyLine();
        all.add("}");
    }

    public String classNameChange(TableFieldBean field) {
        String className = field.getFieldtype();
        if (Boolean.class.getName().equals(className) && field.getIsrequired()) {
            // Boolean 类型如果是必添，则改为 boolean,以下类同
            field.setFieldtype("boolean");
            className = field.getFieldtype();
        }
        if (Integer.class.getName().equals(className) && field.getIsrequired()) {
            field.setFieldtype("int");
            className = field.getFieldtype();
        }
        if (Double.class.getName().equals(className) && field.getIsrequired()) {
            field.setFieldtype("double");
            className = field.getFieldtype();
        }
        if (Float.class.getName().equals(className) && field.getIsrequired()) {
            field.setFieldtype("float");
            className = field.getFieldtype();
        }
        return className;
    }

    public List<String> getFieldList(TableFieldBean field) {
        String className = classNameChange(field);
        String lang = "java.lang";
        if (!className.startsWith(lang) && className.indexOf(Constants.DOT) > 0) {
            imports.add(className);
        }
        String shortClassName = className;
        if (className.lastIndexOf(Constants.DOT) != -1) {
            shortClassName = className.substring(className.lastIndexOf(Constants.DOT) + 1);
        }
        String camelCaseFieldName = checkKeyWords(CamelCaseUtils.underlineToCamelhump(field.getFieldname()));
        // 检查是否是manytoone的,如果是, 类，和名称都要换掉
        boolean manytoone = false;
        if (addManyToOne && StringUtils.isNotBlank(field.getJoincolumnname())) {
            // oneTwoMany fieldname
            field.setBy4(CamelCaseUtils.underlineToCamelhump(tableName) + "s");
            // manytoone的类名
            shortClassName = CamelCaseUtils
                    .firstCharacterUpperCase(CamelCaseUtils.underlineToCamelhump(field.getJointable()));
            // 如果 foreign key 和 one 端的主键是一样的
            if (field.getFieldname().equals(field.getBy5()))
                camelCaseFieldName = checkKeyWords(CamelCaseUtils.underlineToCamelhump(field.getJointable()));
            else {
                String f = CamelCaseUtils
                        .firstCharacterUpperCase(CamelCaseUtils.underlineToCamelhump(field.getFieldname()));
                // 和 one 端的主键不一样的
                if (getManyToOneCount(field.getJointable()) > 1) {
                    field.setBy4(field.getBy4() + "For" + f);
                    camelCaseFieldName = CamelCaseUtils.underlineToCamelhump(field.getJointable()) + "By" + f;
                } else {
                    // 如果当前class 的 manytoone 只有一个，那也用上面的方法
                    camelCaseFieldName = CamelCaseUtils.underlineToCamelhump(field.getJointable());
                }
            }
            // onetomany 的 mappedBy
            field.setBy2(camelCaseFieldName);
            // Set<BeanClassName>
            field.setBy3(beanClassName);
            manytoone = true;
            imports.add(ManyToOne.class.getName());
            imports.add(JoinColumn.class.getName());
            imports.add(FetchType.class.getName());
        }
        if (StringUtils.isNotBlank(field.getComments())) {
            // 检查一下字段有没有分组，有的话再加上一个
            String[] parts = field.getComments().split("\\|");
            if (parts.length >= Constants.INT_2) {
                privateFields.add("");
                privateFields.add(String.format("	/** 字段分组:%s */", parts[1]));
            }
            privateFields.add(String.format("	/** %s */", parts[0]));
        }
        privateFields.add(String.format("	private %s %s;", shortClassName, camelCaseFieldName));

        getSetLines.add("");
        boolean isPk = field.getFieldname().equals(tableBean.getPrimarykey());
        if (isPk) {
            getSetLines.add(String.format("	@Id"));
            // 如果主键是字符串，并且长度大于32，则设置为uuid
            if (Constants.STRING.equalsIgnoreCase(shortClassName) && field.getFieldlen() != null
                    && field.getFieldlen().intValue() >= 32) {
                getSetLines.add("	/** 请使用 strategy = \"uuid.hex\" 主键中最好不要有“-”号 */");
                getSetLines.add("	@GeneratedValue(generator = \"generator\")");
                getSetLines.add("	@GenericGenerator(name = \"generator\", strategy = \"uuid.hex\")");
                imports.add(GeneratedValue.class.getName());
                imports.add(GenericGenerator.class.getName());
            } else {
                getSetLines.add("");
            }
        }
        if (className.equals(java.util.Date.class.getName())) {
            imports.add(Temporal.class.getName());
            imports.add(TemporalType.class.getName());
            getSetLines.add("	@Temporal(TemporalType.TIMESTAMP)");
        }
        if (manytoone) {
            getSetLines.add(String.format("	@ManyToOne(fetch = FetchType.LAZY)"));
            getSetLines.add(String.format("	@JoinColumn(%s)", getColumnAttr(field, isPk, manytoone)));
        } else {
            getSetLines.add(String.format("	@Column(%s)", getColumnAttr(field, isPk, manytoone)));
        }
        getSetLines.add(String.format("	public %s get%s() {", shortClassName,
                CamelCaseUtils.firstCharacterUpperCase(camelCaseFieldName)));
        getSetLines.add(String.format("		return this.%s;", camelCaseFieldName));
        getSetLines.add(String.format("	}"));
        getSetLines.add("");

        getSetLines.add(String.format("	public void set%s(%s %s) {",
                CamelCaseUtils.firstCharacterUpperCase(camelCaseFieldName), shortClassName, camelCaseFieldName));
        getSetLines.add(String.format("		this.%s = %s;", camelCaseFieldName, camelCaseFieldName));
        getSetLines.add(String.format("	}"));
        return getSetLines;
    }

    private int getManyToOneCount(String oneSideTable) {
        int[] i = { 0 };
        tableBean.getFields().forEach(field -> {
            if (oneSideTable.equals(field.getJointable())) {
                i[0] = i[0] + 1;
            }
        });
        return i[0];
    }

    /**
     * @Column(name = "type_", nullable = false, length = 30)
     * @param field
     * @param unique
     * @param manytoone
     * @return
     */
    public String getColumnAttr(TableFieldBean field, boolean unique, boolean manytoone) {
        String ft = field.getFieldtype();
        String result = String.format("name = \"%s\"", field.getFieldname());
        if (unique) {
            result += ", unique = true";
        }
        if (BooleanUtils.isTrue(field.getIsrequired())) {
            result += ", nullable = false";
        }
        if (!manytoone) {
            if (ft.equals(String.class.getName()) || ft.equals(Date.class.getName())
                    || ft.equals(Timestamp.class.getName()) || ft.equals(java.sql.Date.class.getName())) {
                result += ", length = " + field.getFieldlen();
            }
            if (ft.equals(BigDecimal.class.getName())) {
                result += ", precision = " + field.getFieldlen() + ", scale = " + field.getDatascale();
            }
        }
        return result;

    }

    public void initImports() {
        imports.add(Serializable.class.getName());
        imports.add(Entity.class.getName());
        imports.add(DynamicUpdate.class.getName());
        imports.add(Table.class.getName());
        imports.add(Column.class.getName());
        imports.add(Id.class.getName());
    }

    public void initHeaders() {
        headers.add("/**");
        headers.add(" *");
        headers.add(String.format(" * %s(%s) generated by Quick Build System ",
                StringUtils.isBlank(tableBean.getComment()) ? beanClassName : tableBean.getComment(), beanClassName));
        headers.add(" *");
        headers.add(String.format(" * 了解快速架构系统 https://github.com/jfok1972"));
        headers.add(" *");
        headers.add(" * @author 蒋锋 jfok1972@qq.com");
        headers.add(" *");
        headers.add("*/");

        headers.add("@Entity");
        headers.add("@DynamicUpdate");
        headers.add(String.format("@Table(name = \"%s\")", tableName));
        headers.add(String.format("public class %s implements Serializable {", beanClassName));
    }

    public void initCreateClassLines() {
        createClassLines.add(String.format("	public %s() {", beanClassName));
        createClassLines.add(String.format("	}"));
    }

    public void addOneToManyLines(TableFieldBean oneToManyfield) {

        imports.add(OneToMany.class.getName());
        imports.add(Set.class.getName());
        imports.add(LinkedHashSet.class.getName());
        imports.add(FetchType.class.getName());

        privateFields.add(String.format("	private Set<%s> %s = new LinkedHashSet<%s>(0);", oneToManyfield.getBy3(),
                oneToManyfield.getBy4(), oneToManyfield.getBy3()));

        getSetLines.add("");
        getSetLines.add(
                String.format("	@OneToMany(fetch = FetchType.LAZY, mappedBy = \"%s\")", oneToManyfield.getBy2()));
        getSetLines.add(String.format("	public Set<%s> get%s() {", oneToManyfield.getBy3(),
                CamelCaseUtils.firstCharacterUpperCase(oneToManyfield.getBy4())));
        getSetLines.add(String.format("		return this.%s;", oneToManyfield.getBy4()));
        getSetLines.add(String.format("	}"));
        getSetLines.add("");

        getSetLines.add(String.format("	public void set%s(Set<%s> %s) {",
                CamelCaseUtils.firstCharacterUpperCase(oneToManyfield.getBy4()), oneToManyfield.getBy3(),
                oneToManyfield.getBy4()));
        getSetLines.add(String.format("		this.%s = %s;", oneToManyfield.getBy4(), oneToManyfield.getBy4()));
        getSetLines.add(String.format("	}"));

    }

    private void addEmptyLine() {
        all.add("");
    }

    public TableBean getTableBean() {
        return tableBean;
    }

    public void setTableBean(TableBean tableBean) {
        this.tableBean = tableBean;
    }

    public String checkKeyWords(String str) {
        for (int i = 0; i < KEYWORDS.length; i++) {
            if (KEYWORDS[i].equals(str)) {
                return str + "_";
            }
        }
        return str;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }
    
    public static final String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };

}
