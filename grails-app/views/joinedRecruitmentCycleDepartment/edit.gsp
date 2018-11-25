<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'JoinedRecruitmentCycleDepartment List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'joinedRecruitmentCycleDepartment',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="joinedRecruitmentCycleDepartmentForm" controller="joinedRecruitmentCycleDepartment" action="update">
                <el:hiddenField name="id" value="${joinedRecruitmentCycleDepartment?.id}" />
                <g:render template="/joinedRecruitmentCycleDepartment/form" model="[joinedRecruitmentCycleDepartment:joinedRecruitmentCycleDepartment]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>