<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanRequestRelatedPerson List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanRequestRelatedPerson',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanRequestRelatedPersonForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanRequestRelatedPerson" action="save">
                <g:render template="/loanRequestRelatedPerson/form" model="[loanRequestRelatedPerson:loanRequestRelatedPerson]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
