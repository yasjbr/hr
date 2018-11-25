<%=packageName ? "package ${packageName}" : ''%>

import ps.police.common.validation.PCPValidateable<%
def fields=domainClass?.persistentProperties
def constraints = domainClass?.constrainedProperties
fields.each {if(!(it.type in [Integer,String, Long,Double,Number,Boolean,Byte,Short,Character,Float,Date,Set,List])){if(it.type.simpleName == "TrackingInfo"){%>
import ps.police.common.commands.${versionNumber}.TrackingInfoCommand<%}else if(it.type.simpleName == "DescriptionInfo"){%>
import ps.police.common.commands.${versionNumber}.DescriptionInfoCommand<%}else if(it.type.simpleName == "ZonedDateTime"){%>
import java.time.ZonedDateTime<%} else if(it.isEnum()){ %>
import ${it.type.package.name}.${it.type.simpleName}<%}else{%>
import ${it.type.package.name}.commands.${versionNumber}.${it.type.simpleName}Command<%}}}%>

/**
 *<h1>Purpose</h1>
 * hold ${className} properties and relations to public remoteing usage.
 * handle data validation before sending to remote destination.
 **/
class ${className}Command implements PCPValidateable,Serializable{
    static final long serialVersionUID = 1L
    String id
    Long version<%fields.each {
        if((it.type in [Integer,String, Long,
                        Double,Number,Boolean,
                        Byte,Short,Character,
                        Float,Date,java.time.ZonedDateTime]) || it.isEnum()){%>
    ${it.type.simpleName} ${it.name}<%}else if(it.type == Set || it.type == List){ %><%}else{%>
    ${it.type.simpleName}Command ${it.name}<%}
    }%>

    static constraints = {
        id(nullable:true)
        version(nullable:true)<%constraints.each {entry->%><%if(entry.value.propertyType !=Set && entry.value.propertyType != List){%>
        ${entry.key}(<%entry.value.getAppliedConstraints().eachWithIndex { constraintInstance, index ->%><%if(constraintInstance.class.simpleName == "MatchesConstraint"){%>${index == 0 ? "": ","}${constraintInstance.name}:"${constraintInstance.parameter}"<%}else if(constraintInstance.class.simpleName == "ValidatorConstraint"){%><%}else{%>${index == 0 ? "": ","}${constraintInstance.name}:${constraintInstance.parameter}<%}%><%}%>)<%}%><%}%>
    }
}