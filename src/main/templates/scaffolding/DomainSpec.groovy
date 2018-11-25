<%=packageName ? "package ${packageName}" : ''%>

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([${className}<%grails.core.GrailsDomainClassProperty[] fields = domainClass.persistentProperties;fields.eachWithIndex {grails.core.GrailsDomainClassProperty field,Integer index->if(!(field.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,java.time.ZonedDateTime]) && !field.isEnum() && !field.isEmbedded() && !field.isOneToMany() && !field.isManyToMany()){%>,${field.type.name}<%}}%>])
@Domain([${className}])
@TestMixin([HibernateTestMixin])
class ${className}DomainSpec extends ConstraintUnitSpec {

    void "test ${className} all constraints"() {
        when:
        List<Map> constraints = [<%fields.eachWithIndex { grails.core.GrailsDomainClassProperty field,Integer index->grails.validation.ConstrainedProperty constrainedProperties = domainClass.constrainedProperties[field.name];org.grails.orm.hibernate.validation.UniqueConstraint uniqueConstraint = constrainedProperties.getAppliedConstraint("unique");%><%if(constrainedProperties.isNullable()){%>
                [field: "${field.name}", value: null, testResult: TestResult.PASS],<%}else{ %>
                [field: "${field.name}", value: null, testResult: TestResult.FAIL],<%}%><%if(field.type == String && !constrainedProperties.notEqual && !constrainedProperties.isEmail() && !constrainedProperties.isUrl() && !constrainedProperties.isCreditCard() && !constrainedProperties.matches && !constrainedProperties.size && !constrainedProperties.maxSize && !constrainedProperties.minSize){ %>
                [field: "${field.name}", value: "${field.name}", testResult: TestResult.PASS],<%}else if(field.type == Date && !constrainedProperties.range && !constrainedProperties.notEqual && !constrainedProperties.min && !constrainedProperties.max){ %>
                [field: "${field.name}", value: new Date(), testResult: TestResult.PASS],<%}else if(field.type == java.time.ZonedDateTime){%>
                [field: "${field.name}", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],<%}else if(field.type in [Integer,Long,Double,Number,Float,Short] && !constrainedProperties.notEqual && !constrainedProperties.size && !constrainedProperties.min && !constrainedProperties.max){ %>
                [field: "${field.name}", value: 5, testResult: TestResult.PASS],
                [field: "${field.name}", value: "Number", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "101", testResult: TestResult.FAIL],
                [field: "${field.name}", value: -1, testResult: TestResult.PASS],<%}%><%if(field.type == String){ %><%if(constrainedProperties.isBlank()){%>
                [field: "${field.name}", value: " ", testResult: TestResult.PASS],<%}else{%>
                [field: "${field.name}", value: " ", testResult: TestResult.FAIL],<%}%><%if(constrainedProperties.isEmail()){%>
                [field: "${field.name}", value: "${field.name}", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "${field.name}@", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "${field.name}@.", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "${field.name}@gamil.com", testResult: TestResult.PASS],<%}%><%if(constrainedProperties.isUrl()){%>
                [field: "${field.name}", value: "${field.name}", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "www:${field.name}", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "www.${field.name}.com", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "http://www.${field.name}", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "http://${field.name}.com", testResult: TestResult.PASS],
                [field: "${field.name}", value: "http://www.${field.name}.com", testResult: TestResult.PASS],<%}%><%if(constrainedProperties.isCreditCard()){%>
                [field: "${field.name}", value: "${field.name}", testResult: TestResult.FAIL],
                [field: "${field.name}", value: "5105105105105100", testResult: TestResult.PASS],<%}%><%if(constrainedProperties.matches){%>
                [field: "${field.name}", value: "${field.name}A1c_", testResult: TestResult.FAIL],
                [field: "${field.name}", value: new nl.flotsam.xeger.Xeger("${constrainedProperties.matches}").generate(), testResult: TestResult.PASS],<%}%><%if(uniqueConstraint?.isUnique()) { %>
                [field: "${field.name}", value: "${field.name}",isUnique:true, testResult: TestResult.FAIL],<%}%><%if(constrainedProperties.size){  %>
                [field: "${field.name}", value: getLongString(${(constrainedProperties.size.from as int) - 1}), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getLongString(${(constrainedProperties.size.to as int) + 1}), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getLongString(${constrainedProperties.size.to as int}), testResult: TestResult.PASS],<%}%><%if(constrainedProperties.minSize){%>
                [field: "${field.name}", value: getLongString(${constrainedProperties.minSize-1}), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getLongString(${constrainedProperties.minSize}), testResult: TestResult.PASS],<%}%><%if(constrainedProperties.maxSize){%>
                [field: "${field.name}", value: getLongString(${constrainedProperties.maxSize+1}), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getLongString(${constrainedProperties.maxSize}), testResult: TestResult.PASS],<%}%><%if(constrainedProperties.notEqual){%>
                [field: "${field.name}", value: ${constrainedProperties.notEqual}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: "Ac1A", testResult: TestResult.PASS],<%}%><%}else{ %><%if(uniqueConstraint?.isUnique()){%><%if(field.type in [Integer,Long,Double,Number,Float,Short]){%>
                [field: "${field.name}", value: 10 ,isUnique:true, testResult: TestResult.FAIL],<%}%> <%}%><%if(constrainedProperties.size){%><%if(field.type in [Integer,Long,Double,Number,Float,Short]){%>
                [field: "${field.name}", value: ${new Integer((constrainedProperties.size.to as int)+10) as Number}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: ${new Integer((constrainedProperties.size.from as int)-1) as Number}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: 10, testResult: TestResult.PASS],<%}%><%}%><%if(constrainedProperties.min){%><%if(field.type == Date){%>
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.min - 2)}"), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.min + 2)}"), testResult: TestResult.PASS],<%}else if(field.type == java.time.ZonedDateTime){%>
                [field: "${field.name}", value: PCPUtils.parseZonedDateTime("${java.time.ZonedDateTime.ofInstant((constrainedProperties.min - 2).toInstant(), java.time.ZoneId.systemDefault())}"), testResult: TestResult.FAIL],
                [field: "${field.name}", value: PCPUtils.parseZonedDateTime("${java.time.ZonedDateTime.ofInstant((constrainedProperties.min + 2).toInstant(), java.time.ZoneId.systemDefault())}"), testResult: TestResult.PASS],<%}else if(field.type in [Number,Double,Float,Integer,Short]){%>
                [field: "${field.name}", value: ${new Integer((constrainedProperties.min as int)-2)}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: ${new Integer((constrainedProperties.min as int)+2)}, testResult: TestResult.PASS],<%}%><%}%><%if(constrainedProperties.max){ %><%if(field.type == Date){ %>
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.max + 1)}"), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.max - 1)}"), testResult: TestResult.PASS],<%}else if(field.type == java.time.ZonedDateTime){ %>
                [field: "${field.name}", value: PCPUtils.parseZonedDateTime("${java.time.ZonedDateTime.ofInstant((constrainedProperties.max + 2).toInstant(), java.time.ZoneId.systemDefault())}"), testResult: TestResult.FAIL],
                [field: "${field.name}", value: PCPUtils.parseZonedDateTime("${java.time.ZonedDateTime.ofInstant((constrainedProperties.max - 2).toInstant(), java.time.ZoneId.systemDefault())}"), testResult: TestResult.PASS],<%}else if(field.type in [Number,Double,Float,Integer,Short]){%>
                [field: "${field.name}", value: ${new Integer((constrainedProperties.max as int)+2)}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: ${new Integer((constrainedProperties.max as int)-2)}, testResult: TestResult.PASS],<%}%><%}%><%if(constrainedProperties.notEqual){ %><%if(field.type == Date){%>
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.notEqual)}"), testResult: TestResult.FAIL],
                [field: "${field.name}", value: getDateFromString("${ new java.text.SimpleDateFormat("dd/MM/yyyy").format(constrainedProperties.notEqual - 1)}"), testResult: TestResult.PASS],<%}else if(field.type == java.time.ZonedDateTime){%>
                [field: "${field.name}", value: ${constrainedProperties.notEqual}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: ${java.time.ZonedDateTime.now()}, testResult: TestResult.PASS],<%}else if(field.type in [Number,Double,Float,Integer,Short]){%>
                [field: "${field.name}", value: ${constrainedProperties.notEqual}, testResult: TestResult.FAIL],
                [field: "${field.name}", value: ${constrainedProperties.notEqual+1}, testResult: TestResult.PASS],<%}%><%}%><%}%><%if(!(field.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,java.time.ZonedDateTime]) && !field.isEnum() && !field.isEmbedded() && !field.isOneToMany() && !field.isManyToMany()){%>
                [field: "${field.name}", value: ${field.type.name}.build(), testResult: TestResult.PASS],<%}%><%}%>
        ]
        then:
        validateObject(${className},constraints)
    }
}
