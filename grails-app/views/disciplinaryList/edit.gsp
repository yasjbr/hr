<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryList.entity', default: 'LoanList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'LoanList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="disciplinaryListForm" controller="disciplinaryList" action="update">
                <g:render template="/disciplinaryList/form" model="[disciplinaryList:disciplinaryList]"/>
                <el:hiddenField name="encodedId" value="${disciplinaryList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>