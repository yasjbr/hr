<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'provinceLocation.entity', default: 'ProvinceLocation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ProvinceLocation List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'provinceLocation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="provinceLocationForm" callLoadingFunction="performPostActionWithEncodedId" controller="provinceLocation" action="save">
                <g:render template="/provinceLocation/form" model="[provinceLocation:provinceLocation]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
