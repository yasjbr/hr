<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployeeNote.entity', default: 'LoanListPersonNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanListPersonNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanNominatedEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanNominatedEmployeeNote" action="save">
                <g:render template="/loanNominatedEmployeeNote/form" model="[loanNominatedEmployeeNote:loanNominatedEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
