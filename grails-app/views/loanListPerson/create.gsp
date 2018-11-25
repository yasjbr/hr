<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanListPerson.entity', default: 'LoanListPerson List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanListPerson List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanListPerson',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanListPersonForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanListPerson" action="save">
                <g:render template="/loanListPerson/form" model="[loanListPerson:loanListPerson]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
