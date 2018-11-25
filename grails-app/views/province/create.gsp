<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'province.entity', default: 'Province List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Province List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'province', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="provinceForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="province" action="save">
                <g:render template="/province/form" model="[province: province]"/>
                <el:formButton functionName="save" withPreviousLink="true" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
