<%=packageName ? "package ${packageName};" : ''%>
<%def fields=domainClass?.persistentProperties
fields.each {if(!(it.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,Set,List])){
    if(it.type.simpleName == "TrackingInfo"){%>
import ps.police.common.dtos.${versionNumber}.TrackingInfoDTO;<%}else if(it.type.simpleName == "DescriptionInfo"){%>
import ps.police.common.dtos.${versionNumber}.DescriptionInfoDTO;<%}else if(it.type.simpleName == "ZonedDateTime"){%>
import java.time.ZonedDateTime;<%} else if(it.type == Date){ %>
import java.util.Date;<%} else if(it.isEnum()){ %>
import ${it.type.package.name}.${it.type.simpleName}Enum;<%}else{%>
import ${it.type.package.name}.dtos.${versionNumber}.${it.type.simpleName}DTO;<%}}}%>
/**
 *<h1>Purpose</h1>
 * hold ${className} properties to public remoting usage.
 **/
public class ${className}DTO implements org.grails.plugins.dto.DTO {

    private static final long serialVersionUID = 1L;
    private String id;
    private Long version;<%fields.each {
        if((it.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,java.time.ZonedDateTime])){%>
    private ${it.type.simpleName} ${it.name};<%}else if(it.type == Set || it.type == List){%><%}else if(it.isEnum()){%>
    private ${it.type.simpleName}Enum ${it.name};<%}else{%>
    private ${it.type.simpleName}DTO ${it.name};<%}}%>

    public String getId(){return this.id;}
    public void setId(String id){this.id = id;}
    public Long getVersion(){return this.version;}
    public void setVersion(Long version){this.version = version;}<%fields.each {if((it.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,java.time.ZonedDateTime])){%>
    public ${it.type.simpleName} get${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(){return this.${it.name};}
    public void set${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(${it.type.simpleName} ${it.name}){this.${it.name} = ${it.name};}<%}else if(it.type == Set || it.type == List){%><%}else if(it.isEnum()){ %>
    public ${it.type.simpleName}Enum get${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(){return this.${it.name};}
    public void set${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(${it.type.simpleName}Enum ${it.name}){this.${it.name} = ${it.name};}<%}else{%>
    public ${it.type.simpleName}DTO get${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(){return this.${it.name};}
    public void set${it.name.substring(0, 1).toUpperCase() + it.name.substring(1)}(${it.type.simpleName}DTO ${it.name}){this.${it.name} = ${it.name};}<%}
    }%>

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("${className}DTO[");
        sb.append("\\n\\tid: " + this.id);
        sb.append("\\n\\tversion: " + this.version);<%fields.each {%><%if(it.type != Set && it.type != List){%>
        sb.append("\\n\\t${it.name}: " + this.${it.name});<%}}%>
        sb.append("]");
        return sb.toString();
    }
}