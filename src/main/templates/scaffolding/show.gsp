<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="\${message(code: '${propertyName}.entity', default: '${className} List')}" />
    <g:set var="title" value="\${message(code: 'default.show.label',args:[entity], default: '${className} List')}" />
    <title>\${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='\${createLink(controller:'${propertyName}',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="\${title}"><%domainClass?.persistentProperties?.each {%><%if(it.type.simpleName != "TrackingInfo"){%><%if(it.isEnum()){%>
    <lay:showElement value="\${${propertyName}?.${it.name}}" type="enum" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" messagePrefix="${it.type.simpleName}" /><%}else{%>
    <lay:showElement value="\${${propertyName}?.${it.name}}" type="${it.type.simpleName}" label="\${message(code:'${propertyName}.${it.name}.label',default:'${it.name}')}" /><%}%><%}%><%}%>
</lay:showWidget>
<el:row />

</body>
</html>