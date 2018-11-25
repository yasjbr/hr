<%def fields=domainClass?.persistentProperties
def constraintsMap = domainClass?.constrainedProperties
fields.each {
    String className = "";
    String wrapper;
    Boolean display;
    for (entry in constraintsMap) {
        def propertyName = entry.key
        if (propertyName == it.name) {
            grails.validation.ConstrainedProperty propertyAccessor = entry.value
            wrapper=propertyAccessor?.widget
            display=propertyAccessor?.display
            if (!propertyAccessor?.nullable)
            if (it?.type == Double.class || it?.type == Float.class || it?.type == Number.class)
                className += " isDecimal"
            if (it?.type == Short.class || it?.type == Long.class || it?.type == Integer.class)
                className += " isNumber"
            if (propertyAccessor?.email)
                className += " isEmail"
            if (propertyAccessor?.password)
                className += " isPassword"
            if (it?.type == Date.class)
                className += " isDate"
            if (propertyAccessor?.url)
                className += " isUrl"
            if (propertyAccessor?.getMetaConstraintValue("phone"))
                className += " isPhone"
            if (propertyAccessor?.getMetaConstraintValue("currency"))
                className += " isCurrency"
            if (propertyAccessor?.getMetaConstraintValue("color"))
                className += " isColor"
        }
    }
    if(display && it.name != "trackingInfo" && it.type != java.util.Set.class && it.type != java.util.List.class){
        if(!wrapper){%>
<el:formGroup><%if(it.type.simpleName == "Date" || it.type.simpleName == "ZonedDateTime"){%>
    <el:dateField name="${it.name}"  size="8" class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(it.type.simpleName == "Boolean"){%>
    <el:checkboxField name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(it.type.simpleName == "Number" || it.type.simpleName == "Float" || it.type.simpleName == "Double"){%>
    <el:decimalField name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}"  /><%}else if(it.type.simpleName == "Integer" || it.type.simpleName == "Long" || it.type.simpleName == "Short"){%>
    <el:integerField name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" />
    <%}else if(it.type.simpleName == "String"){%>
    <%if(className.contains("isEmail")){%>
    <el:emailField name="${it.name}" size="8" class="${className}"  label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(className.contains("isUrl")){%>
    <el:textField name="${it.name}" size="8" class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(className.contains("isPhone")){%>
    <el:phoneField name="${it.name}" size="8" class="${className}"  label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(className.contains("isCurrency")){%>
    <el:currencyField name="${it.name}" size="8" class="${className}"  label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(className.contains("isPassword")){%>
    <el:passwordField name="${it.name}" size="8" class="${className}"  label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(className.contains("isColor")){%>
    <el:colorField name="${it.name}" size="8" class="${className}"  label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else if(domainClass?.constrainedProperties[it?.name]?.widget && (domainClass?.constrainedProperties[it?.name]?.widget == "textarea" || domainClass?.constrainedProperties[it?.name]?.widget == "textArea")){%>
    <el:textArea name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else{%>
    <el:textField name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}%><%}else if(it.isEnum()){%>
    <el:select valueMessagePrefix="${it.type.simpleName}" from="\${${it.referencedPropertyType.name}.values()}" name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}else{%>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="${className}" controller="${it.referencedPropertyType.simpleName[0].toLowerCase() + it.referencedPropertyType.simpleName.substring(1)}" action="autocomplete" name="${it.name}.id" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}%>
</el:formGroup><%}else{%><%if(wrapper=="autocomplete"){%>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="${className}" controller="${it.referencedPropertyType.simpleName[0].toLowerCase() + it.referencedPropertyType.simpleName.substring(1)}" action="autocomplete" name="${it.name}.id" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" />
</el:formGroup><%}else if(wrapper=="autocompleteMulti"){%>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="${className}" multiple="multiple" controller="${it.referencedPropertyType.simpleName[0].toLowerCase() + it.referencedPropertyType.simpleName.substring(1)}" action="autocomplete" name="${it.name}.id" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" />
</el:formGroup><%}else if(wrapper=="textarea" || wrapper=="textArea"){%>
<el:formGroup>
    <el:textArea name="${it.name}" size="8"  class="${className}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" />
</el:formGroup><%}else{%>
<g:render template="/${wrapper}/wrapper" model="[bean:${propertyName}?.${it.name},isSearch:true]" /><%}%><%}}}%>
