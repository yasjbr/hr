<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'externalTransferList.entity', default: 'AllowanceList List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'AllowanceList List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="externalTransferListForm" controller="externalTransferList" action="update">
                <g:render template="/externalTransferList/form" model="[externalTransferList: externalTransferList]"/>
                <el:hiddenField name="id" value="${externalTransferList?.id}"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>