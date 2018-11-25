<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployee.entity', default: 'RecruitmentListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'RecruitmentListEmployee List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="recruitmentListEmployeeForm" controller="recruitmentListEmployee" action="update">
                <g:render template="/recruitmentListEmployee/form" model="[recruitmentListEmployee:recruitmentListEmployee]"/>
                <el:hiddenField name="id" value="${recruitmentListEmployee?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>