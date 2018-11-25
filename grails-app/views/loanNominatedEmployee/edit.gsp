<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'LoanNominatedEmployee List')}" />
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
            <el:validatableForm name="loanNominatedEmployeeForm" controller="loanNominatedEmployee" action="update">
                <g:render template="/loanNominatedEmployee/form" model="[loanNominatedEmployee:loanNominatedEmployee]"/>
                <el:hiddenField name="id" value="${loanNominatedEmployee?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>