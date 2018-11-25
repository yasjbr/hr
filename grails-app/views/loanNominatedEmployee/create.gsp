<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanNominatedEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanNominatedEmployeeForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanNominatedEmployee" action="save">
                <g:render template="/loanNominatedEmployee/form" model="[loanNominatedEmployee:loanNominatedEmployee]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
