<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanRequest.entity', default: 'LoanRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanRequest" action="save">
                <g:render template="/loanRequest/form" model="[loanRequest:loanRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" />

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
