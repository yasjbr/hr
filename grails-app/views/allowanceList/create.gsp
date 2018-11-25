<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceList.entity', default: 'AllowanceList List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'AllowanceList List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'allowanceList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="allowanceListForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="allowanceList" action="save">
                <g:render template="/allowanceList/form" model="[allowanceList: allowanceList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
