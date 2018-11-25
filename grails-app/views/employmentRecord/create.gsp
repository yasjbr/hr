<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employmentRecord.entity', default: 'EmploymentRecord List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmploymentRecord List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employmentRecord',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employmentRecordForm" callLoadingFunction="performPostActionWithEncodedId" controller="employmentRecord" action="save">
                <g:render template="/employmentRecord/form" model="[employmentRecord:employmentRecord]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
