<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacationList.entity', default: 'VacationList List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'VacationList List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'vacationList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="vacationListForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="vacationList" action="save">
                <g:render template="/vacationList/form" model="[vacationList: vacationList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'vacationList', action: 'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
