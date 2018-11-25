<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionList.entity', default: 'SuspensionList List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'SuspensionList List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'suspensionList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="suspensionListForm" controller="suspensionList" action="update">
                <g:render template="/suspensionList/form" model="[suspensionList: suspensionList]"/>
                <el:hiddenField name="id" value="${suspensionList?.id}"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>