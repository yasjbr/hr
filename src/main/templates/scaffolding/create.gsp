<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="\${message(code: '${propertyName}.entity', default: '${className} List')}" />
    <g:set var="title" value="\${message(code: 'default.create.label',args:[entity], default: '${className} List')}" />
    <title>\${title}</title>
</head>
<body>
<lay:widget title="\${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='\${createLink(controller:'${propertyName}',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="${propertyName}Form" callLoadingFunction="performPostActionWithEncodedId" controller="${propertyName}" action="save">
                <g:render template="/${propertyName}/form" model="[${propertyName}:${propertyName}]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
