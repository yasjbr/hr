<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentList.entity', default: 'RecruitmentList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'RecruitmentList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="recruitmentListForm" controller="recruitmentList" action="update">
                <g:render template="/recruitmentList/form" model="[recruitmentList:recruitmentList]"/>
                <el:hiddenField name="encodedId" value="${recruitmentList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>