<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'loanNotice.entity', default: 'LoanNotice List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'LoanNotice List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'loanNotice',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="loanNoticeForm" controller="loanNotice" action="update">
                <el:hiddenField name="id" value="${loanNotice?.id}"/>
                <g:render template="/loanNotice/form" model="[loanNotice:loanNotice]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>